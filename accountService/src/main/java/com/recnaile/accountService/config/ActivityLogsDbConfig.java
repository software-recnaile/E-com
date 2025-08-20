package com.recnaile.accountService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.recnaile.accountService.repository.activity",
        mongoTemplateRef = "activityLogTemplate"
)
public class ActivityLogsDbConfig {
    // This configures all activity repositories to use the activityLogTemplate
}