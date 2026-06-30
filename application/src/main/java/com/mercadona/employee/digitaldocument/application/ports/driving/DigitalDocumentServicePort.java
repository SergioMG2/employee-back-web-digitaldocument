package com.mercadona.employee.digitaldocument.application.ports.driving;

import com.mercadona.employee.digitaldocument.domain.DigitalDocument;

public interface DigitalDocumentServicePort {
    byte[] getPdfByEmployeeId(String employeeId);
    byte[] getPdfByDocumentId(String documentId);
    DigitalDocument getDocumentStatus(String documentId);
}
