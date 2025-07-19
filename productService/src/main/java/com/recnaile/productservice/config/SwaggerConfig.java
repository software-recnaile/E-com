package com.recnaile.productservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI productServiceApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Product Service API")
                        .description("API for managing products")
                        .version("1.0"));
    }
}