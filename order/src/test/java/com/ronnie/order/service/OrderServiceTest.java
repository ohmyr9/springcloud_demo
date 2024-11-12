package com.ronnie.order.service;

import com.ronnie.order.api.dto.OrderDetailResDTO;
import com.ronnie.order.api.dto.OrderLineItemDTO;
import com.ronnie.order.api.dto.PlaceOrderReqDTO;
import com.ronnie.order.constants.OrderStatus;
import com.ronnie.order.exception.DuplicateOrderLineItemsException;
import com.ronnie.order.exception.InvalidProductException;
import com.ronnie.order.remote.ProductDTO;
import com.ronnie.order.remote.ProductRemoteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @MockBean
    private ProductRemoteService productRemoteService;

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
            orderService.placeOrder(placeOrderReq);
        });
    }

    @Test
    public void placeOrder_usingInvalidProducts_shouldThrow_InvalidProductsException() {
        when(productRemoteService.findProductsByIds(any())).thenReturn(List.of());
        int userId = 123;
        PlaceOrderReqDTO placeOrderReq = PlaceOrderReqDTO
                .builder()
                .userId(userId)
                .lineItems(List.of(
                        new OrderLineItemDTO(1, 3),
                        new OrderLineItemDTO(2, 2)))
                .build();
        assertThrows(InvalidProductException.class, () -> {
            orderService.placeOrder(placeOrderReq);
        });
    }

    @Test
    public void placeOrder_shouldSucceed() {
        when(productRemoteService.findProductsByIds(any()))
                .thenReturn(List.of(
                        new ProductDTO(1, "", BigDecimal.ONE),
                        new ProductDTO(2, "", BigDecimal.ONE)));
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
        OrderDetailResDTO orderDetailResDTO = orderService.placeOrder(placeOrderReq);

        OrderDetailResDTO savedOrder = orderService.orderDetail(orderDetailResDTO.getOrderId());
        assertThat(savedOrder.getOrderStatus()).isEqualTo(OrderStatus.PLACED);
        assertThat(savedOrder.getUserId()).isEqualTo(userId);
        assertThat(savedOrder.getLineItems()).contains(itemOne, itemTwo);
    }

    @Test
    public void listOrdersShouldSucceed() {
        when(productRemoteService.findProductsByIds(any()))
                .thenReturn(List.of(
                        new ProductDTO(1, "", new BigDecimal(5)),
                        new ProductDTO(2, "", new BigDecimal(10))));
        doNothing().when(productRemoteService).decreaseInventory(any());
        int userId = 12345;
        OrderLineItemDTO itemOne = new OrderLineItemDTO(1, 3);
        OrderLineItemDTO itemTwo = new OrderLineItemDTO(2, 2);

        PlaceOrderReqDTO placeOrderReq = PlaceOrderReqDTO
                .builder()
                .userId(userId)
                .lineItems(List.of(
                        itemOne,
                        itemTwo))
                .build();
        OrderDetailResDTO orderDetailResDTO = orderService.placeOrder(placeOrderReq);

        List<OrderDetailResDTO> orderDetailResDTOS = orderService.listOrders(userId);
        assertThat(orderDetailResDTOS).hasSize(1);
        OrderDetailResDTO actual = orderDetailResDTOS.get(0);
        assertThat(actual.getLineItems()).containsAll(orderDetailResDTO.getLineItems());
        assertThat(actual.getOrderStatus()).isEqualTo(orderDetailResDTO.getOrderStatus());
        assertThat(actual.getUserId()).isEqualTo(orderDetailResDTO.getUserId());
    }

}