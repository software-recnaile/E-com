//package com.recnaile.productService.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.recnaile.productService.dto.ProductDTO;
//import com.recnaile.productService.dto.ProductResponse;
//import com.recnaile.productService.service.ProductService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/products")
//@RequiredArgsConstructor
//public class ProductController {
//
//    private final ProductService productService;
//
//    // Existing endpoints
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<ProductResponse> createProduct(
//            @RequestPart("product") String productJson,
//            @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {
//
//        // Convert JSON string to ProductDTO
//        ObjectMapper objectMapper = new ObjectMapper();
//        ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);
//
//        ProductResponse response = productService.addProduct(productDTO, images);
//        return new ResponseEntity<>(response, HttpStatus.CREATED);
//    }
//    @GetMapping("/{id}")
//    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
//        ProductResponse response = productService.getProductById(id);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/unique/{uniqueName}")
//    public ResponseEntity<ProductResponse> getProductByUniqueName(@PathVariable String uniqueName) {
//        ProductResponse response = productService.getProductByUniqueName(uniqueName);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<ProductResponse>> getAllProducts() {
//        List<ProductResponse> responses = productService.getAllProducts();
//        return ResponseEntity.ok(responses);
//    }
//
//    @GetMapping("/category/{category}")
//    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
//        List<ProductResponse> responses = productService.getProductsByCategory(category);
//        return ResponseEntity.ok(responses);
//    }
//
//    @GetMapping("/category/{category}/subcategory/{subCategory}")
//    public ResponseEntity<List<ProductResponse>> getProductsByCategoryAndSubCategory(
//            @PathVariable String category,
//            @PathVariable String subCategory) {
//        List<ProductResponse> responses = productService.getProductsByCategoryAndSubCategory(category, subCategory);
//        return ResponseEntity.ok(responses);
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String query) {
//        List<ProductResponse> responses = productService.searchProducts(query);
//        return ResponseEntity.ok(responses);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ProductResponse> updateProduct(
//            @PathVariable String id,
//            @RequestBody ProductDTO productDTO) {
//        ProductResponse response = productService.updateProduct(id, productDTO);
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
//        productService.deleteProduct(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/{id}/stock")
//    public ResponseEntity<ProductResponse> updateStock(
//            @PathVariable String id,
//            @RequestParam Integer quantity) {
//        ProductResponse response = productService.updateStock(id, quantity);
//        return ResponseEntity.ok(response);
//    }
//
//    // New endpoints for image management
//    @PostMapping("/{id}/images")
//    public ResponseEntity<ProductResponse> uploadProductImages(
//            @PathVariable String id,
//            @RequestParam("images") MultipartFile[] images) throws IOException {
//        ProductResponse response = productService.uploadProductImages(id, images);
//        return ResponseEntity.ok(response);
//    }
//
//    @PutMapping("/{id}/images")
//    public ResponseEntity<ProductResponse> updateProductImages(
//            @PathVariable String id,
//            @RequestParam("images") MultipartFile[] images) throws IOException {
//        ProductResponse response = productService.updateProductImages(id, images);
//        return ResponseEntity.ok(response);
//    }
//
//    @PatchMapping("/{id}/images/{imageIndex}")
//    public ResponseEntity<ProductResponse> updateSingleImage(
//            @PathVariable String id,
//            @PathVariable int imageIndex,
//            @RequestParam("image") MultipartFile image) throws IOException {
//        ProductResponse response = productService.updateSingleImage(id, imageIndex, image);
//        return ResponseEntity.ok(response);
//    }
//
//    @DeleteMapping("/{id}/images")
//    public ResponseEntity<Void> deleteProductImages(@PathVariable String id) throws IOException {
//        productService.deleteProductImages(id);
//        return ResponseEntity.noContent().build();
//    }
//}


package com.recnaile.productService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recnaile.productService.dto.*;
import com.recnaile.productService.exception.ProductNotFoundException;
import com.recnaile.productService.model.Product;
import com.recnaile.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // Create product with images
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> createProduct(
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {

        // Convert JSON string to ProductDTO
        ObjectMapper objectMapper = new ObjectMapper();
        ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);

        ProductResponse response = productService.createProduct(productDTO, images);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Get product by ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable String id) {
        ProductResponse response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    // Get product by unique name
    @GetMapping("/unique/{uniqueName}")
    public ResponseEntity<ProductResponse> getProductByUniqueName(@PathVariable String uniqueName) {
        ProductResponse response = productService.getProductByUniqueName(uniqueName);
        return ResponseEntity.ok(response);
    }

    // Get all products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responses = productService.getAllProducts();
        return ResponseEntity.ok(responses);
    }

    // Get products by category
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponse> responses = productService.getProductsByCategory(category);
        return ResponseEntity.ok(responses);
    }

    // Get products by category and subcategory
    @GetMapping("/category/{category}/subcategory/{subCategory}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategoryAndSubCategory(
            @PathVariable String category,
            @PathVariable String subCategory) {
        List<ProductResponse> responses = productService.getProductsByCategoryAndSubCategory(category, subCategory);
        return ResponseEntity.ok(responses);
    }

    // Search products
    @GetMapping("/search")
    public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String query) {
        List<ProductResponse> responses = productService.searchProducts(query);
        return ResponseEntity.ok(responses);
    }

    // Update product with images
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable String id,
            @RequestPart("product") String productJson,
            @RequestPart(value = "images", required = false) MultipartFile[] images) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        ProductDTO productDTO = objectMapper.readValue(productJson, ProductDTO.class);

        ProductResponse response = productService.updateProduct(id, productDTO, images);
        return ResponseEntity.ok(response);
    }

    // Delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) throws IOException {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }


    // Update stock
    @PatchMapping("/{id}/stock")
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable String id,
            @RequestParam Integer quantity) {
        ProductResponse response = productService.updateStock(id, quantity);
        return ResponseEntity.ok(response);
    }

    // Add more images to product
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> addProductImages(
            @PathVariable String id,
            @RequestPart("images") MultipartFile[] images) throws IOException {
        ProductResponse response = productService.addProductImages(id, images);
        return ResponseEntity.ok(response);
    }

    // Update single image
    @PatchMapping(value = "/{id}/images/{imageIndex}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponse> updateSingleImage(
            @PathVariable String id,
            @PathVariable int imageIndex,
            @RequestPart("image") MultipartFile image) throws IOException {
        ProductResponse response = productService.updateSingleImage(id, imageIndex, image);
        return ResponseEntity.ok(response);
    }

    // Delete all images
    @DeleteMapping("/{id}/images")
    public ResponseEntity<Void> deleteProductImages(@PathVariable String id) throws IOException {
        productService.deleteProductImages(id);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/{id}/images/{imageIndex}")
    public ResponseEntity<Void> deleteSingleImage(
            @PathVariable String id,
            @PathVariable int imageIndex) throws IOException {
        productService.deleteSingleImage(id, imageIndex);
        return ResponseEntity.noContent().build();
    }


    // Update stock by unique product name
@PatchMapping("/unique/{uniqueName}/stock")
public ResponseEntity<ProductResponse> updateStockByUniqueName(
        @PathVariable String uniqueName,
        @RequestParam Integer quantity) {
    ProductResponse response = productService.updateStockByUniqueName(uniqueName, quantity);
    return ResponseEntity.ok(response);
}

    
}
