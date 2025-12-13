package com.recnaile.bulkorderservice.repository;

import com.recnaile.bulkorderservice.model.BulkOrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BulkOrderRepository extends MongoRepository<BulkOrderDocument, String> {
    Optional<BulkOrderDocument> findByReferenceId(String referenceId);
    List<BulkOrderDocument> findByProcessStatus(BulkOrderDocument.OrderProcessStatus status);
    List<BulkOrderDocument> findByPaymentStatus(BulkOrderDocument.PaymentStatus status);
}