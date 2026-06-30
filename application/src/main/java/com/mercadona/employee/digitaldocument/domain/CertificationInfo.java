package com.mercadona.employee.digitaldocument.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificationInfo {
    private String certificationId;
    private String status;
    private Boolean isValid;
    private LocalDate startDate;
    private LocalDate expirationDate;
    private LocalDate issuedDate;
    private String level;
    private List<String> approvedTools;
    private String issuedBy;
    private String description;
}
