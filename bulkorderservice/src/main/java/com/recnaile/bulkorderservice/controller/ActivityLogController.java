package com.recnaile.bulkorderservice.controller;

import com.recnaile.bulkorderservice.model.ActivityLog;
import com.recnaile.bulkorderservice.service.ActivityLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bulkorder-activity-logs")
public class ActivityLogController {

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public List<ActivityLog> getAllActivityLogs() {
        return activityLogService.getAllLogs();
    }

}
