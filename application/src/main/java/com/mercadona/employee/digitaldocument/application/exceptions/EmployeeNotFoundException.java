package com.mercadona.employee.digitaldocument.application.exceptions;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(String employeeId, String managedGroupId) {
        super("Employee not found: employeeId=" + employeeId + ", managedGroupId=" + managedGroupId);
    }
}
