package com.recnaile.bulkorderservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@Document(collection = "bulk_orders")
public class BulkOrderDocument {
    public enum OrderProcessStatus {
        SHIPPED, PROCESSING, DELIVERED, CANCELLED, RETURNED
    }

    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED
    }

    @Data
    public static class StatusHistoryEntry {
        public enum StatusType {
            PROCESS, PAYMENT
        }

        private LocalDateTime timestamp;
        private StatusType statusType;
        private String oldStatus;
        private String newStatus;
        private String changedBy;
        private String notes;
    }

    @Id
    private String id;
    private String referenceId;
    private LocalDateTime timestamp;
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String companyType;
    private String taxId;
    private Map<String, List<String>> selectedProducts;
    private Integer quantity;
    private String deliveryDate;
    private String shippingAddress;
    private String billingAddress;
    private String paymentTerms;
    private String additionalNotes;
    private boolean sameAsShipping;
    private OrderProcessStatus processStatus = OrderProcessStatus.PROCESSING;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private List<StatusHistoryEntry> statusHistory = new ArrayList<>();
}