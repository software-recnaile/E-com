package com.recnaile.bulkorderservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Data
@Document(collection = "bulk-order-logs")
public class ActivityLog {
    public enum ActivityType {
        ORDER_CREATED,
        PAYMENT_STATUS_CHANGED,
        PROCESS_STATUS_CHANGED
    }

    @Id
    private String id;
    private ActivityType activityType;
    private String referenceId;
    private String username;
    private String description;
    private LocalDateTime timestamp;
    private Object oldValue;
    private Object newValue;
}