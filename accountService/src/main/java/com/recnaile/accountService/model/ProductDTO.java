package com.recnaile.accountService.model;

import lombok.Data;
import java.util.List;

@Data
public class ProductDTO {
    private String _id;
    private String uniqueProductName;
    private String productName;
    private List<String> productUsecase;
    private List<String> productImages;
    private String productThumbnail;
    private String productDescription;
    private String productCategory;
    private String productSubCategory;
    private Object productSpecialization;
    private List<Object> productVariants;
    private int availableStock;
    private double mrpRate;
    private double discountAmount;
    private double ratings;
    private String createdAt;
    private String editedAt;
}