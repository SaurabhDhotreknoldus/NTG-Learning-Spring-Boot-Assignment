package com.nashtech.employeeservice.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI 3.0 / Swagger configuration for API discovery, contract testing, and documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("NashTech Employee Management System REST API")
                        .version("1.0.0")
                        .description("Production-grade Spring Boot 4 REST API implementing layered architecture, " +
                                "domain validation, advanced derived and JPQL queries, pagination, and Docker containerization.")
                        .contact(new Contact()
                                .name("NashTech Global Engineering Team")
                                .email("engineering@nashtechglobal.com")
                                .url("https://www.nashtechglobal.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Development Server")
                ));
    }
}
