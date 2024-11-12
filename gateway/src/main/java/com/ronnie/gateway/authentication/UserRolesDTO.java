package com.ronnie.gateway.authentication;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Data
@NoArgsConstructor
public class UserRolesDTO implements GrantedAuthority {
    private String authority;
}
