package com.mercadona.employee.digitaldocument.application.exceptions;

public class DigitalDocumentFailedException extends RuntimeException {

    public DigitalDocumentFailedException(String documentId, String failedStep) {
        super("Document already in FAILED state: documentId=" + documentId + ", failedStep=" + failedStep);
    }
}
