package com.ronnie.product.repo;

import com.ronnie.product.model.Inventory;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.Optional;

public interface InventoryRepository extends CrudRepository<Inventory,Integer> {
    Iterable<Inventory> findAllByProductIdIn(Collection<Integer> productIds);

    @Modifying
    @Query("UPDATE INVENTORY SET QUANTITY=QUANTITY-:quantity WHERE id = :productId AND QUANTITY>=:quantity")
    boolean decreaseInventory(Integer productId, Integer quantity);

    Optional<Inventory> findByProductId(int productId);
}
