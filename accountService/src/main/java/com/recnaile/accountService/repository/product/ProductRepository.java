package com.recnaile.accountService.repository.product;

import com.recnaile.accountService.model.product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByUniqueProductName(String uniqueProductName);

    // Add this if you need case-insensitive search
    Optional<Product> findByUniqueProductNameIgnoreCase(String uniqueProductName);
}