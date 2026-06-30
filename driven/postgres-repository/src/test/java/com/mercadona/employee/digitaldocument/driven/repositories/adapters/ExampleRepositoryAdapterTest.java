package com.mercadona.employee.digitaldocument.driven.repositories.adapters;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.framework.cna.lib.repository.builders.MercadonaPageBuilder;
import com.mercadona.framework.cna.lib.repository.builders.MercadonaPageBuilderImpl;
import com.mercadona.employee.digitaldocument.application.ports.driven.ExampleRepositoryPort;
import com.mercadona.employee.digitaldocument.domain.Example;
import com.mercadona.employee.digitaldocument.driven.repositories.mappers.ExampleMapper;
import com.mercadona.employee.digitaldocument.driven.repositories.models.ExampleMO;
import com.mercadona.employee.digitaldocument.driven.repositories.ExampleMOJpaRepository;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collections;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
class ExampleRepositoryAdapterTest {

  private static final long TEST_ID = 123;

  @Mock
  private ExampleMOJpaRepository exampleMOJpaRepositoryMock;

  @Mock(answer = Answers.RETURNS_SELF)
  private MercadonaPageBuilder mercadonaPageBuilderMock;

  @Mock
  private ExampleMapper exampleMapperMock;

  private ExampleRepositoryPort exampleCrudRepository;

  @BeforeEach
  void setUp() {

    MockitoAnnotations.initMocks(this);
    exampleCrudRepository = new ExampleRepositoryAdapter(
      mercadonaPageBuilderMock,
      exampleMOJpaRepositoryMock,
      exampleMapperMock);
  }

  @DisplayName("Should get a Example by id")
  @SneakyThrows
  @Test
  void shouldGetExample() {

    var testExampleMO = new ExampleMO();
    var testExample = Optional.of(new Example());

    given(exampleMOJpaRepositoryMock.findById(anyLong()))
      .willReturn(Optional.of(testExampleMO));

    given(exampleMapperMock.fromOptionalModel(any(Optional.class)))
      .willReturn(testExample);

    var ExampleRetrieved = exampleCrudRepository.findById(TEST_ID);

    assertEquals(ExampleRetrieved, testExample);

    then(exampleMOJpaRepositoryMock).should().findById(TEST_ID);
  }

  @DisplayName("Should get a page from all Example")
  @SneakyThrows
  @Test
  void shouldGetExamples() {

    var testPage = new PageImpl<>(Collections.singletonList(new ExampleMO()));

    var mercadonaTestPage = MercadonaPage.of(testPage);

    given(mercadonaPageBuilderMock.builder()).willReturn(new MercadonaPageBuilderImpl.Builder(new MercadonaPageBuilderImpl(true, 1, 10)));

    given(exampleMOJpaRepositoryMock.findAll(any(PageRequest.class))).willReturn(testPage);

    given(exampleMapperMock.fromModels(any(Page.class))).willReturn(mercadonaTestPage);

    var ExamplesPage = exampleCrudRepository.findAll(1, 10, "+id");

    assertEquals(ExamplesPage, mercadonaTestPage);

    then(exampleMOJpaRepositoryMock).should().findAll(any(PageRequest.class));
  }

  @Test
  @SneakyThrows
  @DisplayName("Should create a Example")
  void shouldCreateExample() {

    var testExample = new Example();
    var testExampleMO = new ExampleMO();

    given(exampleMapperMock.toModel(any(Example.class))).willReturn(testExampleMO);

    given(exampleMOJpaRepositoryMock.save(testExampleMO)).willReturn(testExampleMO);

    given(exampleMapperMock.fromModel(any(ExampleMO.class))).willReturn(testExample);

    var ExampleRetrieved = exampleCrudRepository.save(testExample);

    assertEquals(ExampleRetrieved, testExample);

    then(exampleMOJpaRepositoryMock).should().save(testExampleMO);
  }

}
