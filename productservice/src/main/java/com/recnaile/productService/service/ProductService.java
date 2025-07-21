package com.recnaile.productService.service;

import com.recnaile.productService.dto.ProductDTO;
import com.recnaile.productService.dto.ProductResponse;
import java.util.List;

public interface ProductService {
    ProductResponse addProduct(ProductDTO productDTO);
    ProductResponse getProductById(String id);
    ProductResponse getProductByUniqueName(String uniqueName);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(String category);
    List<ProductResponse> getProductsByCategoryAndSubCategory(String category, String subCategory);
    List<ProductResponse> searchProducts(String query);
    ProductResponse updateProduct(String id, ProductDTO productDTO);
    void deleteProduct(String id);
    ProductResponse updateStock(String id, Integer quantity);
}