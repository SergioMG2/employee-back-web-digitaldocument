package com.mercadona.employee.digitaldocument.driven.repositories;

import com.mercadona.employee.digitaldocument.driven.repositories.models.DigitalDocumentMO;
import com.mercadona.employee.digitaldocument.driven.repositories.models.DocumentStatusMOEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for {@link DigitalDocumentMO}.
 */
@Repository
public interface DigitalDocumentMOJpaRepository extends JpaRepository<DigitalDocumentMO, Long> {

    Optional<DigitalDocumentMO> findByDocumentId(String documentId);

    Optional<DigitalDocumentMO> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId);

    Page<DigitalDocumentMO> findByStatus(DocumentStatusMOEnum status, Pageable pageable);
}
