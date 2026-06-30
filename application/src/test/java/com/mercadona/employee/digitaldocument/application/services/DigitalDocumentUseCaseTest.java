package com.mercadona.employee.digitaldocument.application.services;

import com.mercadona.employee.digitaldocument.application.ports.driven.DigitalDocumentRepositoryPort;
import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.domain.DocumentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link DigitalDocumentUseCase}.
 */
@ExtendWith(MockitoExtension.class)
class DigitalDocumentUseCaseTest {

    private static final String EMPLOYEE_ID      = "EMP001";
    private static final String MANAGED_GROUP_ID = "GROUP001";

    @Mock
    private DigitalDocumentRepositoryPort repositoryPort;

    private DigitalDocumentUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DigitalDocumentUseCase(repositoryPort);
    }

    @Test
    void process_noExistingDocument_shouldCreateDocumentInPendingState() {
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID);

        var captor = ArgumentCaptor.forClass(DigitalDocument.class);
        verify(repositoryPort).save(captor.capture());

        var saved = captor.getValue();
        assertThat(saved.getEmployeeId()).isEqualTo(EMPLOYEE_ID);
        assertThat(saved.getManagedGroupId()).isEqualTo(MANAGED_GROUP_ID);
        assertThat(saved.getStatus()).isEqualTo(DocumentStatus.PENDING);
        assertThat(saved.getDocumentId()).isNotBlank();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
    }

    @Test
    void process_noExistingDocument_shouldGenerateUniqueDocumentId() {
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(any(), any())).thenReturn(Optional.empty());
        when(repositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var captor = ArgumentCaptor.forClass(DigitalDocument.class);

        useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID);
        useCase.process("EMP002", MANAGED_GROUP_ID);

        verify(repositoryPort, times(2)).save(captor.capture());
        var documentIds = captor.getAllValues().stream().map(DigitalDocument::getDocumentId).toList();
        assertThat(documentIds).hasSize(2).doesNotHaveDuplicates();
        assertThat(documentIds).allMatch(id -> !id.isBlank());
    }

    @Test
    void process_documentAlreadyExists_shouldNotSave() {
        var existing = buildExistingDocument();
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.of(existing));

        useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID);

        verify(repositoryPort, never()).save(any());
    }

    @Test
    void process_documentAlreadyExists_shouldNotThrow() {
        when(repositoryPort.findByEmployeeIdAndManagedGroupId(EMPLOYEE_ID, MANAGED_GROUP_ID))
                .thenReturn(Optional.of(buildExistingDocument()));

        assertThat(catchThrowable(() -> useCase.process(EMPLOYEE_ID, MANAGED_GROUP_ID))).isNull();
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

    private Throwable catchThrowable(Runnable runnable) {
        try {
            runnable.run();
            return null;
        } catch (Throwable t) {
            return t;
        }
    }
}
