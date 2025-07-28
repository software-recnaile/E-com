//package com.recnaile.productService.service;
//
//import com.recnaile.productService.dto.ProductDTO;
//import com.recnaile.productService.dto.ProductResponse;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//
//public interface ProductService {
//    ProductResponse addProduct(ProductDTO productDTO, MultipartFile[] images) throws IOException;
//    ProductResponse getProductById(String id);
//    ProductResponse getProductByUniqueName(String uniqueName);
//    List<ProductResponse> getAllProducts();
//    List<ProductResponse> getProductsByCategory(String category);
//    List<ProductResponse> getProductsByCategoryAndSubCategory(String category, String subCategory);
//    List<ProductResponse> searchProducts(String query);
//    ProductResponse updateProduct(String id, ProductDTO productDTO);
//    void deleteProduct(String id);
//    ProductResponse updateStock(String id, Integer quantity);
//
//    // Image management methods
//    ProductResponse uploadProductImages(String id, MultipartFile[] images) throws IOException;
//    ProductResponse updateProductImages(String id, MultipartFile[] images) throws IOException;
//    ProductResponse updateSingleImage(String id, int imageIndex, MultipartFile image) throws IOException;
//    void deleteProductImages(String id) throws IOException;
//}

package com.recnaile.productService.service;

import com.recnaile.productService.dto.ProductDTO;
import com.recnaile.productService.dto.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductDTO productDTO, MultipartFile[] images) throws IOException;
    ProductResponse getProductById(String id);
    ProductResponse getProductByUniqueName(String uniqueName);
    List<ProductResponse> getAllProducts();
    List<ProductResponse> getProductsByCategory(String category);
    List<ProductResponse> getProductsByCategoryAndSubCategory(String category, String subCategory);
    List<ProductResponse> searchProducts(String query);
    ProductResponse updateProduct(String id, ProductDTO productDTO, MultipartFile[] images) throws IOException;
    void deleteProduct(String id) throws IOException;
    ProductResponse updateStock(String id, Integer quantity);
    ProductResponse addProductImages(String id, MultipartFile[] images) throws IOException;
    ProductResponse updateSingleImage(String id, int imageIndex, MultipartFile image) throws IOException;
    void deleteProductImages(String id) throws IOException;

}