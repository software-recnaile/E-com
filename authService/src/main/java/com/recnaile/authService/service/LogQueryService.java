package com.recnaile.authService.service;

import com.recnaile.authService.model.UserActivityLog;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogQueryService {

    private final MongoTemplate logsMongoTemplate;

    public LogQueryService(@Qualifier("logsMongoTemplate") MongoTemplate logsMongoTemplate) {
        this.logsMongoTemplate = logsMongoTemplate;
    }

    // Fetch all logs
    public List<UserActivityLog> getAllLogs() {
        return logsMongoTemplate.findAll(UserActivityLog.class, "user_activity_logs");
    }

    // Fetch logs by email
    public List<UserActivityLog> getLogsByEmail(String email) {
        Query query = new Query(Criteria.where("email").is(email));
        return logsMongoTemplate.find(query, UserActivityLog.class, "user_activity_logs");
    }

    // Fetch logs by activity type
    public List<UserActivityLog> getLogsByActivityType(String activityType) {
        Query query = new Query(Criteria.where("activityType").is(activityType));
        return logsMongoTemplate.find(query, UserActivityLog.class, "user_activity_logs");
    }

    // Fetch logs between two timestamps
    public List<UserActivityLog> getLogsBetweenTimestamps(String start, String end) {
        Query query = new Query(Criteria.where("timestamp").gte(start).lte(end));
        return logsMongoTemplate.find(query, UserActivityLog.class, "user_activity_logs");
    }
}
