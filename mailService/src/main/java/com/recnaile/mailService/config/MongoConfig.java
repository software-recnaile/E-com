package com.recnaile.mailService.config;

import com.mongodb.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {
    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(MongoClients.create("mongodb+srv://software:%24Tech2025@recnaile.ffukcey.mongodb.net/account-db?retryWrites=true&w=majority&appName=Recnaile"), "account-db");
    }
}
