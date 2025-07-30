package com.recnaile.cartService.repository;


import com.recnaile.cartService.model.Cart;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CartRepository extends MongoRepository<Cart, String> {
    Optional<Cart> findByUserId(String userId);
    boolean existsByUserIdAndItemsUniqueProductName(String userId, String uniqueProductName);
    void deleteByUserId(String userId);
}