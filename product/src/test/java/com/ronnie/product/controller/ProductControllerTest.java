package com.ronnie.product.controller;

import com.ronnie.product.api.dto.ProductDTO;
import com.ronnie.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ProductService service;
    @Test
    void findProductsByIds() throws Exception {
        List<ProductDTO> products = List.of(new ProductDTO(1, "name", BigDecimal.ONE));
        when(service.findProductsByIds(any())).thenReturn(products);
        mvc.perform(get("/v1/products").param("ids","1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"data\": [{\"id\":1,\"name\":\"name\",\"price\":1}]}"));

    }
}