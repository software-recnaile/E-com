package com.recnaile.mailService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "activity_logs")
public class ActivityLogDocument {

    @Id
    private String id;

    private String email;
    private String referenceId;
    private String activityType; // e.g., PLAN_CREATED, PAYMENT_UPDATE, PROCESS_UPDATE
    private String description;
    private LocalDateTime timestamp;
}