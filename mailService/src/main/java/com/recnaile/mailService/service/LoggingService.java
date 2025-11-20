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
//        log.setDescription(details != null && !details.isEmpty() ? details : activityType + " performed");
//        log.setTimestamp(LocalDateTime.now());
//
//        activityLogRepository.save(log);
//    }
//
//    public List<ActivityLogDocument> getAllLogs() {
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