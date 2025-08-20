package com.recnaile.accountService.repository;

import com.recnaile.accountService.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {

    Optional<Product> findByUniqueProductName(String uniqueProductName);

    // Add this if you need case-insensitive search
    Optional<Product> findByUniqueProductNameIgnoreCase(String uniqueProductName);
}