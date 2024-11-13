package com.ronnie.order.model;

import com.ronnie.order.constants.OrderChangeTransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderChangeTransaction {
    @Id
    private String id;
    private String orderId;
    private OrderChangeTransactionStatus status;
    @Version
    private Integer version;
}
