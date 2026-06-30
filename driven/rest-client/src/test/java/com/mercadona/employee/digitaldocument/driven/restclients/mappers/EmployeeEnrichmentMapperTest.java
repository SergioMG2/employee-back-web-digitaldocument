package com.mercadona.employee.digitaldocument.driven.restclients.mappers;

import com.mercadona.employee.digitaldocument.driven.restclients.dto.CertificationDataDTO;
import com.mercadona.employee.digitaldocument.driven.restclients.dto.EmployeeDataDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for {@link EmployeeEnrichmentMapper}.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EmployeeEnrichmentMapperImpl.class})
class EmployeeEnrichmentMapperTest {

    @Autowired
    private EmployeeEnrichmentMapper mapper;

    @Test
    void toEmployeeInfo_validDto_shouldMapAllFields() {
        var dto = buildEmployeeDataDTO();

        var result = mapper.toEmployeeInfo(dto);

        assertThat(result.getEmployeeId()).isEqualTo("EMP001");
        assertThat(result.getManagedGroupId()).isEqualTo("GROUP001");
        assertThat(result.getFullName()).isEqualTo("John Doe");
        assertThat(result.getJobFunction()).isEqualTo("Software Engineer");
        assertThat(result.getDepartment()).isEqualTo("Engineering");
        assertThat(result.getEmail()).isEqualTo("john.doe@mercadona.com");
        assertThat(result.getPhoneExtension()).isEqualTo("1234");
        assertThat(result.getLocation()).isEqualTo("Valencia");
    }

    @Test
    void toEmployeeInfo_shouldMapNestedCertification() {
        var dto = buildEmployeeDataDTO();

        var result = mapper.toEmployeeInfo(dto);

        assertThat(result.getCertification()).isNotNull();
        assertThat(result.getCertification().getCertificationId()).isEqualTo("CERT001");
        assertThat(result.getCertification().getStatus()).isEqualTo("ACTIVE");
        assertThat(result.getCertification().getIsValid()).isTrue();
        assertThat(result.getCertification().getLevel()).isEqualTo("ADVANCED");
        assertThat(result.getCertification().getApprovedTools()).containsExactly("ChatGPT", "Copilot");
        assertThat(result.getCertification().getIssuedBy()).isEqualTo("Mercadona AI");
        assertThat(result.getCertification().getStartDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(result.getCertification().getExpirationDate()).isEqualTo(LocalDate.of(2025, 1, 1));
        assertThat(result.getCertification().getIssuedDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    void toEmployeeInfo_nullCertification_shouldMapNullCertification() {
        var dto = buildEmployeeDataDTO();
        dto.setCertification(null);

        var result = mapper.toEmployeeInfo(dto);

        assertThat(result.getCertification()).isNull();
    }

    // --- helpers ---

    private EmployeeDataDTO buildEmployeeDataDTO() {
        var dto = new EmployeeDataDTO();
        dto.setEmployeeId("EMP001");
        dto.setManagedGroupId("GROUP001");
        dto.setFullName("John Doe");
        dto.setJobFunction("Software Engineer");
        dto.setDepartment("Engineering");
        dto.setEmail("john.doe@mercadona.com");
        dto.setPhoneExtension("1234");
        dto.setLocation("Valencia");
        dto.setCertification(buildCertificationDataDTO());
        return dto;
    }

    private CertificationDataDTO buildCertificationDataDTO() {
        var dto = new CertificationDataDTO();
        dto.setCertificationId("CERT001");
        dto.setStatus("ACTIVE");
        dto.setIsValid(true);
        dto.setStartDate(LocalDate.of(2024, 1, 1));
        dto.setExpirationDate(LocalDate.of(2025, 1, 1));
        dto.setIssuedDate(LocalDate.of(2024, 1, 1));
        dto.setLevel("ADVANCED");
        dto.setApprovedTools(List.of("ChatGPT", "Copilot"));
        dto.setIssuedBy("Mercadona AI");
        dto.setDescription("AI certification for engineering team");
        return dto;
    }
}
