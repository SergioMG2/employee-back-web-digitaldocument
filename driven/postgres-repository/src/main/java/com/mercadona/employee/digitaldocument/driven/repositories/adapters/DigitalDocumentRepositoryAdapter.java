package com.mercadona.employee.digitaldocument.driven.repositories.adapters;

import com.mercadona.employee.digitaldocument.application.ports.driven.DigitalDocumentRepositoryPort;
import com.mercadona.employee.digitaldocument.domain.DigitalDocument;
import com.mercadona.employee.digitaldocument.domain.DocumentStatus;
import com.mercadona.employee.digitaldocument.driven.repositories.DigitalDocumentMOJpaRepository;
import com.mercadona.employee.digitaldocument.driven.repositories.mappers.DigitalDocumentMapper;
import com.mercadona.employee.digitaldocument.driven.repositories.models.DocumentStatusMOEnum;
import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * JPA adapter that implements {@link DigitalDocumentRepositoryPort}.
 *
 * <p>Translates between the domain model and the JPA entity using
 * {@link DigitalDocumentMapper}, and delegates persistence to
 * {@link DigitalDocumentMOJpaRepository}.
 */
@Slf4j
@Service
@AllArgsConstructor
public class DigitalDocumentRepositoryAdapter implements DigitalDocumentRepositoryPort {

    private final DigitalDocumentMOJpaRepository repository;
    private final DigitalDocumentMapper mapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitalDocument save(DigitalDocument digitalDocument) {
        var mo = mapper.toModel(digitalDocument);
        var saved = repository.save(mo);
        return mapper.fromModel(saved);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DigitalDocument> findByDocumentId(String documentId) {
        return repository.findByDocumentId(documentId)
                .map(mapper::fromModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<DigitalDocument> findByEmployeeIdAndManagedGroupId(String employeeId, String managedGroupId) {
        return repository.findByEmployeeIdAndManagedGroupId(employeeId, managedGroupId)
                .map(mapper::fromModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MercadonaPage<DigitalDocument> findByStatus(DocumentStatus status, Integer pageNumber, Integer pageSize) {
        var statusMO = DocumentStatusMOEnum.valueOf(status.name());
        var pageable = PageRequest.of(pageNumber, pageSize);
        var page = repository.findByStatus(statusMO, pageable);
        return MercadonaPage.of(page.map(mapper::fromModel));
    }
}
