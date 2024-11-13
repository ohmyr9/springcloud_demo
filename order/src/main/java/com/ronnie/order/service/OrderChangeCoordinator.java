package com.ronnie.order.service;

import com.ronnie.common.exception.UnknownException;
import com.ronnie.order.api.dto.OrderDetailResDTO;
import com.ronnie.order.api.dto.PlaceOrderReqDTO;
import com.ronnie.order.api.dto.UpdateOrderReqDTO;
import com.ronnie.order.model.Order;
import com.ronnie.order.model.OrderChangeTransaction;
import com.ronnie.order.model.OrderLineItem;
import com.ronnie.order.remote.BatchUpdateInventoryReqDTO;
import com.ronnie.order.remote.ChangeInventoryDTO;
import com.ronnie.order.remote.ProductRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ronnie.order.service.OrderService.mapOrderToOrderDTO;
@Slf4j
@Service
public class OrderChangeCoordinator {
    private final OrderService orderService;
    private final ProductRemoteService productRemoteService;

    public OrderChangeCoordinator(OrderService orderService, ProductRemoteService productRemoteService) {
        this.orderService = orderService;
        this.productRemoteService = productRemoteService;
    }

    public OrderDetailResDTO placeOrder(PlaceOrderReqDTO placeOrderReq) {
        OrderService.validatePlaceOrder(placeOrderReq);
        //validate input
        try {
            String orderId = UUID.randomUUID().toString();
            String requestId = UUID.randomUUID().toString();
            OrderChangeTransaction transaction = orderService.startPlace(placeOrderReq, orderId, requestId);
            Set<OrderLineItem> lineItems = placeOrderReq.getLineItems().stream()
                    .map(dto -> new OrderLineItem(null, dto.getProductId(), dto.getQuantity()))
                    .collect(Collectors.toSet());
            productRemoteService.decreaseInventory(
                    BatchUpdateInventoryReqDTO.builder()
                            .requestId(requestId)
                            .decreaseInventories(
                                    lineItems.stream().map(l -> new ChangeInventoryDTO(l.getProductId(), l.getQuantity())).collect(Collectors.toList()))
                            .build()
            );
            Order order = orderService.finishPlaceOrder(placeOrderReq, orderId, transaction);
            return mapOrderToOrderDTO(order, order.getStatus());
        } catch (RuntimeException e) {
            //todo 1. async compensate all the states changed in this distrubted transaction
            //async before canceling order placing, reverse the change of inventory first
            //orderService.cancelPlaceOrder();
            //2. error handling
            log.error(e.getMessage(), e);
            throw new UnknownException();
        }
    }

    public OrderDetailResDTO updateOrder(UpdateOrderReqDTO reqDTO) {
        //validate input
        OrderService.validateUpdateOrder(reqDTO);
        try {
            String requestId = UUID.randomUUID().toString();
            String orderId = reqDTO.getOrderId();
            Order order = orderService.findById(orderId);

            OrderChangeTransaction transaction = orderService.startUpdateOrder(reqDTO, requestId);
            Map<Integer, OrderLineItem> itemMap = order.getLineItems().stream().collect(Collectors.toMap(OrderLineItem::getProductId, Function.identity()));
            productRemoteService.decreaseInventory(
                    createBatchUpateReq(reqDTO, requestId, itemMap)
            );
            orderService.finishUpdateOrder(reqDTO, transaction);
            return mapOrderToOrderDTO(order, order.getStatus());
        } catch (RuntimeException e) {
            //todo 1. async compensate all the states changed in this distrubted transaction
            //async before canceling order updating, reverse the change of inventory first
            //2. error handling
            //orderService.cancelUpdateOrder();
            log.error(e.getMessage(), e);
            throw new UnknownException();
        }


    }

    private static BatchUpdateInventoryReqDTO createBatchUpateReq(UpdateOrderReqDTO reqDTO, String requestId, Map<Integer, OrderLineItem> itemMap) {
        return BatchUpdateInventoryReqDTO.builder()
                .requestId(requestId)
                .decreaseInventories(
                        reqDTO.getLineItems().stream().map(l -> {
                            OrderLineItem item = itemMap.get(l.getProductId());
                            if (item == null) {
                                return new ChangeInventoryDTO(l.getProductId(), l.getQuantity());
                            } else {
                                return new ChangeInventoryDTO(l.getProductId(), l.getQuantity() - item.getQuantity());
                            }
                        }).collect(Collectors.toList())
                )
                .build();
    }
}