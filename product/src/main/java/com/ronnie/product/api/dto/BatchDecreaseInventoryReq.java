package com.ronnie.product.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public final class BatchDecreaseInventoryReq {
    private final @NotNull String requestId;
    private final @NotEmpty List<DecreaseInventory> decreaseInventories;
}
