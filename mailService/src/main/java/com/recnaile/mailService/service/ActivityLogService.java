//package com.recnaile.mailService.service;
//
//import com.recnaile.mailService.model.ActivityLogDocument;
//import com.recnaile.mailService.repository.ActivityLogRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class ActivityLogService {
//
//    @Autowired
//    private ActivityLogRepository activityLogRepository;
//
//    public void log(String email, String referenceId, String activityType, String details) {
//        ActivityLogDocument log = new ActivityLogDocument();
//        log.setEmail(email);
//        log.setReferenceId(referenceId);
//        log.setActivityType(activityType);
//        log.setDescription(details);
//        log.setTimestamp(LocalDateTime.now());
//
//        activityLogRepository.save(log);
//    }
//
//    public List<ActivityLogDocument> getAllLogs() {
//        System.out.println(activityLogRepository.findAll());
//        return activityLogRepository.findAll();
//    }
//
//    public List<ActivityLogDocument> getLogsByReferenceId(String referenceId) {
//        return activityLogRepository.findByReferenceId(referenceId);
//    }
//
//    public List<ActivityLogDocument> getLogsByEmail(String email) {
//        return activityLogRepository.findByEmail(email);
//    }
//
//    public List<ActivityLogDocument> getLogsByActivityType(String activityType) {
//        return activityLogRepository.findByActivityType(activityType);
//    }
//}



package com.recnaile.mailService.service;

import com.recnaile.mailService.model.ActivityLogDocument;
import com.recnaile.mailService.repository.ActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ActivityLogService {

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm a");

    public void log(String email, String referenceId, String activityType, String details) {
        ActivityLogDocument log = new ActivityLogDocument();
        log.setEmail(email);
        log.setReferenceId(referenceId);
        log.setActivityType(activityType);
        log.setTimestamp(LocalDateTime.now());

        // Format the description based on activity type
        String description = formatDescription(email, referenceId, activityType, details, log.getTimestamp());
        log.setDescription(description);

        activityLogRepository.save(log);
    }

    private String formatDescription(String email, String referenceId, String activityType,
                                     String details, LocalDateTime timestamp) {
        String formattedTime = timestamp.format(DATE_FORMATTER);

        switch (activityType) {
            case "PLAN_CREATED":
                return referenceId + " (" + email + ") created a new drone plan at " + formattedTime;

            case "PLAN_UPDATED":
                return referenceId + " (" + email + ") updated drone plan details at " + formattedTime;

            case "PLAN_DELETED":
                return referenceId + " (" + email + ") deleted drone plan at " + formattedTime;

            case "PAYMENT_UPDATE":
                return referenceId + " (" + email + ") changed payment status to " + details + " at " + formattedTime;

            case "PROCESS_UPDATE":
                return referenceId + " (" + email + ") changed process status to " + details + " at " + formattedTime;

            default:
                return referenceId + " (" + email + ") performed " + activityType + " at " + formattedTime;
        }
    }

    public List<ActivityLogDocument> getAllLogs() {
        return activityLogRepository.findAll();
    }

    public List<ActivityLogDocument> getLogsByReferenceId(String referenceId) {
        return activityLogRepository.findByReferenceId(referenceId);
    }

    public List<ActivityLogDocument> getLogsByEmail(String email) {
        return activityLogRepository.findByEmail(email);
    }

    public List<ActivityLogDocument> getLogsByActivityType(String activityType) {
        return activityLogRepository.findByActivityType(activityType);
    }
}