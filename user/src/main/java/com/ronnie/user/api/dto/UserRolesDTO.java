package com.ronnie.user.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

@Data
@AllArgsConstructor
@Builder
public class UserRolesDTO implements GrantedAuthority {
    private final String authority;
}
