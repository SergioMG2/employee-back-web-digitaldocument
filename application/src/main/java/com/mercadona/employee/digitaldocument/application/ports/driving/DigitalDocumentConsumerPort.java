package com.mercadona.employee.digitaldocument.application.ports.driving;

public interface DigitalDocumentConsumerPort {
    void process(String employeeId, String managedGroupId);
}
