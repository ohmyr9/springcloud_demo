package com.ronnie.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
public class Role {
    @Id
    private final Integer id;
    private final String roleKey;
}
