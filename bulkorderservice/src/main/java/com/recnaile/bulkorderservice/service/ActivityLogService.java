package com.recnaile.bulkorderservice.service;

import com.recnaile.bulkorderservice.model.ActivityLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ActivityLogService {

    @Autowired
    @Qualifier("activityMongoTemplate")
    private MongoTemplate activityMongoTemplate;

    public void logOrderCreated(String referenceId) {
        String username = getCurrentUsername();
        ActivityLog log = new ActivityLog();
        log.setActivityType(ActivityLog.ActivityType.ORDER_CREATED);
        log.setReferenceId(referenceId);
        log.setUsername(username);
        log.setDescription(username + " has placed an order " + referenceId);
        log.setTimestamp(LocalDateTime.now());
        activityMongoTemplate.save(log, "bulk-order-logs");
    }

    public void logPaymentStatusChange(String referenceId, String oldStatus, String newStatus) {
        String username = getCurrentUsername();
        ActivityLog log = new ActivityLog();
        log.setActivityType(ActivityLog.ActivityType.PAYMENT_STATUS_CHANGED);
        log.setReferenceId(referenceId);
        log.setUsername(username);
        log.setDescription(username + " changed payment status from " + oldStatus + " to " + newStatus + " for order " + referenceId);
        log.setOldValue(oldStatus);
        log.setNewValue(newStatus);
        log.setTimestamp(LocalDateTime.now());
        activityMongoTemplate.save(log, "bulk-order-logs");
    }

    public void logProcessStatusChange(String referenceId, String oldStatus, String newStatus) {
        String username = getCurrentUsername();
        ActivityLog log = new ActivityLog();
        log.setActivityType(ActivityLog.ActivityType.PROCESS_STATUS_CHANGED);
        log.setReferenceId(referenceId);
        log.setUsername(username);
        log.setDescription(username + " changed process status from " + oldStatus + " to " + newStatus + " for order " + referenceId);
        log.setOldValue(oldStatus);
        log.setNewValue(newStatus);
        log.setTimestamp(LocalDateTime.now());
        activityMongoTemplate.save(log, "bulk-order-logs");
    }

    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();
        }
        return "SYSTEM";
    }
}