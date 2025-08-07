package com.recnaile.mailService.repository;

import com.recnaile.mailService.model.DronePlanDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface DronePlanRepository extends MongoRepository<DronePlanDocument, String> {
    DronePlanDocument findByReferenceId(String referenceId);

    @Query(value = "{'paymentStatus': ?0}")
    List<DronePlanDocument> findByPaymentStatus(DronePlanDocument.PaymentStatus status);

    @Query(value = "{'processStatus': ?0}")
    List<DronePlanDocument> findByProcessStatus(DronePlanDocument.ProcessStatus status);

    void deleteByReferenceId(String referenceId);
}