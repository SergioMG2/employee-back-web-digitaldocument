package com.mercadona.employee.digitaldocument.application.services;

import com.mercadona.employee.digitaldocument.application.exceptions.DocumentNotFoundException;
import com.mercadona.employee.digitaldocument.application.exceptions.DocumentPdfNotAvailableException;
import com.mercadona.employee.digitaldocument.application.ports.driven.BucketStoragePort;
import com.mercadona.employee.digitaldocument.application.ports.driven.DigitalDocumentRepositoryPort;
import com.mercadona.employee.digitaldocument.application.ports.driving.DigitalDocumentServicePort;
import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.domain.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Use case that handles the REST API queries for digital documents.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DigitalDocumentUseCase implements DigitalDocumentServicePort {

    private final DigitalDocumentRepositoryPort repositoryPort;
    private final BucketStoragePort bucketStoragePort;

    @Override
    public byte[] getPdfByEmployeeId(String employeeId) {
        log.info("Fetching PDF for employeeId={}", employeeId);
        var document = repositoryPort.findByEmployeeId(employeeId)
                .orElseThrow(() -> new DocumentNotFoundException(employeeId));
        return downloadPdf(document);
    }

    @Override
    public byte[] getPdfByDocumentId(String documentId) {
        log.info("Fetching PDF for documentId={}", documentId);
        var document = repositoryPort.findByDocumentId(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
        return downloadPdf(document);
    }

    @Override
    public DigitalDocument getDocumentStatus(String documentId) {
        log.info("Fetching status for documentId={}", documentId);
        return repositoryPort.findByDocumentId(documentId)
                .orElseThrow(() -> new DocumentNotFoundException(documentId));
    }

    private byte[] downloadPdf(DigitalDocument document) {
        if (!DocumentStatus.STORED.equals(document.getStatus())) {
            throw new DocumentPdfNotAvailableException(document.getDocumentId(), document.getStatus().name());
        }
        return bucketStoragePort.download(document.getBucketPath());
    }
}
