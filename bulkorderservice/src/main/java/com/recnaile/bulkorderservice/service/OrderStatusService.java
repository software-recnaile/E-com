package com.recnaile.bulkorderservice.service;

import com.recnaile.bulkorderservice.model.BulkOrderDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class OrderStatusService {

    private final MongoTemplate accountMongoTemplate;
    @Autowired
    private ActivityLogService activityLogService;


    @Autowired
    public OrderStatusService(@Qualifier("accountMongoTemplate") MongoTemplate accountMongoTemplate) {
        this.accountMongoTemplate = accountMongoTemplate;
    }

    public BulkOrderDocument updateProcessStatus(String referenceId,
                                                 BulkOrderDocument.OrderProcessStatus newStatus,
                                                 String changedBy,
                                                 String notes) {
        Query query = Query.query(Criteria.where("referenceId").is(referenceId));
        BulkOrderDocument order = accountMongoTemplate.findOne(query, BulkOrderDocument.class, "bulk_orders");

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        BulkOrderDocument.OrderProcessStatus oldStatus = order.getProcessStatus();

        Update update = new Update()
                .set("processStatus", newStatus)
                .push("statusHistory", createStatusHistoryEntry(
                        BulkOrderDocument.StatusHistoryEntry.StatusType.PROCESS,
                        oldStatus.toString(),
                        newStatus.toString(),
                        changedBy,
                        notes
                ));


        accountMongoTemplate.updateFirst(query, update, BulkOrderDocument.class, "bulk_orders");

        // Return the updated document
        return accountMongoTemplate.findOne(query, BulkOrderDocument.class, "bulk_orders");
    }

    public BulkOrderDocument updatePaymentStatus(String referenceId,
                                                 BulkOrderDocument.PaymentStatus newStatus,
                                                 String changedBy,
                                                 String notes) {
        Query query = Query.query(Criteria.where("referenceId").is(referenceId));
        BulkOrderDocument order = accountMongoTemplate.findOne(query, BulkOrderDocument.class, "bulk_orders");

        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        BulkOrderDocument.PaymentStatus oldStatus = order.getPaymentStatus();

        Update update = new Update()
                .set("paymentStatus", newStatus)
                .push("statusHistory", createStatusHistoryEntry(
                        BulkOrderDocument.StatusHistoryEntry.StatusType.PAYMENT,
                        oldStatus.toString(),
                        newStatus.toString(),
                        changedBy,
                        notes
                ));


        accountMongoTemplate.updateFirst(query, update, BulkOrderDocument.class, "bulk_orders");

        // Return the updated document
        return accountMongoTemplate.findOne(query, BulkOrderDocument.class, "bulk_orders");
    }

    public Optional<BulkOrderDocument> getOrderByReferenceId(String referenceId) {
        Query query = Query.query(Criteria.where("referenceId").is(referenceId));
        BulkOrderDocument order = accountMongoTemplate.findOne(query, BulkOrderDocument.class, "bulk_orders");
        return Optional.ofNullable(order);
    }

    private BulkOrderDocument.StatusHistoryEntry createStatusHistoryEntry(
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
        return entry;
    }
}