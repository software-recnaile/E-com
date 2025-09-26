package com.recnaile.authService.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Document(collection = "user_activity_logs")
public class UserActivityLog {
    @Id
    private String id;
    private String email;
    private String username;
    private String activityType; // "SIGN_UP", "LOGIN", "ADMIN_CHANGE", etc.
    private String description;
    private Date timestamp;

    // constructors, getters, setters
    public UserActivityLog() {
    }

    public UserActivityLog(String email, String username, String activityType, String description) {
        this.email = email;
        this.username = username;
        this.activityType = activityType;
        this.description = description;
        this.timestamp = new Date();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}