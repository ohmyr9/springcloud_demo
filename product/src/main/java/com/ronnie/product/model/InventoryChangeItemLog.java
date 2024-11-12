package com.ronnie.product.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
public class InventoryChangeItemLog {
    @Id
    private final Integer id;
    private final Integer productId;
    private final Integer quantity;

}
