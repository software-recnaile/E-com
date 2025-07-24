package com.recnaile.accountService.service.product;

import com.recnaile.accountService.exception.ResourceNotFoundException;
import com.recnaile.accountService.model.product.Product;
import com.recnaile.accountService.repository.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public Product getProductByUniqueName(String uniqueProductName) {
        // Try exact match first
        return productRepository.findByUniqueProductName(uniqueProductName)
                .or(() -> productRepository.findByUniqueProductNameIgnoreCase(uniqueProductName))
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product not found with uniqueProductName: " + uniqueProductName));
    }
}