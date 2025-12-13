package com.recnaile.bulkorderservice.controller;

import com.recnaile.bulkorderservice.service.GoogleSheetsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final GoogleSheetsService googleSheetsService;

    @GetMapping("/sheets-connection")
    public ResponseEntity<?> testSheetsConnection() {
        boolean isConnected = googleSheetsService.testConnection();
        Map<String, Object> credentialsInfo = googleSheetsService.checkCredentialsHealth();

        if (isConnected) {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Successfully connected to Google Sheets",
                    "credentialsInfo", credentialsInfo
            ));
        } else {
            return ResponseEntity.status(500).body(Map.of(
                    "status", "error",
                    "message", "Failed to connect to Google Sheets",
                    "credentialsInfo", credentialsInfo
            ));
        }
    }

    @GetMapping("/credentials-info")
    public ResponseEntity<?> getCredentialsInfo() {
        return ResponseEntity.ok(googleSheetsService.checkCredentialsHealth());
    }
}