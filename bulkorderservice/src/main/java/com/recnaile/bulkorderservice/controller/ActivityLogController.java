//package com.recnaile.bulkorderservice.controller;
//
//import com.recnaile.bulkorderservice.model.ActivityLog;
//import com.recnaile.bulkorderservice.repository.ActivityLogRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Sort;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/activity-logs")
//@PreAuthorize("hasRole('ADMIN')")
//public class ActivityLogController {
//
//    @Autowired
//    private ActivityLogRepository activityLogRepository;
//
//    @GetMapping
//    public ResponseEntity<List<ActivityLog>> getActivityLogs(
//            @RequestParam(required = false) String entityType,
//            @RequestParam(required = false) String entityId,
//            @RequestParam(required = false) String username) {
//
//        List<ActivityLog> logs;
//
//        if (entityType != null && entityId != null) {
//            logs = activityLogRepository.findByEntityTypeAndEntityId(entityType, entityId);
//        } else if (entityType != null) {
//            logs = activityLogRepository.findByEntityType(entityType);
//        } else if (entityId != null) {
//            logs = activityLogRepository.findByEntityId(entityId);
//        } else if (username != null) {
//            logs = activityLogRepository.findByUsername(username);
//        } else {
//            logs = activityLogRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
//        }
//
//        return ResponseEntity.ok(logs);
//    }
//}