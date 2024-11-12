package com.ronnie.order.service;

import com.ronnie.common.api.Result;
import com.ronnie.common.exception.UnknownException;
import com.ronnie.order.api.dto.OrderDetailResDTO;
import com.ronnie.order.api.dto.OrderLineItemDTO;
import com.ronnie.order.api.dto.PlaceOrderReqDTO;
import com.ronnie.order.constants.OrderStatus;
import com.ronnie.order.exception.DuplicateOrderLineItemsException;
import com.ronnie.order.exception.InvalidProductException;
import com.ronnie.order.model.Order;
import com.ronnie.order.model.OrderLineItem;
import com.ronnie.order.remote.BatchDecreaseInventoryReqDTO;
import com.ronnie.order.remote.DecreaseInventoryDTO;
import com.ronnie.order.remote.ProductDTO;
import com.ronnie.order.remote.ProductRemoteService;
import com.ronnie.order.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRemoteService productRemoteService;

    public OrderService(OrderRepository orderRepository, ProductRemoteService productRemoteService) {
        this.orderRepository = orderRepository;
        this.productRemoteService = productRemoteService;
    }

    public OrderDetailResDTO placeOrder(PlaceOrderReqDTO placeOrderReq) {
        //check products are valid
        Set<Integer> productIds = placeOrderReq.getLineItems().stream().map(item -> item.getProductId()).collect(Collectors.toSet());
        if (productIds.size() != placeOrderReq.getLineItems().size()) {
            //duplicated lineItems!!!
            log.warn("duplicated line items for place order, req = {}", placeOrderReq);
            throw new DuplicateOrderLineItemsException();
        }
        Iterable<ProductDTO> exitedProducts = productRemoteService.findProductsByIds(productIds);
        Set<Integer> exitedProductIds =
                stream(exitedProducts.spliterator(), false).map(p -> p.getId()).collect(Collectors.toSet());
        if (!exitedProductIds.containsAll(productIds)) {
            //request lineItems contains invalid product_id
            productIds.removeAll(exitedProductIds);
            log.warn("invalid products of line items found!, req = {}, invalid products = {}", placeOrderReq, productIds);
            throw new InvalidProductException();
        }

        //persistent
        Set<OrderLineItem> lineItems = placeOrderReq.getLineItems().stream()
                .map(dto -> new OrderLineItem(null, dto.getProductId(), dto.getQuantity()))
                .collect(Collectors.toSet());
        Order order = Order.builder().userId(placeOrderReq.getUserId()).status(OrderStatus.DRAFT).lineItems(lineItems).build();
        Order savedOrder = orderRepository.save(order);

        productRemoteService.decreaseInventory(
                BatchDecreaseInventoryReqDTO.builder()
                        .requestId(requestId(savedOrder))
                        .decreaseInventories(
                                lineItems.stream().map(l -> new DecreaseInventoryDTO(l.getProductId(), l.getQuantity())).collect(Collectors.toList()))
                        .build()
        );

        boolean updated = orderRepository.updateStatusById(savedOrder.getId(), OrderStatus.PLACED);
        if (!updated) {
            log.error("order status update failed! req = {}, orderId = {}", placeOrderReq, savedOrder.getId());
            throw new UnknownException();
        }
        return mapOrderToOrderDTO(savedOrder, OrderStatus.PLACED);
    }

    private static String requestId(Order savedOrder) {
        return "order_" + savedOrder.getId();
    }

    public OrderDetailResDTO orderDetail(Integer id) {
        return orderRepository.findById(id).map((Order order) -> mapOrderToOrderDTO(order, order.getStatus()))
                .orElseThrow();
    }

    private static OrderDetailResDTO mapOrderToOrderDTO(Order order, OrderStatus status) {
        return new OrderDetailResDTO(
                order.getId(),
                order.getUserId(),
                status,
                order.getLineItems()
                        .stream()
                        .map(itemDO -> new OrderLineItemDTO(itemDO.getProductId(), itemDO.getQuantity()))
                        .toList()

        );
    }

    public List<OrderDetailResDTO> listOrders(Integer userId) {
        return orderRepository.findAllByUserId(userId).stream().map(o->mapOrderToOrderDTO(o,o.getStatus())).collect(Collectors.toList());
    }
}
