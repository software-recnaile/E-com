package com.recnaile.accountService.repository;

import com.recnaile.accountService.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByUserId(String userId);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByUserIdAndOrderStatus(String userId, String orderStatus);
}