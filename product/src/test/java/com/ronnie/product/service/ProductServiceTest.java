package com.ronnie.product.service;

import com.ronnie.product.api.dto.CreateProductReq;
import com.ronnie.product.api.dto.ProductDTO;
import com.ronnie.product.api.dto.UpdateProductReq;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ProductServiceTest {
    @Autowired
    private ProductService productService;
    @Test
    void createProduct() {
        String productName = "testName";
        BigDecimal productPrice = new BigDecimal("3.33");
        Integer productId = productService.createProduct(new CreateProductReq(productName, productPrice));

        List<ProductDTO> products = productService.findProductsByIds(List.of(productId));

        assertThat(products.size()).isEqualTo(1);
        ProductDTO productDTO = products.get(0);
        assertThat(productDTO.getName()).isEqualTo(productName);
        assertThat(productDTO.getPrice()).isEqualTo(productPrice);
    }

    @Test
    void updateProduct() {
        String productName = "testName1";
        BigDecimal productPrice = new BigDecimal("3.34");
        Integer productId = productService.createProduct(new CreateProductReq(productName, productPrice));

        String updateProductName = "testName2";
        BigDecimal updateProductPrice = new BigDecimal("4.44");
        productService.updateProduct(new UpdateProductReq(productId,updateProductName , updateProductPrice));

        List<ProductDTO> products = productService.findProductsByIds(List.of(productId));

        assertThat(products.size()).isEqualTo(1);
        ProductDTO productDTO = products.get(0);
        assertThat(productDTO.getName()).isEqualTo(updateProductName);
        assertThat(productDTO.getPrice()).isEqualTo(updateProductPrice);
    }

}