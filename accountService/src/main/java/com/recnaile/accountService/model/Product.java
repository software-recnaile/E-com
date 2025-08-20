package com.recnaile.accountService.model;

import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "products")
public class Product {
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
    @Getter
    private List<String> imageUrls;

}