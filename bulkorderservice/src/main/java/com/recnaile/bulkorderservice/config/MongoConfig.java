package com.recnaile.bulkorderservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;

@Configuration
public class MongoConfig {

    @Bean(name = "adminMongoProperties")
    @ConfigurationProperties(prefix = "spring.data.admin.mongodb")
    public MongoProperties adminMongoProperties() {
        return new MongoProperties();
    }

    @Bean(name = "accountMongoProperties")
    @Primary
    @ConfigurationProperties(prefix = "spring.data.account.mongodb")
    public MongoProperties accountMongoProperties() {
        return new MongoProperties();
    }
    @Bean(name = "activityMongoProperties")
    @ConfigurationProperties(prefix = "spring.data.activity.mongodb")
    public MongoProperties activityMongoProperties() {
        return new MongoProperties();
    }

    @Bean(name = "adminMongoTemplate")
    public MongoTemplate adminMongoTemplate() throws Exception {
        return new MongoTemplate(adminMongoFactory(adminMongoProperties()));
    }

    @Primary
    @Bean(name = "accountMongoTemplate")
    public MongoTemplate accountMongoTemplate() throws Exception {
        return new MongoTemplate(accountMongoFactory(accountMongoProperties()));
    }
    @Bean(name = "activityMongoTemplate")
    public MongoTemplate activityMongoTemplate() throws Exception {
        return new MongoTemplate(activityMongoFactory(activityMongoProperties()));
    }

    @Bean
    public MongoDatabaseFactory adminMongoFactory(MongoProperties adminMongoProperties) throws Exception {
        return new SimpleMongoClientDatabaseFactory(adminMongoProperties.getUri());
    }

    @Primary
    @Bean
    public MongoDatabaseFactory accountMongoFactory(MongoProperties accountMongoProperties) throws Exception {
        return new SimpleMongoClientDatabaseFactory(accountMongoProperties.getUri());
    }

    @Bean
    public MongoDatabaseFactory activityMongoFactory(MongoProperties activityMongoProperties) throws Exception {
        return new SimpleMongoClientDatabaseFactory(activityMongoProperties.getUri());
    }
}