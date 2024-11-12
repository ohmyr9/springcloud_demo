package com.ronnie.user.repo;

import com.ronnie.user.model.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Integer> {
}
