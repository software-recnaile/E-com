package com.recnaile.accountService.repository.activity;

import com.recnaile.accountService.model.ActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityLogRepository extends MongoRepository<ActivityLog, String> {
    // This will now use the activity-logs database connection
}