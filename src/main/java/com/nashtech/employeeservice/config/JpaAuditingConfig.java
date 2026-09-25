package com.nashtech.employeeservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Isolated JPA Auditing configuration.
 * Keeping this distinct from the main application class allows WebMvc slice tests
 * to execute without requiring full JPA repository context.
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}
