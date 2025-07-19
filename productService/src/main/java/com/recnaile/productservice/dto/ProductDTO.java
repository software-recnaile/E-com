package com.recnaile.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
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
}