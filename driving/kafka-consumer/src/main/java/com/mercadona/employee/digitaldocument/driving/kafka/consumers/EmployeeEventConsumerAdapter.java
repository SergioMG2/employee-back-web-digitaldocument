package com.mercadona.employee.digitaldocument.driving.kafka.consumers;

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

    // TODO: inject DigitalDocumentOrchestratorPort once defined in the application module
    protected EmployeeEventConsumerAdapter(
            @Value("${fwkcna.kafka.consumer.topics.groups.employee.main}") String[] topics,
            @Value("${fwkcna.kafka.consumer.topics.groups.employee.group-id}") String groupId) {
        super(topics, groupId);
    }

    @Override
    public void consume(ConsumerRecord<EmployeeEventPublicKey, EmployeeEventPublicValue> consumerRecord) {
        try {
            log.info("Received employee event: employeeId={}, managedGroupId={}",
                    consumerRecord.key().getId(),
                    consumerRecord.key().getManagedGroupId());

            // TODO: mappear el consumo a objeto de dominio

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
