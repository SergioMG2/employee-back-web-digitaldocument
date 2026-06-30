//package com.mercadona.employee.digitaldocument.driven.repositories.mappers;
//
//import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
//import com.mercadona.employee.digitaldocument.domain.DocumentStatus;
//import com.mercadona.employee.digitaldocument.driven.repositories.models.DigitalDocumentMO;
//import com.mercadona.employee.digitaldocument.driven.repositories.models.DocumentStatusMOEnum;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.time.OffsetDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
///**
// * Unit tests for {@link DigitalDocumentMapper}.
// */
//@ExtendWith(SpringExtension.class)
//@ContextConfiguration(classes = {DigitalDocumentMapperImpl.class})
//class DigitalDocumentMapperTest {
//
//    @Autowired
//    private DigitalDocumentMapper mapper;
//
//    @Test
//    void fromModel_validMO_shouldMapAllFields() {
//        var mo = buildMO();
//
//        var result = mapper.fromModel(mo);
//
//        assertThat(result.getId()).isEqualTo(mo.getId());
//        assertThat(result.getDocumentId()).isEqualTo(mo.getDocumentId());
//        assertThat(result.getEmployeeId()).isEqualTo(mo.getEmployeeId());
//        assertThat(result.getManagedGroupId()).isEqualTo(mo.getManagedGroupId());
//        assertThat(result.getStatus()).isEqualTo(DocumentStatus.STORED);
//        assertThat(result.getFailedStep()).isEqualTo(mo.getFailedStep());
//        assertThat(result.getBucketPath()).isEqualTo(mo.getBucketPath());
//        assertThat(result.getCreatedAt()).isEqualTo(mo.getCreatedAt());
//        assertThat(result.getUpdatedAt()).isEqualTo(mo.getUpdatedAt());
//    }
//
//    @Test
//    void toModel_validDomain_shouldMapAllFields() {
//        var domain = buildDomain();
//
//        var result = mapper.toModel(domain);
//
//        assertThat(result.getId()).isEqualTo(domain.getId());
//        assertThat(result.getDocumentId()).isEqualTo(domain.getDocumentId());
//        assertThat(result.getEmployeeId()).isEqualTo(domain.getEmployeeId());
//        assertThat(result.getManagedGroupId()).isEqualTo(domain.getManagedGroupId());
//        assertThat(result.getStatus()).isEqualTo(DocumentStatusMOEnum.STORED);
//        assertThat(result.getFailedStep()).isEqualTo(domain.getFailedStep());
//        assertThat(result.getBucketPath()).isEqualTo(domain.getBucketPath());
//        assertThat(result.getCreatedAt()).isEqualTo(domain.getCreatedAt());
//        assertThat(result.getUpdatedAt()).isEqualTo(domain.getUpdatedAt());
//    }
//
//    @Test
//    void fromModel_failedStatus_shouldMapFailedStepCorrectly() {
//        var mo = DigitalDocumentMO.builder()
//                .id(1L)
//                .documentId("doc-uuid-001")
//                .employeeId("EMP001")
//                .managedGroupId("GROUP001")
//                .status(DocumentStatusMOEnum.FAILED)
//                .failedStep("ENRICH")
//                .bucketPath(null)
//                .createdAt(OffsetDateTime.parse("2024-01-01T10:00:00Z"))
//                .updatedAt(OffsetDateTime.parse("2024-01-01T10:05:00Z"))
//                .build();
//
//        var result = mapper.fromModel(mo);
//
//        assertThat(result.getStatus()).isEqualTo(DocumentStatus.FAILED);
//        assertThat(result.getFailedStep()).isEqualTo("ENRICH");
//    }
//
//    @Test
//    void fromOptionalModel_present_shouldReturnMappedDomain() {
//        var result = mapper.fromOptionalModel(Optional.of(buildMO()));
//
//        assertThat(result).isPresent();
//        assertThat(result.get().getDocumentId()).isEqualTo("doc-uuid-001");
//    }
//
//    @Test
//    void fromOptionalModel_empty_shouldReturnEmpty() {
//        var result = mapper.fromOptionalModel(Optional.empty());
//
//        assertThat(result).isEmpty();
//    }
//
//    @Test
//    void fromModels_validList_shouldMapAllElements() {
//        var result = mapper.fromModels(List.of(buildMO(), buildMO()));
//
//        assertThat(result).hasSize(2);
//        assertThat(result).allMatch(d -> d.getDocumentId().equals("doc-uuid-001"));
//    }
//
//    @Test
//    void fromModels_emptyList_shouldReturnEmptyList() {
//        var result = mapper.fromModels(List.of());
//
//        assertThat(result).isEmpty();
//    }
//
//    // --- helpers ---
//
//    private DigitalDocumentMO buildMO() {
//        return DigitalDocumentMO.builder()
//                .id(1L)
//                .documentId("doc-uuid-001")
//                .employeeId("EMP001")
//                .managedGroupId("GROUP001")
//                .status(DocumentStatusMOEnum.STORED)
//                .failedStep(null)
//                .bucketPath("documents/EMP001/doc-uuid-001.pdf")
//                .createdAt(OffsetDateTime.parse("2024-01-01T10:00:00Z"))
//                .updatedAt(OffsetDateTime.parse("2024-01-01T10:05:00Z"))
//                .build();
//    }
//
//    private DigitalDocument buildDomain() {
//        return DigitalDocument.builder()
//                .id(1L)
//                .documentId("doc-uuid-001")
//                .employeeId("EMP001")
//                .managedGroupId("GROUP001")
//                .status(DocumentStatus.STORED)
//                .failedStep(null)
//                .bucketPath("documents/EMP001/doc-uuid-001.pdf")
//                .createdAt(OffsetDateTime.parse("2024-01-01T10:00:00Z"))
//                .updatedAt(OffsetDateTime.parse("2024-01-01T10:05:00Z"))
//                .build();
//    }
//}
