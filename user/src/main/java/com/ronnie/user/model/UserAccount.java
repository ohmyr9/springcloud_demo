package com.ronnie.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
@Table("USER_ACCOUNT")
public class UserAccount {
    @Id
    private final Integer id;
    private final String username;
    private final String password;
    @MappedCollection(idColumn = "USER_ID")
    private final Set<UserRole> roles;
}
