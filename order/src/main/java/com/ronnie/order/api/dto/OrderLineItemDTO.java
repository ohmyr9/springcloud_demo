package com.ronnie.order.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
public final class OrderLineItemDTO {
    @NotNull
    private final Integer productId;
    @NotNull
    private final Integer quantity;

}
