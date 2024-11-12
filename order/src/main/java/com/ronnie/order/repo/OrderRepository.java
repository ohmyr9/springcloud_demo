package com.ronnie.order.repo;

import com.ronnie.order.constants.OrderStatus;
import com.ronnie.order.model.Order;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Integer> {
    @Modifying
    @Query("UPDATE `ORDER` SET status=:orderStatus WHERE id = :id")
    boolean updateStatusById(Integer id, OrderStatus orderStatus);

    List<Order> findAllByUserId(Integer userId);
}
