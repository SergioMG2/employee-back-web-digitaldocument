package com.mercadona.employee.digitaldocument.application.ports.driven;

import com.mercadona.employee.digitaldocument.domain.DigitalDocument;

public interface OutboxRepositoryPort {
    void publish(DigitalDocument document);
}
