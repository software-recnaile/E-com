package com.recnaile.mailService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "customization")
public class DronePlanDocument {
    @Id
    private String id;
    private String referenceId;
    private LocalDateTime timestamp;
    private String email;
    private String requirements;
    private String droneType;
    private String budget;
    private String timeline;
    private List<String> features;

    // Status fields
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private ProcessStatus processStatus = ProcessStatus.RECEIVED;

    public enum PaymentStatus {
        PENDING, PAID, FAILED, REFUNDED
    }

    public enum ProcessStatus {
        RECEIVED, IN_PROGRESS, COMPLETED, CANCELLED
    }
}