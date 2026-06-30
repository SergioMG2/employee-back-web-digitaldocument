package com.mercadona.employee.digitaldocument.driven.repositories.mappers;

import com.mercadona.employee.digitaldocument.domain.Example;
import com.mercadona.employee.digitaldocument.driven.repositories.models.ExampleMO;
import com.mercadona.framework.cna.commons.domain.MercadonaPage;

import org.mapstruct.Mapper;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

@Mapper(componentModel = "spring")
public interface ExampleMapper {

  Example fromModel(ExampleMO exampleMO);

  ExampleMO toModel(Example example);

  default MercadonaPage<Example> fromModels(Page<ExampleMO> examplesModelPage) {

    var page = examplesModelPage.map(this::fromModel);
    return MercadonaPage.of(page);
  }

  default Optional<Example> fromOptionalModel(Optional<ExampleMO> optionalExampleMO) {

    return (optionalExampleMO.isEmpty()) ? Optional.empty() : Optional.of(fromModel(optionalExampleMO.get()));

  }

  List<Example> listExampleMOToExample (List<ExampleMO> list);
}
