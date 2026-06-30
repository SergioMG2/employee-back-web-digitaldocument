package com.mercadona.employee.digitaldocument.driven.repositories.adapters;

import java.util.Optional;

import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.framework.cna.lib.repository.builders.MercadonaPageBuilder;
import org.springframework.stereotype.Service;

import com.mercadona.employee.digitaldocument.domain.Example;
import com.mercadona.employee.digitaldocument.driven.repositories.mappers.ExampleMapper;
import com.mercadona.employee.digitaldocument.driven.repositories.ExampleMOJpaRepository;
import com.mercadona.employee.digitaldocument.application.ports.driven.ExampleRepositoryPort;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@AllArgsConstructor
public class ExampleRepositoryAdapter implements ExampleRepositoryPort {

  private final MercadonaPageBuilder mercadonaPageBuilder;

  private final ExampleMOJpaRepository repository;

  private final ExampleMapper mapper;

  @Override
  public Optional<Example> findById(Long id) {

    var exampleMO = repository.findById(id);

    return mapper.fromOptionalModel(exampleMO);

  }

  @Override
  public MercadonaPage<Example> findAll(Integer pageNumber, Integer pageSize, String sort) {

    var pageRequest =  mercadonaPageBuilder.builder().page(pageNumber).pageSize(pageSize).sort(sort).build();

    var exampleMOs = repository.findAll(pageRequest);

    return mapper.fromModels(exampleMOs);

  }

  @Override
  public Example save(Example example) {

    var exampleModel = mapper.toModel(example);

    var exampleSaved = repository.save(exampleModel);

    return mapper.fromModel(exampleSaved);

  }

  @Override
  public void deleteById(Long id) {

    repository.deleteById(id);

  }

}
