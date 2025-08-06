//package com.recnaile.bulkorderservice.model;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.time.LocalDateTime;
//import java.util.Map;
//import java.util.List;
//
//@Document(collection = "bulk_orders")
//@Data
//public class BulkOrderDocument {
//    @Id
//    private String id;
//    private String referenceId;
//    private LocalDateTime timestamp;
//    private String companyName;
//    private String contactPerson;
//    private String email;
//    private String phone;
//    private String quantity;
//    private String deliveryDate;
//    private String additionalNotes;
//    private String companyType;
//    private String taxId;
//    private String shippingAddress;
//    private String billingAddress;
//    private String paymentTerms;
//    private boolean sameAsShipping;
//    private Map<String, List<String>> selectedProducts;
//    private OrderProcessStatus processStatus;
//    private PaymentStatus paymentStatus;
//    private List<StatusHistoryEntry> statusHistory;
//
//    public enum OrderProcessStatus {
//        PROCESSING, SHIPPED, DELIVERED, CANCELLED, RETURNED
//    }
//
//    public enum PaymentStatus {
//        PENDING, PAID, FAILED, REFUNDED, PARTIALLY_PAID
//    }
//
//    @Data
//    public static class StatusHistoryEntry {
//        private LocalDateTime timestamp;
//        private String statusType; // "PROCESS" or "PAYMENT"
//        private String oldStatus;
//        private String newStatus;
//        private String changedBy; // "SYSTEM" or "ADMIN" or user ID
//        private String notes;
//    }
//
//
//}


package com.recnaile.bulkorderservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Document(collection = "bulk_orders")
@Data
public class BulkOrderDocument {
    @Id
    private String id;

    @Indexed(unique = true)
    private String referenceId;
    private LocalDateTime timestamp;

    // Existing order fields
    private String companyName;
    private String contactPerson;
    private String email;
    private String phone;
    private String quantity;
    private String deliveryDate;
    private String additionalNotes;
    private String companyType;
    private String taxId;
    private String shippingAddress;
    private String billingAddress;
    private String paymentTerms;
    private boolean sameAsShipping;
    private Map<String, List<String>> selectedProducts;

    // Status fields
    private OrderProcessStatus processStatus = OrderProcessStatus.PROCESSING;
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private List<StatusHistoryEntry> statusHistory = new ArrayList<>();

    public enum OrderProcessStatus {
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        RETURNED
    }

    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        REFUNDED,
        PARTIALLY_PAID
    }

    @Data
    public static class StatusHistoryEntry {
        private LocalDateTime timestamp;
        private StatusType statusType;
        private String oldStatus;
        private String newStatus;
        private String changedBy;
        private String notes;

        public enum StatusType {
            PROCESS, PAYMENT
        }
    }
}