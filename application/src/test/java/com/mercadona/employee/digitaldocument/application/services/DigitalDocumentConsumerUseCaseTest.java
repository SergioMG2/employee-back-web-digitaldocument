package com.mercadona.employee.digitaldocument.application.services;

import com.mercadona.employee.digitaldocument.application.ports.driven.BucketStoragePort;
import com.mercadona.employee.digitaldocument.application.ports.driven.DigitalDocumentRepositoryPort;
import com.mercadona.employee.digitaldocument.application.ports.driven.EmployeeEnrichmentPort;
import com.mercadona.employee.digitaldocument.application.ports.driven.OutboxRepositoryPort;
import com.mercadona.employee.digitaldocument.application.ports.driven.PdfGeneratorPort;
import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.domain.DocumentStatus;
import com.mercadona.employee.digitaldocument.domain.EmployeeInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DigitalDocumentConsumerUseCase}.
 */
@ExtendWith(MockitoExtension.class)
class DigitalDocumentConsumerUseCaseTest {

    private static final String EMPLOYEE_ID      = "EMP001";
    private static final String MANAGED_GROUP_ID = "GROUP001";

    @Mock
    private DigitalDocumentRepositoryPort repositoryPort;

    @Mock
    private EmployeeEnrichmentPort enrichmentPort;

    @Mock
    private PdfGeneratorPort pdfGeneratorPort;

    @Mock
    private BucketStoragePort bucketStoragePort;

    @Mock
    private OutboxRepositoryPort outboxPublisherPort;

    private DigitalDocumentConsumerUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DigitalDocumentConsumerUseCase(repositoryPort, enrichmentPort, pdfGeneratorPort, bucketStoragePort, outboxPublisherPort);
    }

    @Test
    void process_newDocument_shouldPersistInPendingFirst() {
        List<DocumentStatus> capturedStatuses = new ArrayList<>();
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> {
            DigitalDocument doc = invocation.getArgument(0);
            capturedStatuses.add(doc.getStatus());
            return doc;
        });
        when(enrichmentPort.enrich(any(), any())).thenReturn(buildEmployeeInfo());
        when(pdfGeneratorPort.generate(any())).thenReturn(new byte[]{1, 2, 3});
        when(bucketStoragePort.upload(any(), any(), any())).thenReturn("documents/EMP001/doc.pdf");

        useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID);

        assertThat(capturedStatuses.get(0)).isEqualTo(DocumentStatus.PENDING);
    }

    @Test
    void process_newDocument_shouldUpdateToEnrichedAfterEnrichment() {
        List<DocumentStatus> capturedStatuses = new ArrayList<>();
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> {
            DigitalDocument doc = invocation.getArgument(0);
            capturedStatuses.add(doc.getStatus());
            return doc;
        });
        when(enrichmentPort.enrich(any(), any())).thenReturn(buildEmployeeInfo());
        when(pdfGeneratorPort.generate(any())).thenReturn(new byte[]{1, 2, 3});
        when(bucketStoragePort.upload(any(), any(), any())).thenReturn("documents/EMP001/doc.pdf");

        useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID);

        assertThat(capturedStatuses.get(1)).isEqualTo(DocumentStatus.ENRICHED);
    }

    @Test
    void process_newDocument_shouldCallEnrichmentWithCorrectArgs() {
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(enrichmentPort.enrich(any(), any())).thenReturn(buildEmployeeInfo());
        when(pdfGeneratorPort.generate(any())).thenReturn(new byte[]{1, 2, 3});
        when(bucketStoragePort.upload(any(), any(), any())).thenReturn("documents/EMP001/doc.pdf");

        useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID);

        verify(enrichmentPort).enrich(EMPLOYEE_ID, MANAGED_GROUP_ID);
    }

    @Test
    void process_enrichmentFails_shouldMarkDocumentAsFailedWithFailedStep() {
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(enrichmentPort.enrich(any(), any())).thenThrow(new RuntimeException("API error"));

        assertThatThrownBy(() -> useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .isInstanceOf(RuntimeException.class);

        var captor = ArgumentCaptor.forClass(DigitalDocument.class);
        verify(repositoryPort, times(2)).save(captor.capture());

        var failedSave = captor.getAllValues().get(1);
        assertThat(failedSave.getStatus()).isEqualTo(DocumentStatus.FAILED);
        assertThat(failedSave.getFailedStep()).isEqualTo("ENRICH");
    }

    @Test
    void process_documentAlreadyExists_shouldNotSaveOrEnrich() {
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.of(buildExistingDocument()));

        useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID);

        verify(repositoryPort, never()).save(any());
        verify(enrichmentPort, never()).enrich(any(), any());
    }

    @Test
    void process_newDocument_shouldGenerateUniqueDocumentIds() {
        List<String> capturedPendingIds = new ArrayList<>();
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(any(), any())).thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> {
            DigitalDocument doc = invocation.getArgument(0);
            if (doc.getStatus() == DocumentStatus.PENDING) {
                capturedPendingIds.add(doc.getDocumentId());
            }
            return doc;
        });
        when(enrichmentPort.enrich(any(), any())).thenReturn(buildEmployeeInfo());
        when(pdfGeneratorPort.generate(any())).thenReturn(new byte[]{1, 2, 3});
        when(bucketStoragePort.upload(any(), any(), any())).thenReturn("documents/EMP001/doc.pdf");

        useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID);
        useCase.process("EMP002", MANAGED_GROUP_ID);

        assertThat(capturedPendingIds).hasSize(2).doesNotHaveDuplicates();
    }

    @Test
    void process_newDocument_shouldUpdateToPdfGeneratedAfterGeneration() {
        List<DocumentStatus> capturedStatuses = new ArrayList<>();
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> {
            DigitalDocument doc = invocation.getArgument(0);
            capturedStatuses.add(doc.getStatus());
            return doc;
        });
        when(enrichmentPort.enrich(any(), any())).thenReturn(buildEmployeeInfo());
        when(pdfGeneratorPort.generate(any())).thenReturn(new byte[]{1, 2, 3});
        when(bucketStoragePort.upload(any(), any(), any())).thenReturn("documents/EMP001/doc.pdf");

        useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID);

        assertThat(capturedStatuses).containsExactly(
                DocumentStatus.PENDING,
                DocumentStatus.ENRICHED,
                DocumentStatus.PDF_GENERATED,
                DocumentStatus.STORED
        );
    }

    @Test
    void process_newDocument_shouldUpdateToStoredWithBucketPath() {
        List<DocumentStatus> capturedStatuses = new ArrayList<>();
        List<String> capturedBucketPaths = new ArrayList<>();
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> {
            DigitalDocument doc = invocation.getArgument(0);
            capturedStatuses.add(doc.getStatus());
            if (doc.getBucketPath() != null) capturedBucketPaths.add(doc.getBucketPath());
            return doc;
        });
        when(enrichmentPort.enrich(any(), any())).thenReturn(buildEmployeeInfo());
        when(pdfGeneratorPort.generate(any())).thenReturn(new byte[]{1, 2, 3});
        when(bucketStoragePort.upload(any(), any(), any())).thenReturn("documents/EMP001/doc.pdf");

        useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID);

        assertThat(capturedStatuses).containsExactly(
                DocumentStatus.PENDING, DocumentStatus.ENRICHED,
                DocumentStatus.PDF_GENERATED, DocumentStatus.STORED);
        assertThat(capturedBucketPaths).containsExactly("documents/EMP001/doc.pdf");
    }

    @Test
    void process_bucketUploadFails_shouldMarkDocumentAsFailedWithFailedStep() {
        List<DocumentStatus> capturedStatuses = new ArrayList<>();
        List<String> capturedFailedSteps = new ArrayList<>();
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> {
            DigitalDocument doc = invocation.getArgument(0);
            capturedStatuses.add(doc.getStatus());
            if (doc.getFailedStep() != null) capturedFailedSteps.add(doc.getFailedStep());
            return doc;
        });
        when(enrichmentPort.enrich(any(), any())).thenReturn(buildEmployeeInfo());
        when(pdfGeneratorPort.generate(any())).thenReturn(new byte[]{1, 2, 3});
        when(bucketStoragePort.upload(any(), any(), any())).thenThrow(new RuntimeException("bucket error"));

        assertThatThrownBy(() -> useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .isInstanceOf(RuntimeException.class);

        assertThat(capturedStatuses).contains(DocumentStatus.FAILED);
        assertThat(capturedFailedSteps).containsExactly("STORAGE");
    }

    @Test
    void process_pdfGenerationFails_shouldMarkDocumentAsFailedWithFailedStep() {
        List<DocumentStatus> capturedStatuses = new ArrayList<>();
        List<String> capturedFailedSteps = new ArrayList<>();
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> {
            DigitalDocument doc = invocation.getArgument(0);
            capturedStatuses.add(doc.getStatus());
            if (doc.getFailedStep() != null) capturedFailedSteps.add(doc.getFailedStep());
            return doc;
        });
        when(enrichmentPort.enrich(any(), any())).thenReturn(buildEmployeeInfo());
        when(pdfGeneratorPort.generate(any())).thenThrow(new RuntimeException("PDF error"));

        assertThatThrownBy(() -> useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .isInstanceOf(RuntimeException.class);

        assertThat(capturedStatuses).contains(DocumentStatus.FAILED);
        assertThat(capturedFailedSteps).containsExactly("PDF_GENERATION");
    }

    // --- helpers ---

    private DigitalDocument buildExistingDocument() {
        return DigitalDocument.builder()
                .id(1L)
                .documentId("existing-uuid")
                .employeeId(EMPLOYEE_ID)
                .managedGroupId(MANAGED_GROUP_ID)
                .status(DocumentStatus.PENDING)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    private EmployeeInfo buildEmployeeInfo() {
        return EmployeeInfo.builder()
                .employeeId(EMPLOYEE_ID)
                .managedGroupId(MANAGED_GROUP_ID)
                .fullName("John Doe")
                .jobFunction("Engineer")
                .department("IT")
                .email("john.doe@mercadona.com")
                .build();
    }
}
