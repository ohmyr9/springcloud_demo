package com.ronnie.product.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Builder
public final class CreateProductReq {
    @NotEmpty
    private final String name;
    @NotEmpty
    private final BigDecimal price;
}
