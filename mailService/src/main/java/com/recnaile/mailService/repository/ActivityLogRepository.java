package com.recnaile.mailService.repository;

import com.recnaile.mailService.model.ActivityLogDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLogDocument, String> {
}
