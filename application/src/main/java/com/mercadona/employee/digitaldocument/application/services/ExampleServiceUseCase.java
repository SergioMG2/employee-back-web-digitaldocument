package com.mercadona.employee.digitaldocument.application.services;

import com.mercadona.employee.digitaldocument.domain.Example;
import com.mercadona.employee.digitaldocument.application.ports.driving.ExampleServicePort;
import com.mercadona.employee.digitaldocument.application.ports.driven.ExampleRepositoryPort;
import com.mercadona.employee.digitaldocument.application.exceptions.ExampleNotFoundException;
import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import java.util.Optional;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;

@Slf4j
@Service
@AllArgsConstructor
public class ExampleServiceUseCase implements ExampleServicePort {

  private final ExampleRepositoryPort exampleRepository;
  private static final String ERROR_CODE = "C01";

  @Override
  public MercadonaPage<Example> getAllExamples(Integer pageNumber, Integer pageSize, String sort) {

    return exampleRepository.findAll(pageNumber, pageSize, sort);

  }

  @Override
  public Optional<Example> getExample(Long id) throws ExampleNotFoundException {

    return exampleRepository
      .findById(id);


  }

  @Override
  public Example createExample(Example example) {

    return exampleRepository.save(example);

  }

  @Override
  public Example updateExample(Long id, Example exampleUpdate) {

    var example = getExample(id).orElseThrow(() -> new ExampleNotFoundException(String.format("Example with id %s not found.", id), ERROR_CODE));

    example.setName(example.getName());
    example.setDescription(exampleUpdate.getDescription());
    example.setCreationTime(exampleUpdate.getCreationTime());
    example.setIdentification(exampleUpdate.getIdentification());
    example.setIdentificationType(exampleUpdate.getIdentificationType());
    example.setNumberOfDaysInWeek(exampleUpdate.getNumberOfDaysInWeek());

    return exampleRepository.save(example);

  }

  @Override
  public void deleteExample(Long id) {

    var example = getExample(id).orElseThrow(() -> new ExampleNotFoundException(String.format("Example with id %s not found.", id), ERROR_CODE));

    exampleRepository.deleteById(example.getId());

  }

}
