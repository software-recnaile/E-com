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

    public void log(String email, String referenceId, String activityType, String status) {
        ActivityLogDocument log = new ActivityLogDocument();
        log.setEmail(email);
        log.setReferenceId(referenceId);  // Store referenceId in the document if needed
        log.setActivityType(activityType);
        log.setTimestamp(LocalDateTime.now());

        String formattedDate = log.getTimestamp()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a"));

        String description = referenceId + " (" + email + ") "
                + getActivityDescription(activityType, status)
                + " at " + formattedDate;

        log.setDescription(description);

        activityLogMongoTemplate.save(log);
    }

    public List<ActivityLogDocument> getAllLogs() {
        return activityLogRepository.findAll();
    }


    private String getActivityDescription(String activityType, String status) {
        switch (activityType) {
            case "LOGIN":
                return "logged in";
            case "LOGOUT":
                return "logged out";
            case "PAYMENT_UPDATE":
                return "changed payment status to " + status;
            case "PROCESS_UPDATE":
                return "changed process status to " + status;
            case "PLAN_CREATED":
                return "created a new plan";
            default:
                return "performed " + activityType;
        }
    }

}
