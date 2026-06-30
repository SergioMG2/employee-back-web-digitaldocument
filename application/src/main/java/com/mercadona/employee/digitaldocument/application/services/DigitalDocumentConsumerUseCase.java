package com.mercadona.employee.digitaldocument.application.services;

import com.mercadona.employee.digitaldocument.application.exceptions.DigitalDocumentFailedException;
import com.mercadona.employee.digitaldocument.application.ports.driven.BucketStoragePort;
import com.mercadona.employee.digitaldocument.application.ports.driven.DigitalDocumentRepositoryPort;
import com.mercadona.employee.digitaldocument.application.ports.driven.EmployeeEnrichmentPort;
import com.mercadona.employee.digitaldocument.application.ports.driven.OutboxRepositoryPort;
import com.mercadona.employee.digitaldocument.application.ports.driven.PdfGeneratorPort;
import com.mercadona.employee.digitaldocument.application.ports.driving.DigitalDocumentConsumerPort;
import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.domain.DocumentStatus;
import com.mercadona.employee.digitaldocument.domain.EmployeeInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class DigitalDocumentConsumerUseCase implements DigitalDocumentConsumerPort {

    private final DigitalDocumentRepositoryPort digitalDocumentRepositoryPort;
    private final EmployeeEnrichmentPort employeeEnrichmentPort;
    private final PdfGeneratorPort pdfGeneratorPort;
    private final BucketStoragePort bucketStoragePort;
    private final OutboxRepositoryPort outboxPublisherPort;

    @Override
    public void process(String employeeId, String managedGroupId) {
        var existing = digitalDocumentRepositoryPort.findByEmployeeIdAndManagedGroupId(employeeId, managedGroupId);

        if (existing.isPresent()) {
            var document = existing.get();
            if (DocumentStatus.FAILED.equals(document.getStatus())) {
                log.warn("Document in FAILED state for employeeId={}, managedGroupId={}, documentId={}, failedStep={}. Discarding Kafka event — batch reprocessor will handle it.",
                        employeeId, managedGroupId, document.getDocumentId(), document.getFailedStep());
                throw new DigitalDocumentFailedException(document.getDocumentId(), document.getFailedStep());
            }
            log.info("Document already exists for employeeId={}, managedGroupId={}, documentId={}. Ignoring event.",
                    employeeId, managedGroupId, document.getDocumentId());
            return;
        }

        var document = createPendingDocument(employeeId, managedGroupId);
        var savedDocument = digitalDocumentRepositoryPort.save(document);
        log.info("Created digital document in PENDING state: documentId={}", savedDocument.getDocumentId());

        var employeeInfo = enrich(savedDocument);
        var pdfBytes = generatePdf(savedDocument, employeeInfo);
        uploadAndPublish(savedDocument, pdfBytes);
    }

    private void uploadAndPublish(DigitalDocument document, byte[] pdfBytes) {
        try {
            var bucketPath = bucketStoragePort.upload(document.getDocumentId(), document.getEmployeeId(), pdfBytes);
            document.setBucketPath(bucketPath);
            updateStatus(document, DocumentStatus.STORED);
            log.info("PDF uploaded to bucket: documentId={}, path={}", document.getDocumentId(), bucketPath);
        } catch (Exception e) {
            log.error("Bucket upload failed for documentId={}: {}", document.getDocumentId(), e.getMessage());
            markAsFailed(document, "STORAGE");
            throw e;
        }

        try {
            outboxPublisherPort.publish(document);
            log.info("Event written to outbox: documentId={}", document.getDocumentId());
        } catch (Exception e) {
            log.error("Outbox write failed for documentId={}: {}. Document remains STORED.", document.getDocumentId(), e.getMessage());
            // TODO: batch reprocessor should retry the outbox write for STORED documents without outbox entry
        }
    }

    private byte[] generatePdf(DigitalDocument document, EmployeeInfo employeeInfo) {
        try {
            var pdfBytes = pdfGeneratorPort.generate(employeeInfo);
            updateStatus(document, DocumentStatus.PDF_GENERATED);
            log.info("PDF generated for documentId={}", document.getDocumentId());
            return pdfBytes;
        } catch (Exception e) {
            log.error("PDF generation failed for documentId={}: {}", document.getDocumentId(), e.getMessage());
            markAsFailed(document, "PDF_GENERATION");
            throw e;
        }
    }

    private EmployeeInfo enrich(DigitalDocument document) {
        try {
            var employeeInfo = employeeEnrichmentPort.enrich(document.getEmployeeId(), document.getManagedGroupId());
            updateStatus(document, DocumentStatus.ENRICHED);
            log.info("Document enriched: documentId={}", document.getDocumentId());
            return employeeInfo;
        } catch (Exception e) {
            log.error("Enrichment failed for documentId={}: {}", document.getDocumentId(), e.getMessage());
            markAsFailed(document, "ENRICH");
            throw e;
        }
    }

    private DigitalDocument createPendingDocument(String employeeId, String managedGroupId) {
        var now = OffsetDateTime.now();
        return DigitalDocument.builder()
                .documentId(UUID.randomUUID().toString())
                .employeeId(employeeId)
                .managedGroupId(managedGroupId)
                .status(DocumentStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    private void updateStatus(DigitalDocument document, DocumentStatus status) {
        document.setStatus(status);
        document.setUpdatedAt(OffsetDateTime.now());
        digitalDocumentRepositoryPort.save(document);
    }

    private void markAsFailed(DigitalDocument document, String failedStep) {
        document.setStatus(DocumentStatus.FAILED);
        document.setFailedStep(failedStep);
        document.setUpdatedAt(OffsetDateTime.now());
        digitalDocumentRepositoryPort.save(document);
    }
}
