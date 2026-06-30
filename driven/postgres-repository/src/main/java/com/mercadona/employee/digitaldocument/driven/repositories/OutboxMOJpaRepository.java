package com.mercadona.employee.digitaldocument.driven.repositories;

import com.mercadona.employee.digitaldocument.driven.repositories.models.OutboxMO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OutboxMOJpaRepository extends JpaRepository<OutboxMO, Long> {
}
