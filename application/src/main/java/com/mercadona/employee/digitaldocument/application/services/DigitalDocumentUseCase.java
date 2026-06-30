package com.mercadona.employee.digitaldocument.application.services;

import com.mercadona.employee.digitaldocument.application.ports.driven.DigitalDocumentRepositoryPort;
import com.mercadona.employee.digitaldocument.application.ports.driving.DigitalDocumentConsumerPort;
import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.domain.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;


@Slf4j
@Service
@AllArgsConstructor
public class DigitalDocumentUseCase implements DigitalDocumentConsumerPort {

    private final DigitalDocumentRepositoryPort digitalDocumentRepositoryPort;

    @Override
    public void process(String employeeId, String managedGroupId) {
        var existing = digitalDocumentRepositoryPort.findByEmployeeIdAndManagedGroupId(employeeId, managedGroupId);

        if (existing.isPresent()) {
            log.info("Document already exists for employeeId={}, managedGroupId={}, documentId={}. Ignoring event.",
                    employeeId, managedGroupId, existing.get().getDocumentId());
            return;
        }

        var now = OffsetDateTime.now();
        var document = DigitalDocument.builder()
                .documentId(UUID.randomUUID().toString())
                .employeeId(employeeId)
                .managedGroupId(managedGroupId)
                .status(DocumentStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();

        var saved = digitalDocumentRepositoryPort.save(document);

        log.info("Created digital document in PENDING state: documentId={}, employeeId={}, managedGroupId={}",
                saved.getDocumentId(), employeeId, managedGroupId);
    }
}
