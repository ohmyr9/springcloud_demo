package com.ronnie.order.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DecreaseInventoryDTO {
    @NotNull
    private Integer productId;
    @Positive
    private int quantity;
}
