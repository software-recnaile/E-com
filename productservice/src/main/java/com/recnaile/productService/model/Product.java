package com.recnaile.productService.model;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

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

    @NonNull
    private String uniqueProductName; // 6 digits

    @NonNull
    private String productName;

    private List<String> productUsecase;
    private List<String> productImages;
    private String productThumbnail;
    private String productDescription;

    @NonNull
    private String productCategory;
    private String productSubCategory;

    private Map<String, String> productSpecialization; // {"weight":"129G"}
    private List<Map<String, String>> productVariants; // [{"color":"red"}, {"size":"XL"}]

    @NonNull
    private Integer availableStock;

    @NonNull
    private Double mrpRate;
    private Double discountAmount;

    private Double ratings;
    private List<String> reviews;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime editedAt;
}