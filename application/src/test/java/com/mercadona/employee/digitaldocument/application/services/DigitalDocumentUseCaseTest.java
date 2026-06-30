package com.mercadona.employee.digitaldocument.application.services;

import com.mercadona.employee.digitaldocument.application.ports.driven.BucketStoragePort;
import com.mercadona.employee.digitaldocument.application.ports.driven.DigitalDocumentRepositoryPort;
import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.domain.DocumentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DigitalDocumentUseCase}.
 */
@ExtendWith(MockitoExtension.class)
class DigitalDocumentUseCaseTest {

    private static final String EMPLOYEE_ID  = "EMP001";
    private static final String DOCUMENT_ID  = "doc-uuid-001";
    private static final String BUCKET_PATH  = "documents/EMP001/doc-uuid-001.pdf";
    private static final byte[] PDF_BYTES    = new byte[]{1, 2, 3};

    @Mock
    private DigitalDocumentRepositoryPort repositoryPort;

    @Mock
    private BucketStoragePort bucketStoragePort;

    private DigitalDocumentUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DigitalDocumentUseCase(repositoryPort, bucketStoragePort);
    }

    // --- getPdfByEmployeeId ---

    @Test
    void getPdfByEmployeeId_storedDocument_shouldReturnPdfBytes() {
        when(repositoryPort.findByEmployeeId(EMPLOYEE_ID)).thenReturn(Optional.of(buildStoredDocument()));
        when(bucketStoragePort.download(BUCKET_PATH)).thenReturn(PDF_BYTES);

        var result = useCase.getPdfByEmployeeId(EMPLOYEE_ID);

        assertThat(result).isEqualTo(PDF_BYTES);
        verify(bucketStoragePort).download(BUCKET_PATH);
    }

    @Test
    void getPdfByEmployeeId_documentNotFound_shouldThrow() {
        when(repositoryPort.findByEmployeeId(EMPLOYEE_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getPdfByEmployeeId(EMPLOYEE_ID))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getPdfByEmployeeId_documentNotStored_shouldThrow() {
        var pendingDoc = buildDocument(DocumentStatus.PENDING, null);
        when(repositoryPort.findByEmployeeId(EMPLOYEE_ID)).thenReturn(Optional.of(pendingDoc));

        assertThatThrownBy(() -> useCase.getPdfByEmployeeId(EMPLOYEE_ID))
                .isInstanceOf(RuntimeException.class);
    }

    // --- getPdfByDocumentId ---

    @Test
    void getPdfByDocumentId_storedDocument_shouldReturnPdfBytes() {
        when(repositoryPort.findByDocumentId(DOCUMENT_ID)).thenReturn(Optional.of(buildStoredDocument()));
        when(bucketStoragePort.download(BUCKET_PATH)).thenReturn(PDF_BYTES);

        var result = useCase.getPdfByDocumentId(DOCUMENT_ID);

        assertThat(result).isEqualTo(PDF_BYTES);
        verify(bucketStoragePort).download(BUCKET_PATH);
    }

    @Test
    void getPdfByDocumentId_documentNotFound_shouldThrow() {
        when(repositoryPort.findByDocumentId(DOCUMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getPdfByDocumentId(DOCUMENT_ID))
                .isInstanceOf(RuntimeException.class);
    }

    // --- getDocumentStatus ---

    @Test
    void getDocumentStatus_existingDocument_shouldReturnDocument() {
        var document = buildStoredDocument();
        when(repositoryPort.findByDocumentId(DOCUMENT_ID)).thenReturn(Optional.of(document));

        var result = useCase.getDocumentStatus(DOCUMENT_ID);

        assertThat(result.getStatus()).isEqualTo(DocumentStatus.STORED);
        assertThat(result.getDocumentId()).isEqualTo(DOCUMENT_ID);
    }

    @Test
    void getDocumentStatus_failedDocument_shouldReturnFailedStepInfo() {
        var failed = buildDocument(DocumentStatus.FAILED, null);
        failed.setFailedStep("ENRICH");
        when(repositoryPort.findByDocumentId(DOCUMENT_ID)).thenReturn(Optional.of(failed));

        var result = useCase.getDocumentStatus(DOCUMENT_ID);

        assertThat(result.getStatus()).isEqualTo(DocumentStatus.FAILED);
        assertThat(result.getFailedStep()).isEqualTo("ENRICH");
    }

    @Test
    void getDocumentStatus_documentNotFound_shouldThrow() {
        when(repositoryPort.findByDocumentId(DOCUMENT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.getDocumentStatus(DOCUMENT_ID))
                .isInstanceOf(RuntimeException.class);
    }

    // --- helpers ---

    private DigitalDocument buildStoredDocument() {
        return buildDocument(DocumentStatus.STORED, BUCKET_PATH);
    }

    private DigitalDocument buildDocument(DocumentStatus status, String bucketPath) {
        return DigitalDocument.builder()
                .id(1L)
                .documentId(DOCUMENT_ID)
                .employeeId(EMPLOYEE_ID)
                .managedGroupId("GROUP001")
                .status(status)
                .bucketPath(bucketPath)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }
}
