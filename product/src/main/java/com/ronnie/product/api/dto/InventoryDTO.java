package com.ronnie.product.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@AllArgsConstructor
@Data
@Builder
public class InventoryDTO {
    private Integer productId;
    private Integer quantity;
}
