package com.mercadona.employee.digitaldocument.driving.controllers.adapters;

import com.mercadona.employee.digitaldocument.application.ports.driving.DigitalDocumentServicePort;
import com.mercadona.employee.digitaldocument.driving.controllers.dto.DocumentStatusResponseDTO;
import com.mercadona.employee.digitaldocument.driving.controllers.mappers.DigitalDocumentDTOMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller adapter for the digital document API.
 *
 * <p>Exposes endpoints for retrieving PDF documents and querying document status.
 * Delegates business logic to {@link DigitalDocumentServicePort}.
 */
@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/digital-documents")
public class DigitalDocumentControllerAdapter {

    private final DigitalDocumentServicePort servicePort;
    private final DigitalDocumentDTOMapper mapper;

    @GetMapping(value = "/employees/{employeeId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPdfByEmployeeId(@PathVariable String employeeId) {
        var pdfBytes = servicePort.getPdfByEmployeeId(employeeId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + employeeId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(value = "/{documentId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPdfByDocumentId(@PathVariable String documentId) {
        var pdfBytes = servicePort.getPdfByDocumentId(documentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + documentId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(value = "/{documentId}/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DocumentStatusResponseDTO> getDocumentStatus(@PathVariable String documentId) {
        var document = servicePort.getDocumentStatus(documentId);
        return ResponseEntity.ok(mapper.toStatusResponse(document));
    }
}
