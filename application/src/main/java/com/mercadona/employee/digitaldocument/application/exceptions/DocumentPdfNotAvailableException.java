package com.mercadona.employee.digitaldocument.application.exceptions;

public class DocumentPdfNotAvailableException extends RuntimeException {
    public DocumentPdfNotAvailableException(String documentId, String status) {
        super("PDF not available for documentId=" + documentId + ", current status=" + status);
    }
}
