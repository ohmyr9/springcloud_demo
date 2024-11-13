package com.ronnie.product.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;

import javax.validation.constraints.PositiveOrZero;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangeInventoryReqDTO {

    @PositiveOrZero
    private Integer quantity;
    private Integer productId;

}
