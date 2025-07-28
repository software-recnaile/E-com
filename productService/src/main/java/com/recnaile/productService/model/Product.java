package com.recnaile.productService.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Document(collection = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @MongoId
    private String id;

    @Indexed(unique = true)
    @NotNull
    private String uniqueProductName;

    @NotNull
    private String productName;

    private List<String> productUsecase;
    private String productDescription;

    @NotNull
    private String productCategory;
    private String productSubCategory;

    private Map<String, String> productSpecialization;
    private List<Map<String, String>> productVariants;

    @NotNull
    private Integer availableStock;

    @NotNull
    private Double mrpRate;
    private Double discountAmount;

    private Double ratings;
    private List<String> reviews;
    private List<String> imageUrls;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime editedAt;
}