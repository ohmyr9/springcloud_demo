package com.ronnie.product.cdc;

import com.ronnie.product.ProductApplication;
import com.ronnie.product.api.dto.ProductDTO;
import com.ronnie.product.controller.ProductController;
import com.ronnie.product.service.ProductService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

@SpringBootTest(classes = ProductApplication.class)
public abstract class BaseTestClass {
    @Autowired
    ProductController productController;
    @MockBean
    ProductService productService;

    @BeforeEach
    public void setup() {
        RestAssuredMockMvc.standaloneSetup(productController);
        //mock get products by id
        Mockito.when(productService.findProductsByIds(argThat(integers -> integers!=null && !integers.isEmpty())))
                .thenReturn(
                        List.of(
                                new ProductDTO(1, "phone", new BigDecimal("333.33")),
                                new ProductDTO(2, "watch", new BigDecimal("222.22"))
                        ));
    }
}
