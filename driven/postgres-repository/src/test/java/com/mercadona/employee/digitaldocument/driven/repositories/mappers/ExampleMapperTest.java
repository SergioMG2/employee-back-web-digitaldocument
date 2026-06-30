package com.mercadona.employee.digitaldocument.driven.repositories.mappers;

import com.mercadona.employee.digitaldocument.driven.repositories.mappers.ExampleMapper;
import com.mercadona.employee.digitaldocument.driven.repositories.mappers.ExampleMapperImpl;
import com.mercadona.employee.digitaldocument.driven.repositories.models.ExampleMO;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
class ExampleMapperTest {

  private ExampleMapper exampleMapper;

  @BeforeEach
  public void setup() {
    exampleMapper = new ExampleMapperImpl();
  }

  @Test
  @DisplayName("Should return a MercadonaPage of examples for a given Page of examples")
  void shouldReturnMercadonaPageOfProductsForGivenPageOfProducts() {

    var products = List.of(new ExampleMO());

    var examplePage = new PageImpl<>(products);

    var mercadonaExamplesPage = exampleMapper.fromModels(examplePage);

    assertEquals(mercadonaExamplesPage.getNumber(), examplePage.getNumber());
    assertEquals(mercadonaExamplesPage.getSize(), examplePage.getSize());
    assertEquals(mercadonaExamplesPage.getNumberOfElements(), examplePage.getNumberOfElements());
    assertEquals(mercadonaExamplesPage.getTotalPages(), examplePage.getTotalPages());
    assertEquals(mercadonaExamplesPage.getTotalElements(), examplePage.getTotalElements());

  }

  @Test
  @DisplayName("Should return an empty optional for a given empty optional model")
  void shouldReturnAnEmptyOptionalForEmptyOptionalModel() {

    var optionalExample = exampleMapper.fromOptionalModel(Optional.empty());

    assertEquals(Boolean.TRUE, optionalExample.isEmpty());
  }

  @Test
  @DisplayName("Should return a optional with an object for a given optional model")
  void shouldReturnAOptionalWithAnObjectForOptionalModel() {

    var optionalExampleModel = Optional.of(new ExampleMO());

    var optionalExample = exampleMapper.fromOptionalModel(optionalExampleModel);

    assertEquals(Boolean.FALSE, optionalExample.isEmpty());
  }

}
