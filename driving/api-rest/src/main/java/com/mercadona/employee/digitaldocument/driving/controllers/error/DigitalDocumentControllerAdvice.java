package com.mercadona.employee.digitaldocument.driving.controllers.error;

import com.mercadona.employee.digitaldocument.application.exceptions.DocumentNotFoundException;
import com.mercadona.employee.digitaldocument.application.exceptions.DocumentPdfNotAvailableException;
import com.mercadona.employee.digitaldocument.driving.controllers.dto.ErrorDTO;
import com.mercadona.employee.digitaldocument.driving.controllers.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class DigitalDocumentControllerAdvice {

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleDocumentNotFound(DocumentNotFoundException ex) {
        log.warn("Document not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError("DOCUMENT_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(DocumentPdfNotAvailableException.class)
    public ResponseEntity<ErrorResponseDTO> handlePdfNotAvailable(DocumentPdfNotAvailableException ex) {
        log.warn("PDF not available: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError("PDF_NOT_AVAILABLE", ex.getMessage()));
    }

    private ErrorResponseDTO buildError(String code, String description) {
        return ErrorResponseDTO.builder()
                .error(ErrorDTO.builder()
                        .code(code)
                        .description(description)
                        .build())
                .build();
    }
}
