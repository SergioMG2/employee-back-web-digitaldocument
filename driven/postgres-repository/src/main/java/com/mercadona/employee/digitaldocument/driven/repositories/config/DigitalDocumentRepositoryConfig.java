package com.mercadona.employee.digitaldocument.driven.repositories.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.mercadona.employee.digitaldocument.driven.repositories.models")
@EnableJpaRepositories("com.mercadona.employee.digitaldocument.driven.repositories")
public class DigitalDocumentRepositoryConfig {
}
