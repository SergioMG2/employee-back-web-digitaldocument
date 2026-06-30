package com.mercadona.employee.digitaldocument.application.exceptions;

public class DocumentNotFoundException extends RuntimeException {
    public DocumentNotFoundException(String identifier) {
        super("Document not found for identifier=" + identifier);
    }
}
