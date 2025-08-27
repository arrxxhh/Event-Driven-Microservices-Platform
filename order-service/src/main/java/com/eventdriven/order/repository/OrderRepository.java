package com.eventdriven.order.repository;

import com.eventdriven.order.model.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends CrudRepository<Order, String> {

    List<Order> findByCustomerId(String customerId);

    Optional<Order> findByOrderId(String orderId);

    List<Order> findByStatus(String status);
}