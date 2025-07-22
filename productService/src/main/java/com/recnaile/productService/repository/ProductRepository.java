package com.recnaile.productService.repository;

import com.recnaile.productService.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends MongoRepository<Product, String> {
    Optional<Product> findByUniqueProductName(String uniqueProductName);
    List<Product> findByProductCategory(String category);
    List<Product> findByProductCategoryAndProductSubCategory(String category, String subCategory);
    List<Product> findByProductNameContainingIgnoreCase(String name);
}