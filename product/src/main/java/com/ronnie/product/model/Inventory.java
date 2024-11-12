package com.ronnie.product.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table
public class Inventory {
    @Id
    private final Integer id;
    private final Integer productId;
    private final Integer quantity;
    @Version
    private final Integer version;
}
