package com.recnaile.productservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponse {
    private String id;
    private String uniqueProductName;
    private String productName;
    private List<String> productUsecase;
    private List<String> productImages;
    private String productThumbnail;
    private String productDescription;
    private String productCategory;
    private String productSubCategory;
    private Map<String, String> productSpecialization;
    private List<Map<String, String>> productVariants;
    private Integer availableStock;
    private Double mrpRate;
    private Double discountAmount;
    private Double ratings;
    private List<String> reviews;
    private LocalDateTime createdAt;
    private LocalDateTime editedAt;
}