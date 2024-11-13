package com.ronnie.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronnie.common.api.Result;
import com.ronnie.product.api.dto.CreateProductReq;
import com.ronnie.product.api.dto.ProductDTO;
import com.ronnie.product.api.dto.UpdateProductReq;
import com.ronnie.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.internal.stubbing.answers.DoesNothing;
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
        mvc.perform(get("/v1/products").param("ids", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"data\": [{\"id\":1,\"name\":\"name\",\"price\":1}]}"));

    }

    @Test
    void createProduc_shouldSucced() throws Exception {
        String productName = "createProductTest";
        BigDecimal price = BigDecimal.TEN;
        CreateProductReq createProductTest = CreateProductReq.builder().name(productName).price(price).build();

        ProductDTO result = new ProductDTO(1, productName, price);
        when(service.createProduct(any())).thenReturn(result);
        ObjectMapper mapper = new ObjectMapper();


        Result<ProductDTO> expected = Result.success(result);

        mvc.perform(
                        post("/v1/products")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(mapper.writeValueAsString(createProductTest))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @Test
    void updateProduct_shouldSucced() throws Exception {
        String productName = "createProductTest";
        BigDecimal price = BigDecimal.TEN;
        UpdateProductReq createProductTest = UpdateProductReq.builder().name(productName).price(price).build();

        doNothing().when(service).updateProduct(any());
        ObjectMapper mapper = new ObjectMapper();


        Result<Void> expected = Result.success();

        mvc.perform(
                        put("/v1/products")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(mapper.writeValueAsString(createProductTest))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

    }
}