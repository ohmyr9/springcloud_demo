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



//    @Test
//    public void placeOrder_usingInvalidProducts_shouldThrow_InvalidProductsException() {
//        when(productRemoteService.findProductsByIds(any())).thenReturn(List.of());
//        int userId = 123;
//        PlaceOrderReqDTO placeOrderReq = PlaceOrderReqDTO
//                .builder()
//                .userId(userId)
//                .lineItems(List.of(
//                        new OrderLineItemDTO(1, 3),
//                        new OrderLineItemDTO(2, 2)))
//                .build();
//        assertThrows(InvalidProductException.class, () -> {
//            orderService.placeOrder(placeOrderReq);
//        });
//    }

}