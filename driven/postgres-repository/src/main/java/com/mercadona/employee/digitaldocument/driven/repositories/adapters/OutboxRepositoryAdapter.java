package com.mercadona.employee.digitaldocument.driven.repositories.adapters;

import com.mercadona.employee.digitaldocument.application.ports.driven.OutboxRepositoryPort;
import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.driven.repositories.OutboxMOJpaRepository;
import com.mercadona.employee.digitaldocument.driven.repositories.models.OutboxMO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@Slf4j
@Service
@AllArgsConstructor
public class OutboxRepositoryAdapter implements OutboxRepositoryPort {

    private static final String AGGREGATE_TYPE = "employee-digital-document";

    private final OutboxMOJpaRepository outboxRepository;

    @Override
    public void publish(DigitalDocument document) {
        var payload = buildJsonPayload(document);

        var outboxMO = OutboxMO.builder()
                .aggregateId(document.getEmployeeId().getBytes(StandardCharsets.UTF_8))
                .aggregateType(AGGREGATE_TYPE)
                .payload(payload.getBytes(StandardCharsets.UTF_8))
                .creationDate(LocalDateTime.now())
                .build();

        outboxRepository.save(outboxMO);
        log.info("Saved outbox entry for documentId={}", document.getDocumentId());
    }

    private String buildJsonPayload(DigitalDocument document) {
        return String.format(
                "{\"employeeId\":\"%s\",\"managedGroupId\":\"%s\",\"digitalDocumentId\":\"%s\"}",
                document.getEmployeeId(),
                document.getManagedGroupId(),
                document.getDocumentId()
        );
    }
}
