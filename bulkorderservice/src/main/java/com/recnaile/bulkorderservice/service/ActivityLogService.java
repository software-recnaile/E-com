package com.recnaile.bulkorderservice.service;

import com.recnaile.bulkorderservice.model.ActivityLog;
import com.recnaile.bulkorderservice.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    @Qualifier("activityMongoTemplate")
    private MongoTemplate activityMongoTemplate;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void logOrderCreated(String referenceId, String email) {
        ActivityLog log = new ActivityLog();
        log.setActivityType(ActivityLog.ActivityType.ORDER_CREATED);
        log.setReferenceId(referenceId);
        log.setUsername(email);
        log.setDescription(email + " has placed an order " + referenceId);
        log.setTimestamp(LocalDateTime.now());
        activityMongoTemplate.save(log, "bulk-order-logs");
    }

    public void logPaymentStatusChange(String referenceId, String oldStatus, String newStatus, String email) {
        ActivityLog log = new ActivityLog();
        log.setActivityType(ActivityLog.ActivityType.PAYMENT_STATUS_CHANGED);
        log.setReferenceId(referenceId);
        log.setUsername(email);
        log.setDescription(email + " changed payment status  to " + newStatus + " for order " + referenceId);
        log.setOldValue(oldStatus);
        log.setNewValue(newStatus);
        log.setTimestamp(LocalDateTime.now());
        activityMongoTemplate.save(log, "bulk-order-logs");
    }

    public void logProcessStatusChange(String referenceId, String oldStatus, String newStatus, String email) {
        ActivityLog log = new ActivityLog();
        log.setActivityType(ActivityLog.ActivityType.PROCESS_STATUS_CHANGED);
        log.setReferenceId(referenceId);
        log.setUsername(email);
        log.setDescription(email + " changed process status  to " + newStatus + " for order " + referenceId);
        log.setOldValue(oldStatus);
        log.setNewValue(newStatus);
        log.setTimestamp(LocalDateTime.now());
        activityMongoTemplate.save(log, "bulk-order-logs");
    }

    public List<ActivityLog> getAllLogs() {
        Query query = new Query();
        return activityMongoTemplate.find(query, ActivityLog.class, "bulk-order-logs");
    }

}
