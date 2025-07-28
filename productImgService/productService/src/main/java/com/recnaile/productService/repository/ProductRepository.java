package com.recnaile.productService.repository;

import com.recnaile.productService.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByProductUniqueName(String productUniqueName);
    boolean existsByProductUniqueName(String productUniqueName);
    void deleteByProductUniqueName(String productUniqueName);
}
