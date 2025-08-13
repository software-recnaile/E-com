package com.recnaile.mailService.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**, /**")  // Changed to cover all endpoints
                .allowedOrigins("http://localhost:5173")
    
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")  // Added PATCH
                .allowedHeaders("*")
                .allowCredentials(true);  // Added if you need credentials
    }
}


