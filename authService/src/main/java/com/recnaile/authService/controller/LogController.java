package com.recnaile.authService.controller;

import com.recnaile.authService.model.UserActivityLog;
import com.recnaile.authService.service.LogQueryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogQueryService logQueryService;

    public LogController(LogQueryService logQueryService) {
        this.logQueryService = logQueryService;
    }

    @GetMapping
    public List<UserActivityLog> getAllLogs() {
        return logQueryService.getAllLogs();
    }

    @GetMapping("/email/{email}")
    public List<UserActivityLog> getLogsByEmail(@PathVariable String email) {
        return logQueryService.getLogsByEmail(email);
    }

    @GetMapping("/activity/{activityType}")
    public List<UserActivityLog> getLogsByActivity(@PathVariable String activityType) {
        return logQueryService.getLogsByActivityType(activityType);
    }

    @GetMapping("/range")
    public List<UserActivityLog> getLogsInRange(@RequestParam String start, @RequestParam String end) {
        return logQueryService.getLogsBetweenTimestamps(start, end);
    }
}
