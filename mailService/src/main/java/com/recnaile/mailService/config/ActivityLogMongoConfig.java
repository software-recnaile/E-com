package com.recnaile.mailService.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.recnaile.mailService.repository",
        mongoTemplateRef = "activityLogMongoTemplate"
)
public class ActivityLogMongoConfig {
    @Value("${spring.data.mongodb.activitylog.uri}")
    private String activityLogUri;

    @Bean(name = "activityLogMongoTemplate")
    public MongoTemplate activityLogMongoTemplate() {
        MongoClient client = MongoClients.create(activityLogUri);
        return new MongoTemplate(client, "activity-logs");
    }
}