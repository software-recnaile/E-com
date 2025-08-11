//package com.recnaile.mailService.service;
//
//import com.recnaile.mailService.model.DronePlanDocument;
//import com.recnaile.mailService.model.DronePlanLog;
//import com.recnaile.mailService.repository.DronePlanLogRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//public class LoggingService {
//
//    @Autowired
//    private DronePlanLogRepository logRepository;
//
//    public void logCreation(String referenceId, DronePlanDocument document, String changedBy) {
//        DronePlanLog log = new DronePlanLog(
//                referenceId,
//                "CREATE",
//                "New drone plan created",
//                changedBy
//        );
//        log.setNewState(document);
//        logRepository.save(log);
//    }
//
//    public void logUpdate(String referenceId, DronePlanDocument previousState,
//                          DronePlanDocument newState, String changedBy) {
//        DronePlanLog log = new DronePlanLog(
//                referenceId,
//                "UPDATE",
//                "Drone plan updated",
//                changedBy
//        );
//        log.setPreviousState(previousState);
//        log.setNewState(newState);
//        logRepository.save(log);
//    }
//
//    public void logStatusChange(String referenceId, String statusType,
//                                String oldStatus, String newStatus, String changedBy) {
//        DronePlanLog log = new DronePlanLog(
//                referenceId,
//                "STATUS_CHANGE",
//                statusType + " changed from " + oldStatus + " to " + newStatus,
//                changedBy
//        );
//        logRepository.save(log);
//    }
//
//    public void logDeletion(String referenceId, DronePlanDocument document, String changedBy) {
//        DronePlanLog log = new DronePlanLog(
//                referenceId,
//                "DELETE",
//                "Drone plan deleted",
//                changedBy
//        );
//        log.setPreviousState(document);
//        logRepository.save(log);
//    }
//
//    // Add these methods to the existing LoggingService class
//
//    public List<DronePlanLog> getLogsByReferenceId(String referenceId) {
//        return logRepository.findByReferenceId(referenceId);
//    }
//
//    public List<DronePlanLog> getLogsByAdminEmail(String email) {
//        return logRepository.findByChangedBy(email);
//    }
//
//    public List<DronePlanLog> getLogsByAction(String action) {
//        return logRepository.findByAction(action);
//    }
//}