package com.ronnie.order.model;

import com.ronnie.order.constants.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@Table
public final class Order {
    @Id
    private final Integer id;
    private final Integer userId;
    private final OrderStatus status;
    @MappedCollection(idColumn = "ORDER_ID")
    private final Set<OrderLineItem> lineItems;
}
