package com.recnaile.authService.controller;

import com.recnaile.authService.model.UserActivityLog;
import com.recnaile.authService.repository.UserActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    @Autowired
    private UserActivityLogRepository logRepository;

    @GetMapping
    public List<UserActivityLog> getAllLogs() {
        return logRepository.findAll();
    }
}