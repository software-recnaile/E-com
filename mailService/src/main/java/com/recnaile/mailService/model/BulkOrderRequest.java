package com.recnaile.mailService.model;



import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class BulkOrderRequest {
    private String orderReference;
    private CompanyInfo companyInfo;
    private ContactInfo contactInfo;
    private List<Product> products;
    private ShippingInfo shippingInfo;
    private String paymentTerms;
    private String additionalRequirements;
    private String deliveryDate;
    private String estimatedQuantity;

    @Data
    public static class CompanyInfo {
        private String name;
        private String type;
        private String taxId;
    }

    @Data
    public static class ContactInfo {
        private String person;
        private String email;
        private String phone;
    }

    @Data
    public static class Product {
        private String category;
        private String subcategory;
        private String quantity;
        private String specialRequirements;
    }

    @Data
    public static class ShippingInfo {
        private String shippingAddress;
        private String billingAddress;
        private boolean sameAsShipping;
    }

    public int getTotalProducts() {
        return products != null ? products.size() : 0;
    }

    public Map<String, List<Product>> getProductsByCategory() {
        return products.stream()
                .collect(Collectors.groupingBy(Product::getCategory));
    }
}
