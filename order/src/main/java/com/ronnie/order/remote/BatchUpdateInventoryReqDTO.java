package com.ronnie.order.remote;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class BatchUpdateInventoryReqDTO {
    @NotNull
    private String requestId;
    private final @NotEmpty List<ChangeInventoryDTO> decreaseInventories;
}
