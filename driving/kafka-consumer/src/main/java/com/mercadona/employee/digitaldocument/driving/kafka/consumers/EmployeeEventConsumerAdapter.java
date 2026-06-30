package com.mercadona.employee.digitaldocument.driving.kafka.consumers;

import com.mercadona.employee.digitaldocument.application.ports.driving.DigitalDocumentConsumerPort;
import com.mercadona.framework.cna.commons.exception.MercadonaRuntimeException;
import com.mercadona.framework.cna.lib.kafka.consumers.KafkaConsumerListener;
import com.mercadona.framework.cna.lib.kafka.exceptions.BlockingLimitedRetryableException;
import com.mercadona.framework.cna.lib.kafka.exceptions.BlockingUnlimitedRetryableException;
import com.mercadona.framework.cna.lib.kafka.exceptions.NotRetryableException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import thirdparty.employee.employee.EmployeeEventPublicKey;
import thirdparty.employee.employee.EmployeeEventPublicValue;


@Slf4j
@Component
public class EmployeeEventConsumerAdapter
        extends KafkaConsumerListener<EmployeeEventPublicKey, EmployeeEventPublicValue> {

    private final DigitalDocumentConsumerPort consumerPort;

    protected EmployeeEventConsumerAdapter(
            @Value("${fwkcna.kafka.consumer.topics.groups.employee.main}") String[] topics,
            @Value("${fwkcna.kafka.consumer.topics.groups.employee.group-id}") String groupId,
            DigitalDocumentConsumerPort consumerPort) {
        super(topics, groupId);
        this.consumerPort = consumerPort;
    }

    @Override
    public void consume(ConsumerRecord<EmployeeEventPublicKey, EmployeeEventPublicValue> consumerRecord) {
        try {
            var employeeId = consumerRecord.key().getId();
            var managedGroupId = consumerRecord.key().getManagedGroupId().getId();

            log.info("Received employee event: employeeId={}, managedGroupId={}", employeeId, managedGroupId);

            consumerPort.process(employeeId, managedGroupId);

        } catch (BlockingLimitedRetryableException e) {
            // TODO: replace with your domain exception (e.g. catch (BusinessLimitedBlockingException e))
            throw new BlockingLimitedRetryableException("Blocking limited error.");
        } catch (BlockingUnlimitedRetryableException e) {
            // TODO: replace with your domain exception (e.g. catch (BusinessUnlimitedBlockingException e))
            throw new BlockingUnlimitedRetryableException("Blocking unlimited error.");
        } catch (NotRetryableException e) {
            // TODO: replace with your domain exception (e.g. catch (BusinessNotRetryableException e))
            throw new NotRetryableException("Not retryable exception.");
        } catch (Exception e) {
            // Any exception not mapped to a FWK Kafka exception triggers limited blocking retries by default
            throw new MercadonaRuntimeException("Unexpected error processing employee event.");
        }
    }
}
