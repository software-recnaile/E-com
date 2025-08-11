package com.recnaile.mailService.controller;

import com.recnaile.mailService.model.ActivityLogDocument;
import com.recnaile.mailService.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<List<ActivityLogDocument>> getAllLogs() {
        return ResponseEntity.ok(activityLogService.getAllLogs());
    }
}