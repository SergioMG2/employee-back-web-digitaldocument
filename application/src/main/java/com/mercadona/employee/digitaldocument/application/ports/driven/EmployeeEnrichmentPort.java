package com.mercadona.employee.digitaldocument.application.ports.driven;

import com.mercadona.employee.digitaldocument.domain.EmployeeInfo;

public interface EmployeeEnrichmentPort {
    EmployeeInfo enrich(String employeeId, String managedGroupId);
}
