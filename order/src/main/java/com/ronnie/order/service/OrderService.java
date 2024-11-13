package com.ronnie.order.service;

import com.ronnie.common.exception.UnknownException;
import com.ronnie.order.api.dto.OrderDetailResDTO;
import com.ronnie.order.api.dto.OrderLineItemDTO;
import com.ronnie.order.api.dto.PlaceOrderReqDTO;
import com.ronnie.order.api.dto.UpdateOrderReqDTO;
import com.ronnie.order.constants.OrderChangeTransactionStatus;
import com.ronnie.order.constants.OrderStatus;
import com.ronnie.order.exception.DuplicateOrderLineItemsException;
import com.ronnie.order.exception.InvalidProductException;
import com.ronnie.order.model.Order;
import com.ronnie.order.model.OrderChangeTransaction;
import com.ronnie.order.model.OrderLineItem;
import com.ronnie.order.remote.BatchUpdateInventoryReqDTO;
import com.ronnie.order.remote.ChangeInventoryDTO;
import com.ronnie.order.remote.ProductDTO;
import com.ronnie.order.remote.ProductRemoteService;
import com.ronnie.order.repo.OrderChangeTransactionRepository;
import com.ronnie.order.repo.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.StreamSupport.stream;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRemoteService productRemoteService;
    private final OrderChangeTransactionRepository transactionRepository;

    public OrderService(OrderRepository orderRepository, ProductRemoteService productRemoteService, OrderChangeTransactionRepository transactionRepository) {
        this.orderRepository = orderRepository;
        this.productRemoteService = productRemoteService;
        this.transactionRepository = transactionRepository;
    }

    public OrderDetailResDTO updateOrder(UpdateOrderReqDTO reqDTO) {
        //check order's existence
//        orderRepository.findById(reqDTO.getOrderId()).map(order -> {
//           //generate diff
//            Map<Integer, OrderLineItem> itemMap = order.getLineItems().stream().collect(Collectors.toMap(OrderLineItem::getProductId, Function.identity()));
//            reqDTO.getLineItems().stream().map(item->{
//                OrderLineItem oldItem = itemMap.get(item.getQuantity());
//                if(oldItem != null) {
//                    new OrderLineItem(item.getQuantity()-oldItem.getQuantity())
//                }
//            })
//
//        });

        return null;
    }


    public OrderDetailResDTO placeOrder(PlaceOrderReqDTO placeOrderReq) {
        //check if products are valid
        Set<Integer> productIds = validatePlaceOrder(placeOrderReq);
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
        transactionRepository.save(new OrderChangeTransaction(UUID.randomUUID().toString(), savedOrder.getId(), OrderChangeTransactionStatus.PENDING, null));

        productRemoteService.decreaseInventory(
                BatchUpdateInventoryReqDTO.builder()
                        .requestId(requestId(savedOrder))
                        .decreaseInventories(
                                lineItems.stream().map(l -> new ChangeInventoryDTO(l.getProductId(), l.getQuantity())).collect(Collectors.toList()))
                        .build()
        );

        boolean updated = orderRepository.updateStatusById(savedOrder.getId(), OrderStatus.PLACED);
        if (!updated) {
            log.error("order status update failed! req = {}, orderId = {}", placeOrderReq, savedOrder.getId());
            throw new UnknownException();
        }
        return mapOrderToOrderDTO(savedOrder, OrderStatus.PLACED);
    }

    public static Set<Integer> validatePlaceOrder(PlaceOrderReqDTO placeOrderReq) {
        Set<Integer> productIds = placeOrderReq.getLineItems().stream().map(item -> item.getProductId()).collect(Collectors.toSet());
        if (productIds.size() != placeOrderReq.getLineItems().size()) {
            //duplicated lineItems!!!
            log.warn("duplicated line items for place order, req = {}", placeOrderReq);
            throw new DuplicateOrderLineItemsException();
        }
        return productIds;
    }

    public static Set<Integer> validateUpdateOrder(UpdateOrderReqDTO updateOrderReqDTO) {
        List<OrderLineItemDTO> lineItems = updateOrderReqDTO.getLineItems();
        Set<Integer> productIds = lineItems.stream().map(item -> item.getProductId()).collect(Collectors.toSet());
        if (productIds.size() != lineItems.size()) {
            //duplicated lineItems!!!
            log.warn("duplicated line items for place order, req = {}", updateOrderReqDTO);
            throw new DuplicateOrderLineItemsException();
        }
        return productIds;
    }

    private static String requestId(Order savedOrder) {
        return "order_" + savedOrder.getId();
    }

    public OrderDetailResDTO orderDetail(String id) {
        return orderRepository.findById(id).map((Order order) -> mapOrderToOrderDTO(order, order.getStatus()))
                .orElseThrow();
    }

    public static OrderDetailResDTO mapOrderToOrderDTO(Order order, OrderStatus status) {
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
        return orderRepository.findAllByUserId(userId).stream().map(o -> mapOrderToOrderDTO(o, o.getStatus())).collect(Collectors.toList());
    }


    public OrderChangeTransaction startPlace(PlaceOrderReqDTO placeOrderReq, String orderId, String requestId) {
        OrderChangeTransaction save = transactionRepository.save(OrderChangeTransaction.builder().id(requestId).orderId(orderId).status(OrderChangeTransactionStatus.PENDING).build());
        System.out.println("save id = " + save.getId());
        return save;
    }

    @Transactional
    public Order finishPlaceOrder(PlaceOrderReqDTO placeOrderReq, String orderId, OrderChangeTransaction transaction) {
        //persistent
        Set<OrderLineItem> lineItems = placeOrderReq.getLineItems().stream()
                .map(dto -> new OrderLineItem(null, dto.getProductId(), dto.getQuantity()))
                .collect(Collectors.toSet());
        Order order = Order.builder().id(orderId).userId(placeOrderReq.getUserId()).status(OrderStatus.PLACED).lineItems(lineItems).build();
        Order save = orderRepository.save(order);
        transaction.setStatus(OrderChangeTransactionStatus.FINISHED);
        transactionRepository.save(transaction);
        return save;
    }

    public void cancelPlaceOrder() {

    }

    public Order findById(String orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public OrderChangeTransaction startUpdateOrder(UpdateOrderReqDTO reqDTO, String requestId) {
        return transactionRepository.save(new OrderChangeTransaction(requestId, reqDTO.getOrderId(), OrderChangeTransactionStatus.PENDING, null));
    }

    public void finishUpdateOrder(UpdateOrderReqDTO placeOrderReq, OrderChangeTransaction transaction) {
        Order oldOrder = findById(placeOrderReq.getOrderId());
        //persistent
        Set<OrderLineItem> lineItems = placeOrderReq.getLineItems().stream()
                .map(dto -> new OrderLineItem(null, dto.getProductId(), dto.getQuantity()))
                .collect(Collectors.toSet());
        Order order = Order.builder().id(placeOrderReq.getOrderId()).userId(placeOrderReq.getUserId()).status(OrderStatus.PLACED).lineItems(lineItems).version(oldOrder.getVersion()).build();
        Order save = orderRepository.save(order);
        transaction.setStatus(OrderChangeTransactionStatus.FINISHED);
        transactionRepository.save(transaction);
    }
}
