package com.recnaile.mailService.repository;

import com.recnaile.mailService.model.ActivityLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLogDocument, String> {
    List<ActivityLogDocument> findByReferenceId(String referenceId);
    List<ActivityLogDocument> findByEmail(String email);
    List<ActivityLogDocument> findByActivityType(String activityType);
}