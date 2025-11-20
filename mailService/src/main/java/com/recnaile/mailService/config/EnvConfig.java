package com.recnaile.mailService.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;



@Configuration
@Profile("local")
public class EnvConfig {

    private static final Logger logger = LoggerFactory.getLogger(EnvConfig.class);

    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMissing()
                    .filename(".env.local")
                    .load();

            if (dotenv.entries().isEmpty()) {
                dotenv = Dotenv.configure()
                        .ignoreIfMissing()
                        .filename(".env")
                        .load();
            }

            int loadedVars = 0;
            for (var entry : dotenv.entries()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (System.getProperty(key) == null) {
                    System.setProperty(key, value);
                    loadedVars++;
                }
            }

            logger.info("Loaded {} environment variables from .env file", loadedVars);

        } catch (Exception e) {
            logger.warn("Failed to load .env file: {}", e.getMessage());
        }
    }
}