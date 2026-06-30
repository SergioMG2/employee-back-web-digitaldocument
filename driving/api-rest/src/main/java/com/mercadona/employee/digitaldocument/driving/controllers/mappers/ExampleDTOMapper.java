package com.mercadona.employee.digitaldocument.driving.controllers.mappers;

import com.mercadona.employee.digitaldocument.domain.Example;

import com.mercadona.framework.cna.api.demotest.arq.web.model.*;
import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.framework.cna.lib.web.builders.MercadonaPageResponseBuilder;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = { MercadonaPageResponseBuilder.class })
public abstract class ExampleDTOMapper {

  @Autowired
  private MercadonaPageResponseBuilder mercadonaPageResponseBuilder;

  static final ExampleDTOMapper INSTANCE = Mappers.getMapper(ExampleDTOMapper.class);

  public abstract Example from(ExampleRequest exampleForm);

  public abstract ExampleResource to(Example example);

  public ExampleResourceCollectionResponse toExampleResourceCollectionResponse(MercadonaPage<Example> examples) {

    var pagination = mercadonaPageResponseBuilder
      .builder()
      .requestedPage(examples.getNumber())
      .requestedSize(examples.getSize())
      .retrievedResults(examples.getNumberOfElements())
      .totalResults(examples.getTotalElements())
      .buildNextPage(examples.getNumber(), examples.getSize(), examples.getTotalPages())
      .buildPreviousPage(examples.getNumber(), examples.getSize())
      .build();

    var exampleResourceCollection = getExampleResourceCollection(examples);
    return ExampleResourceCollectionResponse
      .builder()
      .data(exampleResourceCollection)
      .pagination(pagination)
      .build();

  }

  public ExampleResourceCollection getExampleResourceCollection(MercadonaPage<Example> products) {

    return ExampleResourceCollection.builder()
      .examples(listExampleToListExampleResource(products.getContent()))
      .build();

  }

  public ExampleResourceResponse toExampleResourceResponse(Example example) {

    return new ExampleResourceResponse(INSTANCE.to(example));

  }

  public abstract List<ExampleResource> listExampleToListExampleResource (List<Example> list);

}
