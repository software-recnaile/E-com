package com.recnaile.accountService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@Document(collection = "products")
public class Product {
    @Id
    private String id;

    @Field("uniqueProductName")
    private String uniqueProductName;

    private String productName;
    private String productThumbnail;
    private List<ProductVariant> productVariants;

    @Data
    public static class ProductVariant {
        private double mrpRate;
        private double discountAmount;
    }
}