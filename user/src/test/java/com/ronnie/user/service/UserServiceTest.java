package com.ronnie.user.service;

import com.ronnie.user.api.dto.CreateUserReqDTO;
import com.ronnie.user.api.dto.UserDTO;
import com.ronnie.user.api.dto.UserDetailsDTO;
import com.ronnie.user.model.UserAccount;
import com.ronnie.user.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class UserServiceTest {
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void createUser_shouldSucceed() {
        String rawPassowrd = "password";
        String username = "username";
        CreateUserReqDTO userReqDTO =
                CreateUserReqDTO.builder()
                        .username(username)
                        .password(rawPassowrd)
                        .build();
        UserDTO user = userService.createUser(userReqDTO);

        Optional<UserAccount> savedUserOption = userRepository.findById(user.getId());

        assertThat(savedUserOption).isPresent();
        UserAccount savedUser = savedUserOption.get();
        assertThat(savedUser.getUsername()).isEqualTo(username);
        passwordEncoder.matches(rawPassowrd, savedUser.getPassword());
        assertThat(passwordEncoder.matches(rawPassowrd, savedUser.getPassword())).isTrue();
        assertThat(savedUser.getRoles()).isNotEmpty();
        assertThat(savedUser.getRoles().stream().findFirst().get().getRoleKey()).isEqualTo("default");
    }

    @Test
    void getUserDetails_shouldSucceed() {
        String rawPassowrd = "testPassword";
        String username = "testUser";
        CreateUserReqDTO userReqDTO =
                CreateUserReqDTO.builder()
                        .username(username)
                        .password(rawPassowrd)
                        .build();
        userService.createUser(userReqDTO);


        UserDetailsDTO userDetailsDTO = userService.userDetailsForAuth(username);
        assertThat(userDetailsDTO.getUsername()).isEqualTo(username);
        assertThat(passwordEncoder.matches(rawPassowrd, userDetailsDTO.getPassword())).isTrue();
        assertThat(userDetailsDTO.getAuthorities()).isNotEmpty();
        assertThat(userDetailsDTO.getAuthorities().stream().findFirst().get().getAuthority()).isEqualTo("default");
    }

    @Test
    void getNonExistentUserDetails_shouldThrowException() {
        String username = "nonExistentUsername";
        assertThrows(UsernameNotFoundException.class, () -> userService.userDetailsForAuth(username));
    }

    @Test
    void findUserByName_shouldSucceed() {
        String rawPassowrd = "password1234";
        String username = "username1234";
        CreateUserReqDTO userReqDTO =
                CreateUserReqDTO.builder()
                        .username(username)
                        .password(rawPassowrd)
                        .build();
        UserDTO user = userService.createUser(userReqDTO);

        Optional<UserAccount> savedUserOption = userRepository.findByUsername(user.getUsername());

        assertThat(savedUserOption).isPresent();
        UserAccount savedUser = savedUserOption.get();
        assertThat(savedUser.getUsername()).isEqualTo(username);
        passwordEncoder.matches(rawPassowrd, savedUser.getPassword());
        assertThat(passwordEncoder.matches(rawPassowrd, savedUser.getPassword())).isTrue();
        assertThat(savedUser.getRoles()).isNotEmpty();
        assertThat(savedUser.getRoles().stream().findFirst().get().getRoleKey()).isEqualTo("default");
    }

}