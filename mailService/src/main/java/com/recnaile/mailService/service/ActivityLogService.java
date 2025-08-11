package com.recnaile.mailService.service;

import com.recnaile.mailService.model.ActivityLogDocument;
import com.recnaile.mailService.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    @Qualifier("activityLogMongoTemplate")
    private MongoTemplate activityLogMongoTemplate;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    public void log(String email, String username, String activityType) {
        ActivityLogDocument log = new ActivityLogDocument();
        log.setEmail(email);
        log.setUsername(username);
        log.setActivityType(activityType);
        log.setTimestamp(LocalDateTime.now());

        String formattedDate = log.getTimestamp()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));
        log.setDescription(username + " (" + email + ") "
                + getActivityDescription(activityType)
                + " at " + formattedDate);

        activityLogMongoTemplate.save(log);
    }

    public List<ActivityLogDocument> getAllLogs() {
        return activityLogRepository.findAll();
    }


    private String getActivityDescription(String activityType) {
        switch (activityType) {
            case "LOGIN": return "logged in";
            case "LOGOUT": return "logged out";
            case "PAYMENT_UPDATE": return "changed payment status";
            case "PROCESS_UPDATE": return "changed process status";
            case "PLAN_CREATED": return "created a new plan";
            default: return "performed " + activityType;
        }
    }
}
