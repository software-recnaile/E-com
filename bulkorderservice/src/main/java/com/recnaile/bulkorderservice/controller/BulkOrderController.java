package com.recnaile.bulkorderservice.controller;

import com.recnaile.bulkorderservice.model.BulkOrderDocument;
import com.recnaile.bulkorderservice.model.BulkOrderRequest;
import com.recnaile.bulkorderservice.service.ActivityLogService;
import com.recnaile.bulkorderservice.service.EmailService;
import com.recnaile.bulkorderservice.service.GoogleSheetsService;
import com.recnaile.bulkorderservice.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.security.PermitAll;
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

    @Autowired
    private OrderStatusService orderStatusService;

    @Autowired
    private ActivityLogService activityLogService;

    @PostMapping
    public ResponseEntity<String> submitBulkOrder(@RequestBody BulkOrderRequest request) {
        try {
            String referenceId = "BULK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);

            googleSheetsService.saveBulkOrder(request, referenceId, timestamp);

            emailService.sendBulkOrderEmail(request, referenceId, timestamp, false);
            emailService.sendBulkOrderEmail(request, referenceId, timestamp, true);

            activityLogService.logOrderCreated(referenceId, request.getEmail());
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

    @PatchMapping("/{referenceId}/process-status")
    public ResponseEntity<BulkOrderDocument> updateProcessStatus(
            @PathVariable String referenceId,
            @RequestParam String status,
            @RequestParam String changedByEmail,
            @RequestParam(required = false) String notes) {

        BulkOrderDocument.OrderProcessStatus newStatus =
                BulkOrderDocument.OrderProcessStatus.valueOf(status.toUpperCase());

        BulkOrderDocument updatedOrder = orderStatusService.updateProcessStatus(
                referenceId, newStatus, changedByEmail, notes);

        // Get old status from the document before update
        String oldStatus = updatedOrder.getProcessStatus().name();

        activityLogService.logProcessStatusChange(
                referenceId,
                oldStatus,
                status,
                changedByEmail);

        return ResponseEntity.ok(updatedOrder);
    }

    @PatchMapping("/{referenceId}/payment-status")
    public ResponseEntity<BulkOrderDocument> updatePaymentStatus(
            @PathVariable String referenceId,
            @RequestParam String status,
            @RequestParam String changedByEmail,
            @RequestParam(required = false) String notes) {

        BulkOrderDocument.PaymentStatus newStatus =
                BulkOrderDocument.PaymentStatus.valueOf(status.toUpperCase());

        BulkOrderDocument updatedOrder = orderStatusService.updatePaymentStatus(
                referenceId, newStatus, changedByEmail, notes);

        // Get old status from the document before update
        String oldStatus = updatedOrder.getPaymentStatus().name();

        activityLogService.logPaymentStatusChange(
                referenceId,
                oldStatus,
                status,
                changedByEmail);

        return ResponseEntity.ok(updatedOrder);
    }

    @GetMapping("/{referenceId}/status-history")
    public ResponseEntity<List<BulkOrderDocument.StatusHistoryEntry>> getStatusHistory(
            @PathVariable String referenceId) {
        BulkOrderDocument order = orderStatusService.getOrderByReferenceId(referenceId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        return ResponseEntity.ok(order.getStatusHistory());
    }
}