package com.ronnie.user.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@Builder
public class CreateUserReqDTO {
    @NotEmpty
    private final String username;
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\\\d)(?=.*[@#$%^&+=]).{8,20}$")
    private final String password;
}
