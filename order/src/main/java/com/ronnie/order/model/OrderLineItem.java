package com.ronnie.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@Builder
@Table
public final class OrderLineItem {
    @Id
    private final Integer id;
    private final Integer productId;
    private final Integer quantity;
}
