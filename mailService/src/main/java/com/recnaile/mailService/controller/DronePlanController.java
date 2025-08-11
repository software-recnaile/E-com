package com.recnaile.mailService.controller;

import com.recnaile.mailService.model.DronePlanDocument;
import com.recnaile.mailService.model.DronePlanForm;
import com.recnaile.mailService.service.DronePlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drone-plans")
public class DronePlanController {

    @Autowired
    private DronePlanService dronePlanService;

    @Autowired
    private ActivityLogService activityLogService;

    @GetMapping
    public ResponseEntity<List<ActivityLogDocument>> getAllLogs() {
        return ResponseEntity.ok(activityLogService.getAllLogs());
    }

    @PostMapping
    public ResponseEntity<DronePlanDocument> create(@RequestBody DronePlanForm form) {
        return ResponseEntity.ok(dronePlanService.createDronePlan(form));
    }

    @GetMapping
    public ResponseEntity<List<DronePlanDocument>> getAll() {
        return ResponseEntity.ok(dronePlanService.getAllDronePlans());
    }

    @GetMapping("/{referenceId}")
    public ResponseEntity<DronePlanDocument> getByReferenceId(@PathVariable String referenceId) {
        DronePlanDocument document = dronePlanService.getDronePlanByReferenceId(referenceId);
        return document != null ? ResponseEntity.ok(document) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{referenceId}")
    public ResponseEntity<DronePlanDocument> update(
            @PathVariable String referenceId,
            @RequestBody DronePlanForm form) {
        DronePlanDocument document = dronePlanService.updateDronePlan(referenceId, form);
        return document != null ? ResponseEntity.ok(document) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{referenceId}")
    public ResponseEntity<Void> delete(@PathVariable String referenceId) {
        dronePlanService.deleteDronePlan(referenceId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{referenceId}/payment-status")
    public ResponseEntity<DronePlanDocument> updatePaymentStatus(
            @PathVariable String referenceId,
            @RequestParam DronePlanDocument.PaymentStatus status,
            @RequestParam String email) {
        DronePlanDocument document = dronePlanService.updatePaymentStatus(referenceId, status, email);
        return document != null ? ResponseEntity.ok(document) : ResponseEntity.notFound().build();
    }

    @PatchMapping("/{referenceId}/process-status")
    public ResponseEntity<DronePlanDocument> updateProcessStatus(
            @PathVariable String referenceId,
            @RequestParam DronePlanDocument.ProcessStatus status,
            @RequestParam String email) {
        DronePlanDocument document = dronePlanService.updateProcessStatus(referenceId, status, email);
        return document != null ? ResponseEntity.ok(document) : ResponseEntity.notFound().build();
    }

    @GetMapping("/payment-status/{status}")
    public ResponseEntity<List<DronePlanDocument>> getByPaymentStatus(
            @PathVariable DronePlanDocument.PaymentStatus status) {
        return ResponseEntity.ok(dronePlanService.getDronePlansByPaymentStatus(status));
    }

    @GetMapping("/process-status/{status}")
    public ResponseEntity<List<DronePlanDocument>> getByProcessStatus(
            @PathVariable DronePlanDocument.ProcessStatus status) {
        return ResponseEntity.ok(dronePlanService.getDronePlansByProcessStatus(status));
    }

}
