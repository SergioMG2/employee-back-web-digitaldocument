package com.mercadona.employee.digitaldocument.driving.controllers.adapters;

import com.mercadona.employee.digitaldocument.application.exceptions.DocumentNotFoundException;
import com.mercadona.employee.digitaldocument.application.exceptions.DocumentPdfNotAvailableException;
import com.mercadona.employee.digitaldocument.application.ports.driving.DigitalDocumentServicePort;
import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.domain.DocumentStatus;
import com.mercadona.employee.digitaldocument.driving.controllers.config.WebSecurityConfigurer;
import com.mercadona.employee.digitaldocument.driving.controllers.dto.DocumentStatusDTO;
import com.mercadona.employee.digitaldocument.driving.controllers.dto.DocumentStatusResponseDTO;
import com.mercadona.employee.digitaldocument.driving.controllers.mappers.DigitalDocumentDTOMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for {@link DigitalDocumentControllerAdapter}.
 */
@WebMvcTest(DigitalDocumentControllerAdapter.class)
@Import(WebSecurityConfigurer.class)
class DigitalDocumentControllerAdapterTest {

    private static final String EMPLOYEE_ID  = "EMP001";
    private static final String DOCUMENT_ID  = "doc-uuid-001";
    private static final byte[] PDF_BYTES    = new byte[]{1, 2, 3};

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DigitalDocumentServicePort servicePort;

    @MockBean
    private DigitalDocumentDTOMapper mapper;

    // --- GET /digital-documents/employees/{employeeId} ---

    @Test
    void getPdfByEmployeeId_existingDocument_shouldReturn200WithPdf() throws Exception {
        when(servicePort.getPdfByEmployeeId(EMPLOYEE_ID)).thenReturn(PDF_BYTES);

        mockMvc.perform(get("/digital-documents/employees/{employeeId}", EMPLOYEE_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    void getPdfByEmployeeId_documentNotFound_shouldReturn404() throws Exception {
        when(servicePort.getPdfByEmployeeId(EMPLOYEE_ID))
                .thenThrow(new DocumentNotFoundException(EMPLOYEE_ID));

        mockMvc.perform(get("/digital-documents/employees/{employeeId}", EMPLOYEE_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("DOCUMENT_NOT_FOUND"));
    }

    @Test
    void getPdfByEmployeeId_pdfNotAvailable_shouldReturn409() throws Exception {
        when(servicePort.getPdfByEmployeeId(EMPLOYEE_ID))
                .thenThrow(new DocumentPdfNotAvailableException(DOCUMENT_ID, "PENDING"));

        mockMvc.perform(get("/digital-documents/employees/{employeeId}", EMPLOYEE_ID))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("PDF_NOT_AVAILABLE"));
    }

    // --- GET /digital-documents/{documentId} ---

    @Test
    void getPdfByDocumentId_existingDocument_shouldReturn200WithPdf() throws Exception {
        when(servicePort.getPdfByDocumentId(DOCUMENT_ID)).thenReturn(PDF_BYTES);

        mockMvc.perform(get("/digital-documents/{documentId}", DOCUMENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    void getPdfByDocumentId_documentNotFound_shouldReturn404() throws Exception {
        when(servicePort.getPdfByDocumentId(DOCUMENT_ID))
                .thenThrow(new DocumentNotFoundException(DOCUMENT_ID));

        mockMvc.perform(get("/digital-documents/{documentId}", DOCUMENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("DOCUMENT_NOT_FOUND"));
    }

    // --- GET /digital-documents/{documentId}/status ---

    @Test
    void getDocumentStatus_existingDocument_shouldReturn200WithStatus() throws Exception {
        var document = buildDocument(DocumentStatus.STORED);
        var response = buildStatusResponse(document);
        when(servicePort.getDocumentStatus(DOCUMENT_ID)).thenReturn(document);
        when(mapper.toStatusResponse(document)).thenReturn(response);

        mockMvc.perform(get("/digital-documents/{documentId}/status", DOCUMENT_ID))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.data.documentId").value(DOCUMENT_ID))
                .andExpect(jsonPath("$.data.status").value("STORED"));
    }

    @Test
    void getDocumentStatus_failedDocument_shouldReturnFailedStep() throws Exception {
        var document = buildDocument(DocumentStatus.FAILED);
        document.setFailedStep("ENRICH");
        var response = buildStatusResponse(document);
        when(servicePort.getDocumentStatus(DOCUMENT_ID)).thenReturn(document);
        when(mapper.toStatusResponse(document)).thenReturn(response);

        mockMvc.perform(get("/digital-documents/{documentId}/status", DOCUMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("FAILED"))
                .andExpect(jsonPath("$.data.failedStep").value("ENRICH"));
    }

    @Test
    void getDocumentStatus_documentNotFound_shouldReturn404() throws Exception {
        when(servicePort.getDocumentStatus(DOCUMENT_ID))
                .thenThrow(new DocumentNotFoundException(DOCUMENT_ID));

        mockMvc.perform(get("/digital-documents/{documentId}/status", DOCUMENT_ID))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value("DOCUMENT_NOT_FOUND"));
    }

    // --- helpers ---

    private DigitalDocument buildDocument(DocumentStatus status) {
        return DigitalDocument.builder()
                .id(1L)
                .documentId(DOCUMENT_ID)
                .employeeId(EMPLOYEE_ID)
                .managedGroupId("GROUP001")
                .status(status)
                .bucketPath("documents/EMP001/doc-uuid-001.pdf")
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    private DocumentStatusResponseDTO buildStatusResponse(DigitalDocument document) {
        return DocumentStatusResponseDTO.builder()
                .data(DocumentStatusDTO.builder()
                        .documentId(document.getDocumentId())
                        .employeeId(document.getEmployeeId())
                        .status(document.getStatus().name())
                        .failedStep(document.getFailedStep())
                        .build())
                .build();
    }
}
