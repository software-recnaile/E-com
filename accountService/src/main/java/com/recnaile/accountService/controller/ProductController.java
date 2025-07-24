package com.recnaile.accountService.controller;

import org.springframework.web.bind.annotation.*;
import com.recnaile.accountService.model.product.Product;
import com.recnaile.accountService.service.product.ProductService;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{uniqueProductName}")
    public Product getProduct(@PathVariable String uniqueProductName) {
        return productService.getProductByUniqueName(uniqueProductName);
    }
}