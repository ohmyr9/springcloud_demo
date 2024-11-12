package com.ronnie.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronnie.common.api.Result;
import com.ronnie.order.api.dto.OrderDetailResDTO;
import com.ronnie.order.api.dto.OrderLineItemDTO;
import com.ronnie.order.api.dto.PlaceOrderReqDTO;
import com.ronnie.order.config.RestTemplateConfig;
import com.ronnie.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(RestTemplateConfig.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private OrderService service;

    @Test
    public void placeOrder() throws Exception {
        int orderId = 1;
        OrderDetailResDTO order = OrderDetailResDTO.builder().
                orderId(orderId)
                .build();

        when(service.placeOrder(any())).thenReturn(order);

        ObjectMapper mapper = new ObjectMapper();
        Integer userId = 1;
        String inputJson = mapper.writeValueAsString(new PlaceOrderReqDTO(userId, List.of(new OrderLineItemDTO(1, 10))));
        mvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Result.success(order))));
    }

}