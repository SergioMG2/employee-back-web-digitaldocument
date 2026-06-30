package com.mercadona.employee.digitaldocument.driven.restclients.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;


@Data
@NoArgsConstructor
public class CertificationDataDTO {

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
