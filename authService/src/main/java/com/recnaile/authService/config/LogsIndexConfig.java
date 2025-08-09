package com.recnaile.authService.config;

import com.recnaile.authService.model.UserActivityLog;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.index.IndexOperations;
//import javax.annotation.PostConstruct;

@Configuration
public class LogsIndexConfig {

    private final MongoTemplate logsMongoTemplate;

    public LogsIndexConfig(@Qualifier("logsMongoTemplate") MongoTemplate logsMongoTemplate) {
        this.logsMongoTemplate = logsMongoTemplate;
    }

    @PostConstruct
    public void createIndexes() {
        IndexOperations indexOps = logsMongoTemplate.indexOps(UserActivityLog.class);

        // Index for email field
        indexOps.ensureIndex(new Index().on("email", Sort.Direction.ASC));

        // Index for activityType field
        indexOps.ensureIndex(new Index().on("activityType", Sort.Direction.ASC));

        // Index for timestamp field (most important for querying logs by time)
        indexOps.ensureIndex(new Index().on("timestamp", Sort.Direction.DESC));

        // Compound index for email and timestamp
        indexOps.ensureIndex(new Index()
                .on("email", Sort.Direction.ASC)
                .on("timestamp", Sort.Direction.DESC));
    }
}