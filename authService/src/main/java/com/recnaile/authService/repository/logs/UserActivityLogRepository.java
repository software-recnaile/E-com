package com.recnaile.authService.repository.logs;

import com.recnaile.authService.model.UserActivityLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserActivityLogRepository extends MongoRepository<UserActivityLog, String> {
}