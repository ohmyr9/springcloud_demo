package com.ronnie.product.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public class UpdateProductReq {
    @NotNull
    private final Integer productId;
    @NotEmpty
    private final String name;
    @NotNull
    private final BigDecimal price;
}
