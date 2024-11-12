package com.ronnie.product.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductDTO {
    private Integer id;
    private String name;
    private BigDecimal price;
}
