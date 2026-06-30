package com.mercadona.employee.digitaldocument.application.services;

import com.mercadona.employee.digitaldocument.domain.Example;
import com.mercadona.employee.digitaldocument.application.exceptions.ExampleNotFoundException;
import com.mercadona.employee.digitaldocument.application.ports.driving.ExampleServicePort;
import com.mercadona.employee.digitaldocument.application.ports.driven.ExampleRepositoryPort;
import com.mercadona.employee.digitaldocument.domain.IdentificationTypesEnum;
import com.mercadona.framework.cna.commons.domain.MercadonaPage;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class ExampleServiceUseCaseTest {

  private static final long EXAMPLE_ID = 123;
  private static final String EXAMPLE_NAME = "exampleName";
  private static final String EXAMPLE_DESCRIPTION = "exampleDescription";
  private static final OffsetDateTime EXAMPLE_OFFSET_DATE_TIME = OffsetDateTime.now();
  private static final IdentificationTypesEnum IDENTIFICATION_TYPE = IdentificationTypesEnum.NIE;
  private static final String IDENTIFICATION = "Y1111111Y";
  private static final Integer NUMBER_OF_DAYS_IN_WEEK = 7;
  private static final String DEFAULT_SORT="+id";

  @Mock
  private ExampleRepositoryPort exampleRepositoryMock;

  private ExampleServicePort service;

  @BeforeEach
  public void before() {
    service = new ExampleServiceUseCase(exampleRepositoryMock);
  }

  @Test
  @DisplayName("Should get a page of examples")
  void shouldGetAPageOfExamples() {

    var examplesListMock = List.of(
      new Example(
        1L,
        "name-one",
        "description-one",
        EXAMPLE_OFFSET_DATE_TIME,
        IdentificationTypesEnum.NIE,
        "Y1111111Y",
        7),
      new Example(
        2L,
        "name-two",
        "description-two",
        EXAMPLE_OFFSET_DATE_TIME,
        IdentificationTypesEnum.NIE,
        "Y1111111Y",
        7)
    );

    var mockPageExamples = new PageImpl<>(examplesListMock);

    var mockMercadonaPageOfExamples = MercadonaPage.of(mockPageExamples);

    given(exampleRepositoryMock.findAll(anyInt(), anyInt(), anyString())).willReturn(mockMercadonaPageOfExamples);


    var examplesPage = service.getAllExamples(1, 10, DEFAULT_SORT);
    var examples = examplesPage.getContent();

    assertNotNull(examples);
    assertEquals(2, examples.size());

    var exampleOne = examples.get(0);
    var exampleTwo = examples.get(1);

    assertEquals(1L, exampleOne.getId());
    assertEquals("name-one", exampleOne.getName());
    assertEquals("description-one", exampleOne.getDescription());
    assertThat(exampleOne.getCreationTime(), instanceOf(OffsetDateTime.class));

    assertEquals(2L, exampleTwo.getId());
    assertEquals("name-two", exampleTwo.getName());
    assertEquals("description-two", exampleTwo.getDescription());
    assertThat(exampleTwo.getCreationTime(), instanceOf(OffsetDateTime.class));
  }

  @Test
  @DisplayName("Should get an example for a given id")
  void shouldGetExampleWithId() {

    var optionalExample = Optional.of(
      new Example(
        123L,
        "example-name",
        "example-description",
        EXAMPLE_OFFSET_DATE_TIME,
        IdentificationTypesEnum.NIE,
        "Y1111111Y",
        7
      ));

    given(exampleRepositoryMock.findById(anyLong()))
      .willReturn(optionalExample);


    var example = service.getExample(EXAMPLE_ID).get();

    assertNotNull(example);
    assertEquals(EXAMPLE_ID, example.getId());
    assertEquals("example-name", example.getName());
    assertEquals("example-description", example.getDescription());
    assertThat(example.getCreationTime(), instanceOf(OffsetDateTime.class));
  }


  @Test
  @SneakyThrows
  @DisplayName("Should create an example successfully")
  void shouldCreateExampleWithValidInput() {

    var example = new Example(
      EXAMPLE_ID,
      EXAMPLE_NAME,
      EXAMPLE_DESCRIPTION,
      EXAMPLE_OFFSET_DATE_TIME,
      IDENTIFICATION_TYPE,
      IDENTIFICATION,
      NUMBER_OF_DAYS_IN_WEEK
    );

    given(exampleRepositoryMock.save(any(Example.class))).willReturn(example);

    var exampleOutput = service.createExample(example);

    assertNotNull(exampleOutput);
    assertEquals(EXAMPLE_ID, exampleOutput.getId());
    assertEquals(EXAMPLE_NAME, exampleOutput.getName());
    assertEquals(EXAMPLE_DESCRIPTION, exampleOutput.getDescription());
    assertEquals(EXAMPLE_OFFSET_DATE_TIME, exampleOutput.getCreationTime());
    assertEquals(IDENTIFICATION_TYPE, exampleOutput.getIdentificationType());
    assertEquals(IDENTIFICATION, exampleOutput.getIdentification());
    assertEquals(NUMBER_OF_DAYS_IN_WEEK, exampleOutput.getNumberOfDaysInWeek());

  }

  @Test
  @SneakyThrows
  @DisplayName("Should update an example")
  void shouldUpdateAnExample() {

    var oldExample = new Example(
      EXAMPLE_ID,
      EXAMPLE_NAME,
      EXAMPLE_DESCRIPTION,
      EXAMPLE_OFFSET_DATE_TIME,
      IDENTIFICATION_TYPE,
      IDENTIFICATION,
      NUMBER_OF_DAYS_IN_WEEK
    );

    var example = new Example(
      EXAMPLE_ID,
      EXAMPLE_NAME,
      EXAMPLE_DESCRIPTION,
      EXAMPLE_OFFSET_DATE_TIME,
      IDENTIFICATION_TYPE,
      IDENTIFICATION,
      6
    );

    given(exampleRepositoryMock.findById(anyLong())).willReturn(Optional.of(oldExample));

    given(exampleRepositoryMock.save(any(Example.class))).willReturn(example);

    var exampleOutput = service.updateExample(example.getId(), example);

    assertNotNull(exampleOutput);
    assertEquals(EXAMPLE_ID, exampleOutput.getId());
    assertEquals(EXAMPLE_NAME, exampleOutput.getName());
    assertEquals(EXAMPLE_DESCRIPTION, exampleOutput.getDescription());
    assertEquals(EXAMPLE_OFFSET_DATE_TIME, exampleOutput.getCreationTime());
    assertEquals(IDENTIFICATION_TYPE, exampleOutput.getIdentificationType());
    assertEquals(IDENTIFICATION, exampleOutput.getIdentification());
    assertEquals(6, exampleOutput.getNumberOfDaysInWeek());

  }

  @Test
  @SneakyThrows
  @DisplayName("Should throw a ExampleNotFoundException when the example to update does not exist")
  void shouldThrowAnExceptionIfTheExampleToUpdateDoesNotExist() {

    var example = new Example(
      EXAMPLE_ID,
      EXAMPLE_NAME,
      EXAMPLE_DESCRIPTION,
      EXAMPLE_OFFSET_DATE_TIME,
      IDENTIFICATION_TYPE,
      IDENTIFICATION,
      6
    );

    given(exampleRepositoryMock.findById(anyLong())).willReturn(Optional.ofNullable(null));

    Executable execution = () -> service.updateExample(example.getId(), example);

    assertThrows(ExampleNotFoundException.class, execution);

  }

  @Test
  @SneakyThrows
  @DisplayName("Should delete an example")
  void shouldDeleteAExample() {

    var example = new Example(
      EXAMPLE_ID,
      EXAMPLE_NAME,
      EXAMPLE_DESCRIPTION,
      EXAMPLE_OFFSET_DATE_TIME,
      IDENTIFICATION_TYPE,
      IDENTIFICATION,
      NUMBER_OF_DAYS_IN_WEEK
    );

    given(exampleRepositoryMock.findById(anyLong())).willReturn(Optional.of(example));

    willDoNothing().given(exampleRepositoryMock).deleteById(anyLong());

    service.deleteExample(EXAMPLE_ID);

    verify(exampleRepositoryMock, times(1)).findById(anyLong());
    verify(exampleRepositoryMock, times(1)).deleteById(anyLong());

  }

  @Test
  @SneakyThrows
  @DisplayName("Should throw a ExampleNotFoundException when the required example to delete does not exist")
  void shouldThrowAnExceptionIfTheExampleDoesNotExists() {

    given(exampleRepositoryMock.findById(anyLong())).willReturn(Optional.ofNullable(null));

    Executable execution = () -> service.deleteExample(EXAMPLE_ID);

    assertThrows(ExampleNotFoundException.class, execution);

  }

}
