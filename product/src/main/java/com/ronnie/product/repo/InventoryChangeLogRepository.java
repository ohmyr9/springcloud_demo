package com.ronnie.product.repo;

import com.ronnie.product.model.InventoryChangeLog;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface InventoryChangeLogRepository extends CrudRepository<InventoryChangeLog, Integer> {
    Optional<InventoryChangeLog> findByRequestId(String requestId);


}
