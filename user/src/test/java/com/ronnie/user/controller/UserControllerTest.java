package com.ronnie.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronnie.common.api.Result;
import com.ronnie.user.api.dto.CreateUserReqDTO;
import com.ronnie.user.api.dto.UserDTO;
import com.ronnie.user.api.dto.UserRolesDTO;
import com.ronnie.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;

    @Test
    void createUser_shouldSucceed() throws Exception {
        UserDTO userDTO = UserDTO.builder()
                .id(999)
                .username("username")
                .authorities(List.of(new UserRolesDTO("default")))
                .build();
        when(userService.createUser(any())).thenReturn(userDTO);
        ObjectMapper mapper = new ObjectMapper();
        CreateUserReqDTO request = CreateUserReqDTO.builder()
                .username("username")
                .password("Passwr0d!23")
                .build();

        Result<UserDTO> expected = Result.success(userDTO);
        mvc.perform(
                        post("/v1/users")
                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                .content(mapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));

    }

    @Test
    void findUser_shouldSucceed() throws Exception {
        int userId = 123;
        UserDTO userDTO = UserDTO.builder()
                .id(userId)
                .username("username")
                .authorities(List.of(new UserRolesDTO("default")))
                .build();


        when(userService.findUserById(userId)).thenReturn(userDTO);

        ObjectMapper mapper = new ObjectMapper();


        Result<UserDTO> expected = Result.success(userDTO);
        mvc.perform(get("/v1/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }

    @Test
    void findUserByName_shouldSucceed() throws Exception {
        String userName = "testUsername";
        UserDTO userDTO = UserDTO.builder()
                .id(1234)
                .username("username")
                .authorities(List.of(new UserRolesDTO("default")))
                .build();


        when(userService.findUserByName(userName)).thenReturn(userDTO);

        ObjectMapper mapper = new ObjectMapper();


        Result<UserDTO> expected = Result.success(userDTO);
        mvc.perform(get("/v1/users/name/" + userName))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }
}