package com.recnaile.bulkorderservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class SimpleMongoConfig {

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(new SimpleMongoClientDatabaseFactory(
                "mongodb+srv://software:%24Tech2025@recnaile.ffukcey.mongodb.net/admin-db?retryWrites=true&w=majority&appName=Recnaile"
        ));
    }
}