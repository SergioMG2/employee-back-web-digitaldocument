package com.mercadona.employee.digitaldocument.driving.kafka.consumers;

import com.mercadona.employee.digitaldocument.application.ports.driving.DigitalDocumentConsumerPort;
import com.mercadona.framework.cna.commons.exception.MercadonaRuntimeException;
import com.mercadona.framework.cna.lib.kafka.exceptions.BlockingLimitedRetryableException;
import com.mercadona.framework.cna.lib.kafka.exceptions.BlockingUnlimitedRetryableException;
import com.mercadona.framework.cna.lib.kafka.exceptions.NotRetryableException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import thirdparty.employee.employee.EmployeeEventPublicKey;
import thirdparty.employee.employee.EmployeeEventPublicValue;
import thirdparty.employee.employee.ManagedGroupIds;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link EmployeeEventConsumerAdapter}.
 */
@ExtendWith(MockitoExtension.class)
class EmployeeEventConsumerAdapterTest {

    private static final String TEST_TOPIC    = "employee";
    private static final String TEST_GROUP_ID = "employee-digital-document-consumer-group";
    private static final String EMPLOYEE_ID   = "EMP001";
    private static final String GROUP_ID      = "GROUP001";

    @Mock
    private DigitalDocumentConsumerPort consumerPort;

    private EmployeeEventConsumerAdapter consumer;

    @BeforeEach
    void setUp() {
        consumer = new EmployeeEventConsumerAdapter(new String[]{TEST_TOPIC}, TEST_GROUP_ID, consumerPort);
    }

    @Test
    void consume_validRecord_shouldCallOrchestratorWithCorrectArgs() {
        var consumerRecord = buildConsumerRecord(EMPLOYEE_ID, GROUP_ID);

        consumer.consume(consumerRecord);

        verify(consumerPort).process(EMPLOYEE_ID, GROUP_ID);
    }

    @Test
    void consume_validRecord_shouldNotThrow() {
        assertThatCode(() -> consumer.consume(buildConsumerRecord(EMPLOYEE_ID, GROUP_ID)))
                .doesNotThrowAnyException();
    }

    @Test
    void consume_orchestratorThrowsBlockingLimited_shouldPropagateAsBlockingLimited() {
        doThrow(new BlockingLimitedRetryableException("transient error"))
                .when(consumerPort).process(EMPLOYEE_ID, GROUP_ID);

        assertThatThrownBy(() -> consumer.consume(buildConsumerRecord(EMPLOYEE_ID, GROUP_ID)))
                .isInstanceOf(BlockingLimitedRetryableException.class);
    }

    @Test
    void consume_orchestratorThrowsBlockingUnlimited_shouldPropagateAsBlockingUnlimited() {
        doThrow(new BlockingUnlimitedRetryableException("unlimited error"))
                .when(consumerPort).process(EMPLOYEE_ID, GROUP_ID);

        assertThatThrownBy(() -> consumer.consume(buildConsumerRecord(EMPLOYEE_ID, GROUP_ID)))
                .isInstanceOf(BlockingUnlimitedRetryableException.class);
    }

    @Test
    void consume_orchestratorThrowsNotRetryable_shouldPropagateAsNotRetryable() {
        doThrow(new NotRetryableException("permanent error"))
                .when(consumerPort).process(EMPLOYEE_ID, GROUP_ID);

        assertThatThrownBy(() -> consumer.consume(buildConsumerRecord(EMPLOYEE_ID, GROUP_ID)))
                .isInstanceOf(NotRetryableException.class);
    }

    @Test
    void consume_orchestratorThrowsUnexpectedException_shouldWrapAsMercadonaRuntimeException() {
        doThrow(new RuntimeException("unexpected"))
                .when(consumerPort).process(EMPLOYEE_ID, GROUP_ID);

        assertThatThrownBy(() -> consumer.consume(buildConsumerRecord(EMPLOYEE_ID, GROUP_ID)))
                .isInstanceOf(MercadonaRuntimeException.class);
    }

    @Test
    void consume_nullKey_shouldThrowMercadonaRuntimeException() {
        var consumerRecord = new ConsumerRecord<EmployeeEventPublicKey, EmployeeEventPublicValue>(
                TEST_TOPIC, 0, 0L, null, mock(EmployeeEventPublicValue.class));

        assertThatThrownBy(() -> consumer.consume(consumerRecord))
                .isInstanceOf(MercadonaRuntimeException.class);
    }

    // --- helpers ---

    private ConsumerRecord<EmployeeEventPublicKey, EmployeeEventPublicValue> buildConsumerRecord(
            String employeeId, String managedGroupId) {
        var key = EmployeeEventPublicKey.newBuilder()
                .setId(employeeId)
                .setManagedGroupId(ManagedGroupIds.newBuilder().setId(managedGroupId).build())
                .build();
        return new ConsumerRecord<>(TEST_TOPIC, 0, 0L, key, mock(EmployeeEventPublicValue.class));
    }
}
