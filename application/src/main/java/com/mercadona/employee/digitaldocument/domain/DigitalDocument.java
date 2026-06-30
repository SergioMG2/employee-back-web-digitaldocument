package com.mercadona.employee.digitaldocument.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Domain entity representing the lifecycle of a digital employee document.
 *
 * <p>Tracks the state of each document from the moment the employee event is
 * received until the availability notification is published to Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DigitalDocument {

    /** Internal surrogate primary key. */
    private Long id;

    /** Externally visible unique document identifier (UUID). */
    private String documentId;

    /** Employee identifier received in the Kafka event. */
    private String employeeId;

    /** Managed group identifier received in the Kafka event. */
    private String managedGroupId;

    /** Current lifecycle state of the document. */
    private DocumentStatus status;

    /**
     * Step where the process failed.
     * Only populated when {@code status} is {@link DocumentStatus#FAILED}.
     */
    private String failedStep;

    /**
     * Storage path of the generated PDF in the bucket.
     * Only populated when {@code status} is {@link DocumentStatus#STORED} or later.
     */
    private String bucketPath;

    /** Timestamp when this record was created. */
    private OffsetDateTime createdAt;

    /** Timestamp of the last status update. */
    private OffsetDateTime updatedAt;
}
