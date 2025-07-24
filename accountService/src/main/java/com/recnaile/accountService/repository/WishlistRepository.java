package com.recnaile.accountService.repository;

import com.recnaile.accountService.model.Wishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WishlistRepository extends MongoRepository<Wishlist, String> {
    Optional<Wishlist> findByUserId(String userId);
    boolean existsByUserIdAndItemsUniqueProductName(String userId, String uniqueProductName);
    void deleteByUserId(String userId);
}