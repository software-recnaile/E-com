package com.recnaile.authService.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

@Configuration
public class MultipleMongoConfig {

    @Primary
    @Bean(name = "primaryProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.primary")
    public MongoProperties primaryProperties() {
        return new MongoProperties();
    }

    @Bean(name = "logsProperties")
    @ConfigurationProperties(prefix = "spring.data.mongodb.logs")
    public MongoProperties logsProperties() {
        return new MongoProperties();
    }

    @Primary
    @Bean(name = "primaryMongoFactory")
    public MongoDatabaseFactory primaryMongoFactory(
            @Qualifier("primaryProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoProperties.getUri());
    }

    @Bean(name = "logsMongoFactory")
    public MongoDatabaseFactory logsMongoFactory(
            @Qualifier("logsProperties") MongoProperties mongoProperties) {
        return new SimpleMongoClientDatabaseFactory(mongoProperties.getUri());
    }

    @Primary
    @Bean(name = "primaryMongoTemplate")
    public MongoTemplate primaryMongoTemplate(
            @Qualifier("primaryMongoFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }

    @Bean(name = "logsMongoTemplate")
    public MongoTemplate logsMongoTemplate(
            @Qualifier("logsMongoFactory") MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTemplate(mongoDatabaseFactory);
    }
}