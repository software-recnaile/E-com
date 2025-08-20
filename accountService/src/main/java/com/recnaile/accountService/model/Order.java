package com.recnaile.accountService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "orders")
public class Order {
    @Id
    private String id;
    private String userId;
    private String orderNumber;
    private LocalDateTime orderDate;
    private LocalDateTime deliveryDate;
    private List<OrderItem> items;
    private String shippingAddressId;
    private String billingAddressId;
    private double subtotal;
    private double tax;
    private double shippingCharge;
    private double total;
    private String paymentMethod;
    private String paymentStatus; // PENDING, PAID, FAILED
    private String orderStatus; // PROCESSING, SHIPPED, DELIVERED, CANCELLED
//    private String trackingNumber;

    @Data
    public static class OrderItem {
//        private String productId;
        private String uniqueProductName;
        private String productName;
        private String productThumbnail;
        private int quantity;
        private double mrpRate;
        private double discountAmount;
        private double finalPrice;
    }

    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        REFUNDED
    }
}