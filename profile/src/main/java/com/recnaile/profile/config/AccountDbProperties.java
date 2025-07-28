package com.recnaile.profile.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.data.mongodb.account-db")
public class AccountDbProperties {
    private String uri;
}
