package com.mercadona.employee.digitaldocument.application.ports.driven;

public interface BucketStoragePort {
    String upload(String documentId, String employeeId, byte[] pdfBytes);
}
