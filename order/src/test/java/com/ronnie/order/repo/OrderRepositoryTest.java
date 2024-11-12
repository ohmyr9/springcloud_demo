package com.ronnie.order.repo;

import com.ronnie.order.constants.OrderStatus;
import com.ronnie.order.model.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;

import java.util.Optional;

@DataJdbcTest
public class OrderRepositoryTest {
    @Autowired
    private OrderRepository repository;

    @Test
    public void updateStatusOnNonExistentOrderShouldFail() {
        int nonExistentId = 99999;
        boolean updated = repository.updateStatusById(nonExistentId, OrderStatus.PLACED);
        Assertions.assertFalse(updated);
    }

    @Test
    public void updateStatusOnExistentOrderShouldSucceed() {
        OrderStatus originalStatus = OrderStatus.DRAFT;
        Order saved = repository.save(new Order(null, 1, originalStatus, null));
        OrderStatus newStatus = OrderStatus.PLACED;

        boolean updated = repository.updateStatusById(saved.getId(), newStatus);

        Optional<Order> order = repository.findById(saved.getId());
        Assertions.assertTrue(updated);
        Assertions.assertTrue(order.isPresent());
        Assertions.assertEquals(newStatus, order.get().getStatus());
    }

    @Test
    public void updateStatusOnExistentOrderWithSameStatusShouldBeIdempotent() {
        OrderStatus originalStatus = OrderStatus.DRAFT;
        Order saved = repository.save(new Order(null, 1, originalStatus, null));
        OrderStatus newStatus = originalStatus;

        boolean updated = repository.updateStatusById(saved.getId(), newStatus);

        Optional<Order> order = repository.findById(saved.getId());
        Assertions.assertTrue(updated);
        Assertions.assertTrue(order.isPresent());
        Assertions.assertEquals(newStatus, order.get().getStatus());
    }
}