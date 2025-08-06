package com.recnaile.bulkorderservice.controller;

import com.recnaile.bulkorderservice.model.BulkOrderDocument;
import com.recnaile.bulkorderservice.service.OrderStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderStatusController {

    private final OrderStatusService orderStatusService;

    @Autowired
    public OrderStatusController(OrderStatusService orderStatusService) {
        this.orderStatusService = orderStatusService;
    }

    @PatchMapping("/{referenceId}/process-status")
    public ResponseEntity<BulkOrderDocument> updateProcessStatus(
            @PathVariable String referenceId,
            @RequestParam String status,
            @RequestParam String changedBy,
            @RequestParam(required = false) String notes) {

        BulkOrderDocument.OrderProcessStatus newStatus =
                BulkOrderDocument.OrderProcessStatus.valueOf(status.toUpperCase());

        return ResponseEntity.ok(
                orderStatusService.updateProcessStatus(referenceId, newStatus, changedBy, notes)
        );
    }

    @PatchMapping("/{referenceId}/payment-status")
    public ResponseEntity<BulkOrderDocument> updatePaymentStatus(
            @PathVariable String referenceId,
            @RequestParam String status,
            @RequestParam String changedBy,
            @RequestParam(required = false) String notes) {

        BulkOrderDocument.PaymentStatus newStatus =
                BulkOrderDocument.PaymentStatus.valueOf(status.toUpperCase());

        return ResponseEntity.ok(
                orderStatusService.updatePaymentStatus(referenceId, newStatus, changedBy, notes)
        );
    }

    @GetMapping("/{referenceId}/status-history")
    public ResponseEntity<List<BulkOrderDocument.StatusHistoryEntry>> getStatusHistory(
            @PathVariable String referenceId) {
        BulkOrderDocument order = orderStatusService.getOrderByReferenceId(referenceId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        return ResponseEntity.ok(order.getStatusHistory());
    }
}