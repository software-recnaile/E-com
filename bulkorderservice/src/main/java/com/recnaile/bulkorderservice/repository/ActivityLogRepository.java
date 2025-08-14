package com.recnaile.bulkorderservice.repository;

import com.recnaile.bulkorderservice.model.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    List<ActivityLog> findAll();
}