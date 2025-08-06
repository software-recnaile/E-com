package com.recnaile.bulkorderservice.service;

import com.recnaile.bulkorderservice.model.BulkOrderDocument;
import com.recnaile.bulkorderservice.repository.BulkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderStatusService {

    private final BulkOrderRepository bulkOrderRepository;

    @Autowired
    public OrderStatusService(BulkOrderRepository bulkOrderRepository) {
        this.bulkOrderRepository = bulkOrderRepository;
    }

    public BulkOrderDocument updateProcessStatus(String referenceId,
                                                 BulkOrderDocument.OrderProcessStatus newStatus,
                                                 String changedBy,
                                                 String notes) {
        BulkOrderDocument order = bulkOrderRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        BulkOrderDocument.OrderProcessStatus oldStatus = order.getProcessStatus();
        order.setProcessStatus(newStatus);
        addStatusHistory(order,
                BulkOrderDocument.StatusHistoryEntry.StatusType.PROCESS,
                oldStatus.toString(),
                newStatus.toString(),
                changedBy,
                notes);

        return bulkOrderRepository.save(order);
    }

    public BulkOrderDocument updatePaymentStatus(String referenceId,
                                                 BulkOrderDocument.PaymentStatus newStatus,
                                                 String changedBy,
                                                 String notes) {
        BulkOrderDocument order = bulkOrderRepository.findByReferenceId(referenceId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        BulkOrderDocument.PaymentStatus oldStatus = order.getPaymentStatus();
        order.setPaymentStatus(newStatus);
        addStatusHistory(order,
                BulkOrderDocument.StatusHistoryEntry.StatusType.PAYMENT,
                oldStatus.toString(),
                newStatus.toString(),
                changedBy,
                notes);

        return bulkOrderRepository.save(order);
    }
    public Optional<BulkOrderDocument> getOrderByReferenceId(String referenceId) {
        return bulkOrderRepository.findByReferenceId(referenceId);
    }

    private void addStatusHistory(BulkOrderDocument order,
                                  BulkOrderDocument.StatusHistoryEntry.StatusType statusType,
                                  String oldStatus,
                                  String newStatus,
                                  String changedBy,
                                  String notes) {
        BulkOrderDocument.StatusHistoryEntry entry = new BulkOrderDocument.StatusHistoryEntry();
        entry.setTimestamp(LocalDateTime.now());
        entry.setStatusType(statusType);
        entry.setOldStatus(oldStatus);
        entry.setNewStatus(newStatus);
        entry.setChangedBy(changedBy);
        entry.setNotes(notes);

        order.getStatusHistory().add(entry);
    }
}