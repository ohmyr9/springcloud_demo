package com.ronnie.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.Set;
@Data
@AllArgsConstructor
public class InventoryChangeLog {
    @Id
    private final Integer id;
    private final String requestId;
    @MappedCollection(idColumn = "LOG_ID")
    private final Set<InventoryChangeItemLog> itemLogs;
}
