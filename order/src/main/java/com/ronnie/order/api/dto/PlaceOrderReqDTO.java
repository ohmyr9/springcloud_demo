package com.ronnie.order.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public final class PlaceOrderReqDTO {
    @NotNull
    private final Integer userId;

    @NotEmpty
    private final List<OrderLineItemDTO> lineItems;

}
