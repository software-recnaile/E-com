package com.recnaile.productService.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProductRequest {
    private String productUniqueName;
    private String name;
    private String description;
    private double price;
    // Images will be handled separately as MultipartFile[]
}