package com.recnaile.mailService.controller;

import com.recnaile.mailService.model.BulkOrderRequest;
import com.recnaile.mailService.service.EmailService;
import com.recnaile.mailService.service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/bulk-orders")
public class BulkOrderController {

    private final ExcelService excelService;
    private final EmailService emailService;

    @Autowired
    public BulkOrderController(ExcelService excelService, EmailService emailService) {
        this.excelService = excelService;
        this.emailService = emailService;
    }

    @PostMapping
    public ResponseEntity<byte[]> submitBulkOrder(@RequestBody BulkOrderRequest orderRequest) {
        try {
            // Generate order reference
            String orderRef = "ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            orderRequest.setOrderReference(orderRef);

            // Generate Excel file
            byte[] excelBytes = excelService.createBulkOrderExcel(orderRequest);

            // Append to master Excel file
            excelService.appendToMasterExcel(orderRequest);

            // Send emails with attachment
            emailService.sendBulkOrderEmails(orderRequest, excelBytes);

            // Return Excel file for download
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"Order_" + orderRef + ".xlsx\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelBytes);

        } catch (Exception e) {
            throw new RuntimeException("Failed to process bulk order", e);
        }
    }
}