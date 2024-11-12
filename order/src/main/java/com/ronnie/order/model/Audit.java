package com.ronnie.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public final class Audit {
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
}
