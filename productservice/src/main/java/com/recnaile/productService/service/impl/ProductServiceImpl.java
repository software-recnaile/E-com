package com.recnaile.productService.service.impl;

import com.recnaile.productService.dto.ProductDTO;
import com.recnaile.productService.dto.ProductResponse;
import com.recnaile.productService.exception.ProductNotFoundException;
import com.recnaile.productService.model.Product;
import com.recnaile.productService.repository.ProductRepository;
import com.recnaile.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Override
    public ProductResponse addProduct(ProductDTO productDTO) {
        // Generate 6-digit unique product name
        String uniqueProductName = generateUniqueProductName();

        Product product = modelMapper.map(productDTO, Product.class);
        product.setUniqueProductName(uniqueProductName);
        product.setRatings(0.0);
        product.setCreatedAt(LocalDateTime.now());
        product.setEditedAt(LocalDateTime.now());

        Product savedProduct = productRepository.save(product);
        return mapToProductResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(String id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse getProductByUniqueName(String uniqueName) {
        Product product = productRepository.findByUniqueProductName(uniqueName)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with unique name: " + uniqueName));
        return mapToProductResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        List<Product> products = productRepository.findAll();
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByProductCategory(category);
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategoryAndSubCategory(String category, String subCategory) {
        List<Product> products = productRepository.findByProductCategoryAndProductSubCategory(category, subCategory);
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> searchProducts(String query) {
        List<Product> products = productRepository.findByProductNameContainingIgnoreCase(query);
        return products.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse updateProduct(String id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        modelMapper.map(productDTO, existingProduct);
        existingProduct.setEditedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(existingProduct);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(String id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public ProductResponse updateStock(String id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));

        product.setAvailableStock(product.getAvailableStock() + quantity);
        product.setEditedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    private String generateUniqueProductName() {
        String uuid = UUID.randomUUID().toString();
        // Take first 6 characters and ensure they are digits
        String digits = uuid.replaceAll("[^0-9]", "");
        if (digits.length() < 6) {
            // If not enough digits, pad with zeros
            return String.format("%06d", Integer.parseInt(digits));
        }
        return digits.substring(0, 6);
    }

    private ProductResponse mapToProductResponse(Product product) {
        return modelMapper.map(product, ProductResponse.class);
    }
}