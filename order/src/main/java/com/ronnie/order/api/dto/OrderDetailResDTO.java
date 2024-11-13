package com.ronnie.order.api.dto;

import com.ronnie.order.constants.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public final class OrderDetailResDTO {
    private final String orderId;
    private final Integer userId;
    private final OrderStatus orderStatus;
    private final List<OrderLineItemDTO> lineItems;
}
