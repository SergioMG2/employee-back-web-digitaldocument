package com.mercadona.employee.digitaldocument.driven.repositories.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * JPA entity mapping to the {@code o_digital_documents} table.
 *
 * <p>Represents the persisted lifecycle state of a digital employee document.
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "o_digital_documents")
public class DigitalDocumentMO {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_id", nullable = false, unique = true, length = 36)
    private String documentId;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "managed_group_id", nullable = false)
    private String managedGroupId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private DocumentStatusMOEnum status;

    @Column(name = "failed_step", length = 50)
    private String failedStep;

    @Column(name = "bucket_path", length = 500)
    private String bucketPath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
