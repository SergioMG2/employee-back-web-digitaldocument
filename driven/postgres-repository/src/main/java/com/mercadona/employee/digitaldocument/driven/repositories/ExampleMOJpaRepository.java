package com.mercadona.employee.digitaldocument.driven.repositories;

import com.mercadona.employee.digitaldocument.driven.repositories.models.ExampleMO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExampleMOJpaRepository extends JpaRepository<ExampleMO, Long> { }
