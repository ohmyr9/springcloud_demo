package com.ronnie.user.controller;

import com.ronnie.common.api.Result;
import com.ronnie.user.api.dto.ActiveUserReqDTO;
import com.ronnie.user.api.dto.CreateUserReqDTO;
import com.ronnie.user.api.dto.UserDTO;
import com.ronnie.user.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/v1/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Result<UserDTO> getUser(@PathVariable @NotNull Integer id) {
        return Result.success(userService.findUserById(id));
    }

    @GetMapping()
    public Result<List<UserDTO>> listUsers() {
        return Result.success(userService.listAllUsers());
    }

    @GetMapping("/name/{name}")
    public Result<UserDTO> getUser(@PathVariable @NotNull String name) {
        return Result.success(userService.findUserByName(name));
    }

    @PostMapping
    public Result<UserDTO> createUser(CreateUserReqDTO createUserDTO) {
        UserDTO user = userService.createUser(createUserDTO);
        return Result.success(user);
    }

    @PostMapping("/active")
    public Result<Void> activateUser(ActiveUserReqDTO activeUserDTO) {
        //todo
        return Result.success();
    }

    @PostMapping("/deactivate")
    public Result<Void> deactivateUser(ActiveUserReqDTO activeUserDTO) {
        //todo
        return Result.success();
    }


}
