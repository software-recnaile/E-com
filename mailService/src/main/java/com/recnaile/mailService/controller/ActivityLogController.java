package com.recnaile.mailService.controller;

import com.recnaile.mailService.model.ActivityLogDocument;
import com.recnaile.mailService.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mail-activity-logs")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<List<ActivityLogDocument>> getAllLogs() {
        return ResponseEntity.ok(activityLogService.getAllLogs());
    }

    @GetMapping("/reference/{referenceId}")
    public ResponseEntity<List<ActivityLogDocument>> getLogsByReferenceId(@PathVariable String referenceId) {
        return ResponseEntity.ok(activityLogService.getLogsByReferenceId(referenceId));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<List<ActivityLogDocument>> getLogsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(activityLogService.getLogsByEmail(email));
    }

    @GetMapping("/type/{activityType}")
    public ResponseEntity<List<ActivityLogDocument>> getLogsByActivityType(@PathVariable String activityType) {
        return ResponseEntity.ok(activityLogService.getLogsByActivityType(activityType));
    }

}
