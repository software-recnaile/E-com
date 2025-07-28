package com.recnaile.productService.controller;//package com.recnaile.productService.controller;
//import com.recnaile.productService.dto.ProductRequest;
//import com.recnaile.productService.model.Product;
//import com.recnaile.productService.service.ProductService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.validation.Valid;
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
//    @PostMapping
//    public ResponseEntity<?> createProduct(
//            @Valid @ModelAttribute ProductRequest productRequest,
//            @RequestParam("images") MultipartFile[] imageFiles) throws IOException {
//
//        Product product = new Product();
//        product.setProductUniqueName(productRequest.getProductUniqueName());
//        product.setName(productRequest.getName());
//        product.setDescription(productRequest.getDescription());
//        product.setPrice(productRequest.getPrice());
//
//        Product createdProduct = productService.createProduct(product, imageFiles);
//        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/{productUniqueName}")
//    public ResponseEntity<Product> getProductByUniqueName(
//            @PathVariable String productUniqueName) {
//        Product product = productService.getProductByUniqueName(productUniqueName);
//        return ResponseEntity.ok(product);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Product>> getAllProducts() {
//        List<Product> products = productService.getAllProducts();
//        return ResponseEntity.ok(products);
//    }
//
//    // Add update and delete endpoints as needed
//}

import com.recnaile.productService.model.Product;
import com.recnaile.productService.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestParam("productUniqueName") String productUniqueName,
            @RequestParam(value = "images", required = false) MultipartFile[] images) throws IOException {
        return new ResponseEntity<>(
                productService.createProduct(productUniqueName, images),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{productUniqueName}")
    public ResponseEntity<Product> getProduct(
            @PathVariable String productUniqueName) {
        return ResponseEntity.ok(productService.getProduct(productUniqueName));
    }

    @PutMapping("/{productUniqueName}/images")
    public ResponseEntity<Product> updateProductImages(
            @PathVariable String productUniqueName,
            @RequestParam("images") MultipartFile[] images) throws IOException {
        return ResponseEntity.ok(
                productService.updateProductImages(productUniqueName, images)
        );
    }

    @DeleteMapping("/{productUniqueName}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String productUniqueName) throws IOException {
        productService.deleteProduct(productUniqueName);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{productUniqueName}/images/{imageIndex}")
    public ResponseEntity<Product> updateSingleImage(
            @PathVariable String productUniqueName,
            @PathVariable int imageIndex,
            @RequestParam("image") MultipartFile image) throws IOException {
        return ResponseEntity.ok(
                productService.updateSingleImage(productUniqueName, imageIndex, image)
        );
    }
}