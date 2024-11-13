package com.ronnie.user.service;

import com.ronnie.user.api.dto.CreateUserReqDTO;
import com.ronnie.user.api.dto.UserDTO;
import com.ronnie.user.api.dto.UserDetailsDTO;
import com.ronnie.user.api.dto.UserRolesDTO;
import com.ronnie.user.exception.UserIdNotFoundException;
import com.ronnie.user.model.UserAccount;
import com.ronnie.user.model.UserRole;
import com.ronnie.user.repo.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDetailsDTO userDetailsForAuth(String username) {
        return userRepository.findByUsername(username).map(userAccount -> {
            UserDetailsDTO userDetailsDTO = UserDetailsDTO.builder()
                    .username(userAccount.getUsername())
                    .password(userAccount.getPassword())
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .enabled(true)
                    .authorities(userAccount.getRoles().stream().map(r -> new UserRolesDTO(r.getRoleKey())).collect(Collectors.toSet()))
                    .build();
            return userDetailsDTO;
        }).orElseThrow(() -> new UsernameNotFoundException(username));
    }

    public UserDTO createUser(CreateUserReqDTO createUserDTO) {
        UserAccount userAccount = UserAccount.builder()
                .username(createUserDTO.getUsername())
                .password(passwordEncoder.encode(createUserDTO.getPassword()))
                .roles(Set.of(new UserRole(null, "default")))
                .build();
        UserAccount savedUser = userRepository.save(userAccount);
        return mapUserToUserDTO(savedUser);
    }

    private static UserDTO mapUserToUserDTO(UserAccount savedUser) {
        UserDTO userDTO = UserDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .authorities(savedUser.getRoles().stream().map(r -> new UserRolesDTO(r.getRoleKey()))
                        .collect(Collectors.toList())).build();
        return userDTO;
    }

    public UserDTO findUserById(@NotNull Integer id) {
        return userRepository.findById(id).map(UserService::mapUserToUserDTO).orElseThrow(() -> new UserIdNotFoundException());
    }

    public UserDTO findUserByName(@NotNull String name) {
        return userRepository.findByUsername(name).map(UserService::mapUserToUserDTO).orElseThrow(() -> new UserIdNotFoundException());
    }

    public List<UserDTO> listAllUsers() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false).map(UserService::mapUserToUserDTO).collect(Collectors.toList());
    }
}
