package com.ronnie.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@Table("USER_ROLE")
public class UserRole {
    @Id
    private final Integer id;
    private final String roleKey;
}
