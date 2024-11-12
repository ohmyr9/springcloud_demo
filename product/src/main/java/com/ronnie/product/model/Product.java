package com.ronnie.product.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
@Table(name = "PRODUCT")
public final class Product {
    @Id
    private final Integer id;
    private final String name;
    private final BigDecimal price;
}
