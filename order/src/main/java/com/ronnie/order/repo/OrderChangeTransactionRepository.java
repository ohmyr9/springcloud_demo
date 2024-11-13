package com.ronnie.order.repo;

import com.ronnie.order.model.OrderChangeTransaction;
import org.springframework.data.repository.CrudRepository;

public interface OrderChangeTransactionRepository extends CrudRepository<OrderChangeTransaction,String> {
}
