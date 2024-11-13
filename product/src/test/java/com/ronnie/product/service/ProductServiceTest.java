package com.ronnie.product.service;

import com.ronnie.product.api.dto.CreateProductReq;
import com.ronnie.product.api.dto.ProductDTO;
import com.ronnie.product.api.dto.UpdateProductReq;
import com.ronnie.product.exception.ProductNotExistException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class ProductServiceTest {
    @Autowired
    private ProductService productService;

    @Test
    void createProduct_shouldSucced() {
        ProductDTO result = getCreateProduct("testName", "3.33");

        List<ProductDTO> products = productService.findProductsByIds(List.of(result.getId()));

        assertThat(products.size()).isEqualTo(1);
        ProductDTO productDTO = products.get(0);
        assertThat(productDTO.getName()).isEqualTo(result.getName());
        assertThat(productDTO.getPrice()).isEqualTo(result.getPrice());
    }

    private ProductDTO getCreateProduct(String productName, String price) {
        BigDecimal productPrice = new BigDecimal(price);
        ProductDTO result = productService.createProduct(new CreateProductReq(productName, productPrice));
        return result;
    }


    @Test
    void updateProduct_shouldSucceed() {
        String productName = "testName1";
        BigDecimal productPrice = new BigDecimal("3.34");
        ProductDTO result = productService.createProduct(new CreateProductReq(productName, productPrice));

        String updateProductName = "testName2";
        BigDecimal updateProductPrice = new BigDecimal("4.44");
        productService.updateProduct(new UpdateProductReq(result.getId(), updateProductName, updateProductPrice));

        List<ProductDTO> products = productService.findProductsByIds(List.of(result.getId()));

        assertThat(products.size()).isEqualTo(1);
        ProductDTO productDTO = products.get(0);
        assertThat(productDTO.getName()).isEqualTo(updateProductName);
        assertThat(productDTO.getPrice()).isEqualTo(updateProductPrice);
    }

    @Test
    void listProducts() {
        ProductDTO listProducts1 = getCreateProduct("listProducts1", "1.23");
        ProductDTO listProducts2 = getCreateProduct("listProducts2", "2.34");

        List<ProductDTO> productDTOS = productService.listProducts();
        assertThat(productDTOS).containsAll(List.of(listProducts1, listProducts2));
    }


    @Test
    void deleteById() {
        ProductDTO listProducts1 = getCreateProduct("deleteById1", "1.23");

        ProductDTO productDTO = productService.findById(listProducts1.getId());
        assertThat(productDTO.getId()).isEqualTo(listProducts1.getId());

        productService.deleteById(listProducts1.getId());

        assertThrows(ProductNotExistException.class, () -> {
            productService.findById(listProducts1.getId());

        });
    }

}