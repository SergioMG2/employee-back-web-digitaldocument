package com.mercadona.employee.digitaldocument.application.ports.driven;

import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.domain.DocumentStatus;
import com.mercadona.framework.cna.commons.domain.MercadonaPage;

import java.util.Optional;


public interface DigitalDocumentRepositoryPort {
    DigitalDocument save(DigitalDocument digitalDocument);
    Optional<DigitalDocument> findByDocumentId(String documentId);
    Optional<DigitalDocument> findByEmployeeId(String employeeId);
    Optional<DigitalDocument> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId);
    MercadonaPage<DigitalDocument> findByStatus(DocumentStatus status, Integer pageNumber, Integer pageSize);
}
