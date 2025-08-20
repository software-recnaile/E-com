package com.recnaile.accountService.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = {
                "com.recnaile.accountService.repository",
                "com.recnaile.accountService.repository.product",
                "com.recnaile.accountService.repository.activity"
        },

        mongoTemplateRef = "accountDbTemplate"
)
public class MongoConfig {

    @Autowired
    private UserDbProperties userDbProperties;

    @Autowired
    private AccountDbProperties accountDbProperties;

    @Autowired
    private ProductDbProperties productDbProperties;

    @Autowired
    private ActivityLogsProperties activityLogsProperties;


    @Primary
    @Bean(name = "userDbTemplate")
    public MongoTemplate userDbTemplate() throws Exception {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(userDbProperties.getUri());
        return new MongoTemplate(factory);
    }

    @Bean(name = "accountDbTemplate")
    public MongoTemplate accountDbTemplate() throws Exception {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(accountDbProperties.getUri());
        return new MongoTemplate(factory);
    }

    @Bean(name = "productDbTemplate")
    public MongoTemplate productDbTemplate() throws Exception {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(productDbProperties.getUri());
        return new MongoTemplate(factory);
    }




    @Bean(name = "activityLogTemplate")
    public MongoTemplate activityLogTemplate() throws Exception {
        MongoDatabaseFactory factory = new SimpleMongoClientDatabaseFactory(activityLogsProperties.getUri());
        MongoTemplate template = new MongoTemplate(factory);

        System.out.println("Activity Log Template configured for DB: " +
                template.getDb().getName());
        return template;
    }
}