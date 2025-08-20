package com.recnaile.accountService.service;

import com.recnaile.accountService.model.ActivityLog;
import com.recnaile.accountService.repository.activity.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityLogService {
    private final ActivityLogRepository activityLogRepository;

    public void logActivity(String email, String activityType, String description) {
        ActivityLog log = new ActivityLog();
        log.setEmail(email);
        log.setActivityType(activityType);
        log.setDescription(description);
        log.setTimestamp(LocalDateTime.now());
        activityLogRepository.save(log);
    }

    // Add this new method to get all activity logs
    public List<ActivityLog> getAllActivityLogs() {
        return activityLogRepository.findAll();
    }
}