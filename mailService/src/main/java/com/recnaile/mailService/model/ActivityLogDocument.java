package com.recnaile.mailService.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "customize_activity_logs")
public class ActivityLogDocument {

    @Id
    private String id; // MongoDB will auto-generate ObjectId

    private String email;
    private String username;
    private String activityType; // e.g. LOGIN, LOGOUT, PAYMENT_UPDATE
    private String description;
    private LocalDateTime timestamp;
}
