package com.recnaile.mailService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.recnaile.mailService.repository",
        mongoTemplateRef = "activityLogMongoTemplate"
)
public class ActivityLogMongoRepositoryConfig {
}