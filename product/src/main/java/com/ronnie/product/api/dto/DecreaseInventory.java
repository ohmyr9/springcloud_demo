package com.ronnie.product.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@AllArgsConstructor
@Builder
public final class DecreaseInventory {
    @NotNull
    private final Integer productId;
    @NotNull
    private final Integer quantity;
}