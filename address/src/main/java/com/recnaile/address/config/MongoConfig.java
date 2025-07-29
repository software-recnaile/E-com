package com.recnaile.address.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(
        basePackages = {
                "com.recnaile.address.repository",
                "com.recnaile.address.repository.product"
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
}