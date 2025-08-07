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

    public DronePlanDocument createDronePlan(DronePlanForm form) {
        String referenceId = "DRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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

        googleSheetsService.saveDronePlan(form, referenceId,
                timestamp.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        emailService.sendDronePlanEmail(form, referenceId, timestamp.toString(), false);
        emailService.sendDronePlanEmail(form, referenceId, timestamp.toString(), true);

        return savedDocument;
    }

    public List<DronePlanDocument> getAllDronePlans() {
        return repository.findAll();
    }

    public DronePlanDocument getDronePlanByReferenceId(String referenceId) {
        return repository.findByReferenceId(referenceId);
    }

    public DronePlanDocument updateDronePlan(String referenceId, DronePlanForm form) {
        Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
        return optionalDocument
                .map(existing -> {
                    existing.setRequirements(form.getRequirements());
                    existing.setDroneType(form.getDroneType());
                    existing.setBudget(form.getBudget());
                    existing.setTimeline(form.getTimeline());
                    existing.setFeatures(form.getFeatures());
                    return repository.save(existing);
                })
                .orElse(null);
    }

    public void deleteDronePlan(String referenceId) {
        DronePlanDocument document = repository.findByReferenceId(referenceId);
        if (document != null) {
            repository.delete(document);
        }
    }

    public DronePlanDocument updatePaymentStatus(String referenceId, DronePlanDocument.PaymentStatus status) {
        Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
        return optionalDocument
                .map(document -> {
                    document.setPaymentStatus(status);
                    return repository.save(document);
                })
                .orElse(null);
    }

    public DronePlanDocument updateProcessStatus(String referenceId, DronePlanDocument.ProcessStatus status) {
        Optional<DronePlanDocument> optionalDocument = Optional.ofNullable(repository.findByReferenceId(referenceId));
        return optionalDocument
                .map(document -> {
                    document.setProcessStatus(status);
                    return repository.save(document);
                })
                .orElse(null);
    }

    public List<DronePlanDocument> getDronePlansByPaymentStatus(DronePlanDocument.PaymentStatus status) {
        return repository.findByPaymentStatus(status);
    }

    public List<DronePlanDocument> getDronePlansByProcessStatus(DronePlanDocument.ProcessStatus status) {
        return repository.findByProcessStatus(status);
    }
}