package com.recnaile.accountService.model.product;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "products")
public class Product {
    @Id
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
    private List<ProductVariant> productVariants;
    private Date createdAt;
    private Date editedAt;
    private String _class;

    @Data
    public static class ProductVariant {
        private Map<String, String> properties;
        private Integer availableStock;
        private Double mrpRate;
        private Double discountAmount;
        private Double ratings;
    }
}