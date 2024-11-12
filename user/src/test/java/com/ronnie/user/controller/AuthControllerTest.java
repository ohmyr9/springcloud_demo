package com.ronnie.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ronnie.common.api.Result;
import com.ronnie.user.api.dto.UserDetailsDTO;
import com.ronnie.user.api.dto.UserRolesDTO;
import com.ronnie.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private UserService userService;

    @Test
    void userDetails_shouldSucceed() throws Exception {
        UserDetailsDTO userDetailsDTO = UserDetailsDTO.builder()
                .username("username")
                .password("password")
                .authorities(List.of(new UserRolesDTO("default")))
                .build();

        when(userService.userDetailsForAuth("user")).thenReturn(userDetailsDTO);

        ObjectMapper mapper = new ObjectMapper();


        Result<UserDetailsDTO> expected = Result.success(userDetailsDTO);
        mvc.perform(get("/v1/users/authenticate").param("username", "user"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(expected)));
    }
}