package com.recnaile.authService.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = "com.recnaile.authService.repository",
        mongoTemplateRef = "logsMongoTemplate"
)
public class LogsMongoConfig {
}