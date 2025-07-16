package org.ebndrnk.authorizationservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class that enables JPA auditing.
 * <p>
 * This allows automatic population of audit-related fields
 * such as createdBy, createdDate, etc.
 */
@Configuration
@EnableJpaAuditing
public class AuditConfig {
}
