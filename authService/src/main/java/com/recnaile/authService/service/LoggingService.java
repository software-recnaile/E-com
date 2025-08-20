package com.recnaile.authService.service;

import com.recnaile.authService.model.UserActivityLog;
import com.recnaile.authService.repository.UserActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class LoggingService {
    // private final UserActivityLogRepository logRepository;
    private final MongoTemplate logsMongoTemplate;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

    @Autowired
    public LoggingService(
            UserActivityLogRepository logRepository,
            @Qualifier("logsMongoTemplate") MongoTemplate logsMongoTemplate) {
        // this.logRepository = logRepository;
        this.logsMongoTemplate = logsMongoTemplate;
    }


    public void logActivity(String email, String username, String activityType, String description) {
        UserActivityLog log = new UserActivityLog(email, username, activityType, description);
        // logRepository.save(log);
        logsMongoTemplate.save(log, "user_activity_logs");
    }

    public void logSignUp(String email, String username) {
        String timestamp = dateFormat.format(new Date());
        String description = String.format("%s (%s) signed up at %s", username, email, timestamp);
        logActivity(email, username, "SIGN_UP", description);
    }

    public void logLogin(String email, String username) {
        String timestamp = dateFormat.format(new Date());
        String description = String.format("%s (%s) logged in at %s", username, email, timestamp);
        logActivity(email, username, "LOGIN", description);
    }

    public void logAdminChange(String email, String username, boolean isAdmin) {
        String timestamp = dateFormat.format(new Date());
        String adminStatus = isAdmin ? "ADMIN" : "REGULAR_USER";
        String description = String.format("The role for %s (%s) was changed to %s at %s",
                username, email, adminStatus, timestamp);
        logActivity(email, username, "ADMIN_CHANGE", description);
    }

}
