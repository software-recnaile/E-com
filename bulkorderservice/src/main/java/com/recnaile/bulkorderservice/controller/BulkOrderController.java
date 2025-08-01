package com.recnaile.bulkorderservice.controller;

import com.recnaile.bulkorderservice.model.BulkOrderRequest;
import com.recnaile.bulkorderservice.service.EmailService;
import com.recnaile.bulkorderservice.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
@RequestMapping("/api/bulk-orders")
public class BulkOrderController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private ExcelService excelService;

    @PostMapping
    public ResponseEntity<String> submitBulkOrder(@RequestBody BulkOrderRequest request) {
        try {
            // Generate reference ID and timestamp
            String referenceId = "BULK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

            // Save to Excel
            excelService.saveBulkOrderToExcel(request, referenceId, timestamp);

            // Send emails
            emailService.sendBulkOrderEmail(request, referenceId, timestamp, false); // To admin
            emailService.sendBulkOrderEmail(request, referenceId, timestamp, true);  // To customer

            return ResponseEntity.ok("{\"message\": \"Bulk order processed successfully\", \"referenceId\": \"" +
                    referenceId + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Error processing bulk order: " + e.getMessage() + "\"}");
        }
    }
}