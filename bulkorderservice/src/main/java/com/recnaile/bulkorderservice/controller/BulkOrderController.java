//
//package com.recnaile.bulkorderservice.controller;
//
//import com.recnaile.bulkorderservice.model.BulkOrderRequest;
//import com.recnaile.bulkorderservice.service.EmailService;
//import com.recnaile.bulkorderservice.service.GoogleSheetsService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/bulk-orders")
//public class BulkOrderController {
//
//    @Autowired
//    private EmailService emailService;
//
//    @Autowired
//    private GoogleSheetsService googleSheetsService;
//
//    @PostMapping
//    public ResponseEntity<String> submitBulkOrder(@RequestBody BulkOrderRequest request) {
//        try {
//            // Generate reference ID and timestamp
//            String referenceId = "BULK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
////            String timestamp = LocalDateTime.now()
////                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
//
//            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
//
//            // Save to Google Sheets and MongoDB
//            googleSheetsService.saveBulkOrder(request, referenceId, timestamp);
//
//            // Send emails
//            emailService.sendBulkOrderEmail(request, referenceId, timestamp, false); // To admin
//            emailService.sendBulkOrderEmail(request, referenceId, timestamp, true);  // To customer
//
//            return ResponseEntity.ok("{\"message\": \"Bulk order processed successfully\", \"referenceId\": \"" +
//                    referenceId + "\"}");
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError()
//                    .body("{\"error\": \"Error processing bulk order: " + e.getMessage() + "\"}");
//        }
//    }
//}


package com.recnaile.bulkorderservice.controller;

import com.recnaile.bulkorderservice.model.BulkOrderDocument;
import com.recnaile.bulkorderservice.model.BulkOrderRequest;
import com.recnaile.bulkorderservice.service.EmailService;
import com.recnaile.bulkorderservice.service.GoogleSheetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bulk-orders")
public class BulkOrderController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private GoogleSheetsService googleSheetsService;


    @PostMapping
    public ResponseEntity<String> submitBulkOrder(@RequestBody BulkOrderRequest request) {
        try {
            String referenceId = "BULK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            googleSheetsService.saveBulkOrder(request, referenceId, timestamp);

            emailService.sendBulkOrderEmail(request, referenceId, timestamp, false);
            emailService.sendBulkOrderEmail(request, referenceId, timestamp, true);

            return ResponseEntity.ok("{\"message\": \"Bulk order processed successfully\", \"referenceId\": \"" +
                    referenceId + "\"}");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"Error processing bulk order: " + e.getMessage() + "\"}");
        }
    }

    @GetMapping
    public ResponseEntity<List<BulkOrderDocument>> getAllOrders() {
        try {
            List<BulkOrderDocument> orders = googleSheetsService.getAllOrders();
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{referenceId}")
    public ResponseEntity<BulkOrderDocument> getOrderByReferenceId(@PathVariable String referenceId) {
        try {
            return googleSheetsService.getOrderByReferenceId(referenceId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }



}