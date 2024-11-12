package com.ronnie.user.controller;

import com.ronnie.common.api.Result;
import com.ronnie.user.api.dto.UserDetailsDTO;
import com.ronnie.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users/authenticate")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Result<UserDetailsDTO> userDetails(@RequestParam String username) {
        UserDetailsDTO userDetailsDTO = userService.userDetailsForAuth(username);
        return Result.success(userDetailsDTO);
    }
}
