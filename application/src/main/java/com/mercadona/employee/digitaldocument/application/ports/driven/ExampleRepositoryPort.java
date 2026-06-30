package com.mercadona.employee.digitaldocument.application.ports.driven;

import com.mercadona.framework.cna.commons.interfaces.CNACrudRepository;
import com.mercadona.employee.digitaldocument.domain.Example;

public interface ExampleRepositoryPort extends CNACrudRepository<Example, Long> { }
