package com.souhailbektachi.backend.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class OpenApiConfig {
    
    @Bean
    @Primary  // Add this annotation to make this bean the primary one
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Credit Management API")
                    .description("Spring Boot application for managing bank credits")
                    .version("v1.0.0")
                    .contact(new Contact()
                        .name("Souhail Bektachi")
                        .email("souhail.bektachi@example.com"))
                    .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                    .description("Credit Management Documentation")
                    .url("https://github.com/souhailbektachi"))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                    .addSecuritySchemes("bearerAuth", 
                        new SecurityScheme()
                            .name("bearerAuth")
                            .type(SecurityScheme.Type.HTTP)
                            .scheme("bearer")
                            .bearerFormat("JWT")));
    }
    
    // Additional configuration for Swagger UI if needed
}
