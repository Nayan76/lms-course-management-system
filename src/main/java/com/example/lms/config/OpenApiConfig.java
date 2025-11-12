package com.example.lms.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {
     @Bean
    public OpenAPI lmsOpenAPI() {
        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("LMS Course Management API")
                        .description("REST API for managing course offerings, registrations, cancellations, and allotments.")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Nayan LMS Project")
                                .email("support@lmsapp.com")
                                .url("https://github.com/nayan-lms"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("LMS CLI & REST API Reference")
                        .url("https://springdoc.org"));
    }
}
