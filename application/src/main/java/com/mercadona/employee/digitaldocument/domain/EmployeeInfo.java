package com.mercadona.employee.digitaldocument.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeInfo {

    private String employeeId;
    private String managedGroupId;
    private String fullName;
    private String jobFunction;
    private String department;
    private String email;
    private String phoneExtension;
    private String location;
    private CertificationInfo certification;
}
