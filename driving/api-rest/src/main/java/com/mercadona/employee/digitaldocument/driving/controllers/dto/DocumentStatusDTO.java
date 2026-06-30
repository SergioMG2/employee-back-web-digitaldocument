package com.mercadona.employee.digitaldocument.driving.controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatusDTO {

    private String documentId;
    private String employeeId;
    private String status;
    private String failedStep;
}
