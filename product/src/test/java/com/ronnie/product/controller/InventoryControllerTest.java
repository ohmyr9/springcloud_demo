package com.ronnie.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronnie.common.api.Result;
import com.ronnie.product.api.dto.*;
import com.ronnie.product.service.InventoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private InventoryService service;

    @Test
    void decreaseInventory_shouldSucceed() throws Exception {

        doNothing().when(service).decreaseInventory(any());
        ObjectMapper mapper = new ObjectMapper();
        BatchUpdateInventoryReqDTO input = BatchUpdateInventoryReqDTO.builder().requestId("123").decreaseInventories(List.of(new DecreaseInventory(2, 3))).build();
        mvc.perform(
                        post("/v1/inventories")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(mapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Result.success())));
    }

    @Test
    void changeInventory_shouldSucceed() throws Exception {
        int quantity = 10;
        ChangeInventoryReqDTO changeRequest = ChangeInventoryReqDTO.builder().quantity(quantity).build();
        Integer productId = 123;
        InventoryDTO expect = new InventoryDTO(productId, quantity);
        when(service.changeInventory(any())).thenReturn(expect);
        ObjectMapper mapper = new ObjectMapper();
        mvc.perform(
                        put("/v1/inventories/" + productId)
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(mapper.writeValueAsString(changeRequest))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(Result.success(expect))));
    }

    @Test
    void listInventory_shouldSucceed() throws Exception {

        List<InventoryDetailDTO> results = List.of(
                new InventoryDetailDTO(1, "test", BigDecimal.TWO, 91),
                new InventoryDetailDTO(2, "test2", BigDecimal.TEN, 92)
        );
        ObjectMapper objectMapper = new ObjectMapper();

        when(service.listAll()).thenReturn(results);

        mvc.perform(get("/v1/inventories"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Result.success(results))));
    }
}