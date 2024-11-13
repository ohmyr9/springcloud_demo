package com.ronnie.order.service;

import com.ronnie.order.api.dto.OrderDetailResDTO;
import com.ronnie.order.api.dto.OrderLineItemDTO;
import com.ronnie.order.api.dto.PlaceOrderReqDTO;
import com.ronnie.order.api.dto.UpdateOrderReqDTO;
import com.ronnie.order.constants.OrderStatus;
import com.ronnie.order.exception.DuplicateOrderLineItemsException;
import com.ronnie.order.remote.ProductRemoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

@SpringBootTest
class OrderChangeCoordinatorTest {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderChangeCoordinator orderChangeCoordinator;
    @MockBean
    private ProductRemoteService productRemoteService;

    @Test
    void placeOrder_shoudSucced() {

        doNothing().when(productRemoteService).decreaseInventory(any());
        int userId = 123;
        OrderLineItemDTO itemOne = new OrderLineItemDTO(1, 3);
        OrderLineItemDTO itemTwo = new OrderLineItemDTO(2, 2);

        PlaceOrderReqDTO placeOrderReq = PlaceOrderReqDTO
                .builder()
                .userId(userId)
                .lineItems(List.of(
                        itemOne,
                        itemTwo))
                .build();
        OrderDetailResDTO orderDetailResDTO = orderChangeCoordinator.placeOrder(placeOrderReq);

        OrderDetailResDTO savedOrder = orderService.orderDetail(orderDetailResDTO.getOrderId());
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(savedOrder.getUserId()).isEqualTo(userId);
        assertThat(savedOrder.getLineItems()).contains(itemOne, itemTwo);
    }

    @Test
    void updateOrder_shoudSucced() {

        doNothing().when(productRemoteService).decreaseInventory(any());
        int userId = 123;
        //place order
        OrderLineItemDTO itemOne = new OrderLineItemDTO(1, 3);
        OrderLineItemDTO itemTwo = new OrderLineItemDTO(2, 2);

        PlaceOrderReqDTO placeOrderReq = PlaceOrderReqDTO
                .builder()
                .userId(userId)
                .lineItems(List.of(
                        itemOne,
                        itemTwo))
                .build();
        OrderDetailResDTO orderDetailResDTO = orderChangeCoordinator.placeOrder(placeOrderReq);


        OrderLineItemDTO itemThree = new OrderLineItemDTO(1, 4);
        OrderLineItemDTO itemFour = new OrderLineItemDTO(2, 5);

        String orderId = orderDetailResDTO.getOrderId();
        UpdateOrderReqDTO updateOrderReqDTO = UpdateOrderReqDTO
                .builder()
                .orderId(orderId)
                .userId(userId)
                .lineItems(List.of(
                        itemThree,
                        itemFour))
                .build();
        OrderDetailResDTO updatedOrder = orderChangeCoordinator.updateOrder(updateOrderReqDTO);

        OrderDetailResDTO savedOrder = orderService.orderDetail(orderDetailResDTO.getOrderId());
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(savedOrder.getUserId()).isEqualTo(userId);
        assertThat(savedOrder.getLineItems()).contains(itemThree, itemFour);
    }

    @Test
    public void placeOrder_UsingDuplicateOrderLineItems_ShouldThrow_DuplicateOrderLineItemsException() {
        int userId = 123;
        int productId = 1;
        PlaceOrderReqDTO placeOrderReq = PlaceOrderReqDTO
                .builder()
                .userId(userId)
                .lineItems(List.of(
                        new OrderLineItemDTO(productId, 3),
                        new OrderLineItemDTO(productId, 2)))
                .build();
        DuplicateOrderLineItemsException exception = assertThrows(DuplicateOrderLineItemsException.class, () -> {
            orderChangeCoordinator.placeOrder(placeOrderReq);
        });
    }
}