package com.ronnie.user.repo;

import com.ronnie.user.model.UserAccount;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<UserAccount, Integer> {
    Optional<UserAccount> findByUsername(String username);
}
