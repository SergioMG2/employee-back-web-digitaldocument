package com.mercadona.employee.digitaldocument.driving.controllers.mappers;

import com.mercadona.employee.digitaldocument.domain.IdentificationTypesEnum;
import com.mercadona.employee.digitaldocument.domain.Example;


import com.mercadona.framework.cna.lib.web.builders.MercadonaPageResponseBuilder;
import com.mercadona.framework.cna.lib.web.builders.MercadonaPageResponseBuilderImpl;
import com.mercadona.framework.cna.commons.rest.api.model.Pagination;
import com.mercadona.framework.cna.commons.domain.MercadonaPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.mock.web.MockHttpServletRequest;

import java.time.OffsetDateTime;
import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
class ExampleDTOMapperTest {

  private static final long EXAMPLE_ID = 123;
  private static final String NAME = "test example name";
  private static final String DESCRIPTION = "test example description";
  private static final OffsetDateTime OFFSET_DATE_TIME = OffsetDateTime.now();
  private static final IdentificationTypesEnum IDENTIFICATION_TYPE = IdentificationTypesEnum.NIE;
  private static final String IDENTIFICATION = "Y1111111Y";
  private static final Integer NUMBER_OF_DAYS_IN_WEEK = 7;

  @Mock
  private MercadonaPageResponseBuilder mercadonaPageResponseBuilderMock;

  @Mock(answer = Answers.RETURNS_SELF)
  private MercadonaPageResponseBuilder.Builder mercadonaPageResponseBuilderBuilderMock;

  @InjectMocks
  private ExampleDTOMapperImpl exampleDTOMapper;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  @SneakyThrows
  @DisplayName("Should build a example resource collection response")
  void shouldBuildExampleResourceCollectionResponse() {

    var example = createExample();

    var examplePage = new PageImpl<>(Collections.singletonList(example));
    var exampleMercadonaPage = MercadonaPage.of(examplePage);

    var paginationMock = Pagination.builder()
      .requestedPage(1)
      .requestedSize(10)
      .retrievedResults(1)
      .totalResults(1L)
      .nextPage("http://localhost/democna/products/v2/products?firstPage=1")
      .previousPage("http://localhost/democna/products/v2/products?firstPage=1")
      .build();

    given(mercadonaPageResponseBuilderMock.builder()).willReturn(mercadonaPageResponseBuilderBuilderMock);
    given(mercadonaPageResponseBuilderBuilderMock.build()).willReturn(paginationMock);

    var exampleResourceCollection = exampleDTOMapper.toExampleResourceCollectionResponse(exampleMercadonaPage);

    assertEquals(exampleResourceCollection.getData().getExamples().get(0).getId(), example.getId());
    assertEquals(exampleResourceCollection.getData().getExamples().get(0).getName(), example.getName());
    assertEquals(exampleResourceCollection.getData().getExamples().get(0).getDescription(), example.getDescription());
    assertEquals(1, exampleResourceCollection.getData().getExamples().size());

  }

  @Test
  @SneakyThrows
  @DisplayName("Should build a example resource collection")
  void shouldBuildExampleResourceCollection() {

    var example = createExample();

    var examplePage = new PageImpl<>(Collections.singletonList(example));
    var exampleMercadonaPage = MercadonaPage.of(examplePage);

    var paginationMock = Pagination.builder()
      .requestedPage(1)
      .requestedSize(10)
      .retrievedResults(1)
      .totalResults(1L)
      .nextPage("http://localhost/democna/products/v2/products?firstPage=1")
      .previousPage("http://localhost/democna/products/v2/products?firstPage=1")
      .build();

    given(mercadonaPageResponseBuilderMock.builder()).willReturn(mercadonaPageResponseBuilderBuilderMock);
    given(mercadonaPageResponseBuilderBuilderMock.build()).willReturn(paginationMock);

    var exampleResourceCollection = exampleDTOMapper.getExampleResourceCollection(exampleMercadonaPage);

    assertEquals(exampleResourceCollection.getExamples().get(0).getId(), example.getId());
    assertEquals(exampleResourceCollection.getExamples().get(0).getName(), example.getName());
    assertEquals(exampleResourceCollection.getExamples().get(0).getDescription(), example.getDescription());
    assertEquals(1, exampleResourceCollection.getExamples().size());

  }

  @Test
  @SneakyThrows
  @DisplayName("Should build a example resource response")
  void shouldBuildExampleResourceResponse() {

    var example = createExample();

    var exampleResource = exampleDTOMapper.toExampleResourceResponse(example);

    assertEquals(exampleResource.getData().getId(), example.getId());
    assertEquals(exampleResource.getData().getName(), example.getName());
    assertEquals(exampleResource.getData().getDescription(), example.getDescription());

  }

  private Example createExample() {

    return new Example(
      EXAMPLE_ID,
      NAME,
      DESCRIPTION,
      OFFSET_DATE_TIME,
      IDENTIFICATION_TYPE,
      IDENTIFICATION,
      NUMBER_OF_DAYS_IN_WEEK
    );
  }

}
