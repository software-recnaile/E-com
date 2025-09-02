//package com.recnaile.mailService.service;
//
//import com.recnaile.mailService.service.ActivityLogService;
//import com.recnaile.mailService.model.DronePlanDocument;
//import com.recnaile.mailService.model.DronePlanForm;
//import com.recnaile.mailService.repository.DronePlanRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//@Service
//public class DronePlanService {
//
//    @Autowired
//    private DronePlanRepository repository;
//
//    @Autowired
//    private GoogleSheetsService googleSheetsService;
//
//    @Autowired
//    private EmailService emailService;
//
//    @Autowired
//    private ActivityLogService activityLogService;
//
//    public DronePlanDocument createDronePlan(DronePlanForm form) {
//        String referenceId = "CUS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
//        LocalDateTime timestamp = LocalDateTime.now();
//
//        DronePlanDocument document = new DronePlanDocument();
//        document.setReferenceId(referenceId);
//        document.setTimestamp(timestamp);
//        document.setEmail(form.getEmail());
//        document.setRequirements(form.getRequirements());
//        document.setDroneType(form.getDroneType());
//        document.setBudget(form.getBudget());
//        document.setTimeline(form.getTimeline());
//        document.setFeatures(form.getFeatures());
//
//        DronePlanDocument savedDocument = repository.save(document);
//
//        // Save to Google Sheets
//        googleSheetsService.saveDronePlan(form, referenceId,
//                timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
//
//        // Send Emails
//        emailService.sendDronePlanEmail(form, referenceId, timestamp.toString(), false);
//        emailService.sendDronePlanEmail(form, referenceId, timestamp.toString(), true);
//
//        // Log the action to activity-logs DB
//        // activityLogService.log(form.getEmail(), referenceId, "PLAN_CREATED");
//
//        activityLogService.log(form.getEmail(), referenceId, "PLAN_CREATED", "");
//
//        return savedDocument;
//    }
//
//    public List<DronePlanDocument> getAllDronePlans() {
//        return repository.findAll();
//    }
//
//    public DronePlanDocument getDronePlanByReferenceId(String referenceId) {
//        return repository.findByReferenceId(referenceId);
//    }
//
//    public DronePlanDocument updateDronePlan(String referenceId, DronePlanForm form) {
//        Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
//        return optionalDocument
//                .map(existing -> {
//                    existing.setRequirements(form.getRequirements());
//                    existing.setDroneType(form.getDroneType());
//                    existing.setBudget(form.getBudget());
//                    existing.setTimeline(form.getTimeline());
//                    existing.setFeatures(form.getFeatures());
//                    return repository.save(existing);
//                })
//                .orElse(null);
//    }
//
//    public void deleteDronePlan(String referenceId) {
//        DronePlanDocument document = repository.findByReferenceId(referenceId);
//        if (document != null) {
//            repository.delete(document);
//        }
//    }
//
//    // public DronePlanDocument updatePaymentStatus(String referenceId, DronePlanDocument.PaymentStatus status, String email) {
//    //     Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
//    //     return optionalDocument
//    //             .map(document -> {
//    //                 document.setPaymentStatus(status);
//    //                 activityLogService.log(email, getUsernameFromEmail(email), "PAYMENT_UPDATE");
//    //                 return repository.save(document);
//    //             })
//    //             .orElse(null);
//    // }
//
//    // public DronePlanDocument updateProcessStatus(String referenceId, DronePlanDocument.ProcessStatus status, String email) {
//    //     Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
//    //     return optionalDocument
//    //             .map(document -> {
//    //                 document.setProcessStatus(status);
//    //                 activityLogService.log(email, getUsernameFromEmail(email), "PROCESS_UPDATE");
//    //                 return repository.save(document);
//    //             })
//    //             .orElse(null);
//    // }
//
//    public DronePlanDocument updatePaymentStatus(String referenceId, DronePlanDocument.PaymentStatus status, String email) {
//        Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
//        return optionalDocument
//                .map(document -> {
//                    document.setPaymentStatus(status);
//                    activityLogService.log(email, referenceId, "PAYMENT_UPDATE", status.name());
//                    return repository.save(document);
//                })
//                .orElse(null);
//    }
//
//    public DronePlanDocument updateProcessStatus(String referenceId, DronePlanDocument.ProcessStatus status, String email) {
//        Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
//        return optionalDocument
//                .map(document -> {
//                    document.setProcessStatus(status);
//                    activityLogService.log(email, referenceId, "PROCESS_UPDATE", status.name());
//                    return repository.save(document);
//                })
//                .orElse(null);
//    }
//
//    public List<DronePlanDocument> getDronePlansByPaymentStatus(DronePlanDocument.PaymentStatus status) {
//        return repository.findByPaymentStatus(status);
//    }
//
//    public List<DronePlanDocument> getDronePlansByProcessStatus(DronePlanDocument.ProcessStatus status) {
//        return repository.findByProcessStatus(status);
//    }
//
//    private String getUsernameFromEmail(String email) {
//        return email != null && email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
//    }
//}
//
//
//
//
//


package com.recnaile.mailService.service;

import com.recnaile.mailService.model.DronePlanDocument;
import com.recnaile.mailService.model.DronePlanForm;
import com.recnaile.mailService.repository.DronePlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DronePlanService {

    @Autowired
    private DronePlanRepository repository;

    @Autowired
    private GoogleSheetsService googleSheetsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ActivityLogService activityLogService;

    public DronePlanDocument createDronePlan(DronePlanForm form) {
        String referenceId = "CUS-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        LocalDateTime timestamp = LocalDateTime.now();

        DronePlanDocument document = new DronePlanDocument();
        document.setReferenceId(referenceId);
        document.setTimestamp(timestamp);
        document.setEmail(form.getEmail());
        document.setRequirements(form.getRequirements());
        document.setDroneType(form.getDroneType());
        document.setBudget(form.getBudget());
        document.setTimeline(form.getTimeline());
        document.setFeatures(form.getFeatures());

        DronePlanDocument savedDocument = repository.save(document);

        // Save to Google Sheets
        googleSheetsService.saveDronePlan(form, referenceId,
                timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        // Send Emails
        emailService.sendDronePlanEmail(form, referenceId, timestamp.toString(), false);
        emailService.sendDronePlanEmail(form, referenceId, timestamp.toString(), true);

        // Log the action to activity-logs DB
        activityLogService.log(form.getEmail(), referenceId, "PLAN_CREATED",
                "Drone plan created with reference: " + referenceId);

        return savedDocument;
    }

    public List<DronePlanDocument> getAllDronePlans() {
        return repository.findAll();
    }

    public DronePlanDocument getDronePlanByReferenceId(String referenceId) {
        return repository.findByReferenceId(referenceId);
    }

    public DronePlanDocument updateDronePlan(String referenceId, DronePlanForm form, String updatedBy) {
        Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
        return optionalDocument
                .map(existing -> {
                    existing.setRequirements(form.getRequirements());
                    existing.setDroneType(form.getDroneType());
                    existing.setBudget(form.getBudget());
                    existing.setTimeline(form.getTimeline());
                    existing.setFeatures(form.getFeatures());

                    DronePlanDocument updated = repository.save(existing);

                    // Log the update
                    activityLogService.log(updatedBy, referenceId, "PLAN_UPDATED",
                            "Drone plan updated by: " + updatedBy);

                    return updated;
                })
                .orElse(null);
    }

    public void deleteDronePlan(String referenceId, String deletedBy) {
        DronePlanDocument document = repository.findByReferenceId(referenceId);
        if (document != null) {
            repository.delete(document);

            // Log the deletion
            activityLogService.log(deletedBy, referenceId, "PLAN_DELETED",
                    "Drone plan deleted by: " + deletedBy);
        }
    }

    public DronePlanDocument updatePaymentStatus(String referenceId, DronePlanDocument.PaymentStatus status, String email) {
        Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
        return optionalDocument
                .map(document -> {
                    DronePlanDocument.PaymentStatus oldStatus = document.getPaymentStatus();
                    document.setPaymentStatus(status);

                    DronePlanDocument updated = repository.save(document);

                    // Log the payment status update
                    activityLogService.log(email, referenceId, "PAYMENT_UPDATE",
                            "Payment status changed from " + oldStatus + " to " + status);

                    return updated;
                })
                .orElse(null);
    }

    public DronePlanDocument updateProcessStatus(String referenceId, DronePlanDocument.ProcessStatus status, String email) {
        Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
        return optionalDocument
                .map(document -> {
                    DronePlanDocument.ProcessStatus oldStatus = document.getProcessStatus();
                    document.setProcessStatus(status);

                    DronePlanDocument updated = repository.save(document);

                    // Log the process status update
                    activityLogService.log(email, referenceId, "PROCESS_UPDATE",
                            "Process status changed from " + oldStatus + " to " + status);

                    return updated;
                })
                .orElse(null);
    }

    public List<DronePlanDocument> getDronePlansByPaymentStatus(DronePlanDocument.PaymentStatus status) {
        return repository.findByPaymentStatus(status);
    }

    public List<DronePlanDocument> getDronePlansByProcessStatus(DronePlanDocument.ProcessStatus status) {
        return repository.findByProcessStatus(status);
    }

    private String getUsernameFromEmail(String email) {
        return email != null && email.contains("@") ? email.substring(0, email.indexOf("@")) : email;
    }
}