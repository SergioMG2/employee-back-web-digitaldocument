package com.mercadona.employee.digitaldocument.driving.controllers.adapters;


import com.mercadona.employee.digitaldocument.domain.Example;
import com.mercadona.employee.digitaldocument.application.ports.driving.ExampleServicePort;
import com.mercadona.employee.digitaldocument.domain.IdentificationTypesEnum;
import com.mercadona.employee.digitaldocument.application.exceptions.ExampleNotFoundException;
import com.mercadona.employee.digitaldocument.driving.controllers.mappers.ExampleDTOMapper;

import com.mercadona.framework.cna.api.demotest.arq.web.model.*;
import com.mercadona.framework.cna.commons.domain.MercadonaPage;
import com.mercadona.framework.cna.lib.error.handler.handlers.DefaultExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mercadona.employee.digitaldocument.driving.controllers.adapters.ExampleControllerAdapter;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.data.domain.PageImpl;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.mercadona.employee.digitaldocument.driving.controllers.config.WebSecurityConfigurer;
import com.mercadona.employee.digitaldocument.driving.controllers.error.ExampleAPIControllerAdvice;


@WebMvcTest
@WithMockUser
@Import(DefaultExceptionHandler.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {   ExampleControllerAdapter.class,
                                    ExampleAPIControllerAdvice.class,
                                    WebSecurityConfigurer.class })

class ExampleControllerAdapterTest {

  private static final long EXAMPLE_ID = 123L;

  private static final String ID_PATTERN = "$.data.id";
  private static final String NAME_PATTERN = "$.data.name";
  private static final String DESCRIPTION_PATTERN = "$.data.description";
  private static final String CREATION_TIME_PATTERN = "$.data.creationTime";
  private static final String IDENTIFICATION_TYPE_PATTERN = "$.data.identificationType";
  private static final String IDENTIFICATION_PATTERN = "$.data.identification";
  private static final String NUMBER_OF_DAYS_IN_WEEK_PATTERN = "$.data.numberOfDaysInWeek";

  private static final String ERROR_CODE_PATTERN = "$.error.code";
  private static final String ERROR_DESCRIPTION_PATTERN = "$.error.description";
  private static final String ERROR_DETAILS_PATTERN = "$.error.details";

  private static final OffsetDateTime TEST_OFFSET_DATE_TIME =  OffsetDateTime.now();
  private static final IdentificationTypesEnum IDENTIFICATION_TYPE = IdentificationTypesEnum.NIE;
  private static final IdentificationTypesRequestEnum IDENTIFICATION_TYPE_ENUM_NIE = IdentificationTypesRequestEnum.NIE;
  private static final String IDENTIFICATION = "Y1111111Y";
  private static final Integer NUMBER_OF_DAYS_IN_WEEK = 7;

  private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME;
  private static final String CODE_BUSINESS_ERROR = "CO1";

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private ExampleServicePort exampleService;

  @MockBean
  private ExampleDTOMapper exampleDTOMapperMock;

  @Test
  @SneakyThrows
  @DisplayName("Should get an example by resource id")
  void shouldGetExample() {

    var testExample = getTestExample();
    var testExampleResource = getTestExampleResourceResponse();

    given(exampleService.getExample(testExample.getId())).willReturn(Optional.of(testExample));
    given(exampleDTOMapperMock.toExampleResourceResponse(testExample)).willReturn(testExampleResource);

    mockMvc.perform(get("/examples/{id}", EXAMPLE_ID)
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath(ID_PATTERN, is(testExample.getId().intValue())))
      .andExpect(jsonPath(NAME_PATTERN, is(testExample.getName())))
      .andExpect(jsonPath(DESCRIPTION_PATTERN, is(testExample.getDescription())))
      .andExpect(jsonPath(CREATION_TIME_PATTERN, is(dateTimeFormatter.format(testExample.getCreationTime()))))
      .andExpect(jsonPath(IDENTIFICATION_TYPE_PATTERN, is(testExample.getIdentificationType().toString())))
      .andExpect(jsonPath(IDENTIFICATION_PATTERN, is(testExample.getIdentification())))
      .andExpect(jsonPath(NUMBER_OF_DAYS_IN_WEEK_PATTERN, is(testExample.getNumberOfDaysInWeek())));

  }

  @Test
  @SneakyThrows
  @DisplayName("Should return a 404 status code when the required example does not exist")
  void shouldReturnANotFoundStatusCodeWhenTheExampleDoesNotExist() {

    given(exampleService.getExample(anyLong())).willThrow(new ExampleNotFoundException(String.format("Example with id %s not found.", EXAMPLE_ID),CODE_BUSINESS_ERROR));

    mockMvc.perform(get("/examples/{id}", EXAMPLE_ID)
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().is4xxClientError())
      .andExpect(jsonPath(ERROR_CODE_PATTERN, is(CODE_BUSINESS_ERROR)))
      .andExpect(jsonPath(ERROR_DETAILS_PATTERN + "[0]", is(String.format("Example with id %s not found.", EXAMPLE_ID))));

  }

  @Test
  @SneakyThrows
  @DisplayName("Should get a collection of examples")
  void shouldGetExampleCollection() {

    var testExample = getTestExample();

    var testExamples = new PageImpl<>(Collections.singletonList(testExample));
    var testMercadonaPageExample = MercadonaPage.of(testExamples);
    var testExampleResourcesResponse = getTestExampleResourceCollectionResponse();

    given(exampleService.getAllExamples(anyInt(), anyInt(),anyString())).willReturn(testMercadonaPageExample);
    given(exampleDTOMapperMock.toExampleResourceCollectionResponse(testMercadonaPageExample)).willReturn(testExampleResourcesResponse);

    mockMvc.perform(get("/examples").param("firstPage", "1").param("pageSize","10").param("sort", "+id")
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.examples.[0].id", is(testExample.getId().intValue())))
      .andExpect(jsonPath("$.data.examples.[0].name", is(testExample.getName())))
      .andExpect(jsonPath("$.data.examples.[0].description", is(testExample.getDescription())))
      .andExpect(jsonPath("$.data.examples.[0].creationTime", is(dateTimeFormatter.format(testExample.getCreationTime()))))
      .andExpect(jsonPath("$.data.examples.[0].identificationType", is(testExample.getIdentificationType().toString())))
      .andExpect(jsonPath("$.data.examples.[0].identification", is(testExample.getIdentification())))
      .andExpect(jsonPath("$.data.examples.[0].numberOfDaysInWeek", is(testExample.getNumberOfDaysInWeek())));

  }

  @Test
  @SneakyThrows
  @DisplayName("Should create an example")
  void shouldAddExample() {

    var exampleRequest = getTestExampleRequest();
    var testExample = getTestExample();
    var testExampleResourceResponse = getTestExampleResourceResponse();

    given(exampleDTOMapperMock.from(any(ExampleRequest.class))).willReturn(testExample);
    given(exampleService.createExample(any(Example.class))).willReturn(testExample);
    given(exampleDTOMapperMock.toExampleResourceResponse(any(Example.class))).willReturn(testExampleResourceResponse);

    mockMvc.perform(post("/examples")
      .contentType(MediaType.APPLICATION_JSON)
      .content(objectMapper.writeValueAsString(exampleRequest)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath(ID_PATTERN, is(testExample.getId().intValue())))
      .andExpect(jsonPath(NAME_PATTERN, is(testExample.getName())))
      .andExpect(jsonPath(DESCRIPTION_PATTERN, is(testExample.getDescription())))
      .andExpect(jsonPath(CREATION_TIME_PATTERN, is(dateTimeFormatter.format(testExample.getCreationTime()))))
      .andExpect(jsonPath(IDENTIFICATION_TYPE_PATTERN, is(testExample.getIdentificationType().toString())))
      .andExpect(jsonPath(IDENTIFICATION_PATTERN, is(testExample.getIdentification())))
      .andExpect(jsonPath(NUMBER_OF_DAYS_IN_WEEK_PATTERN, is(testExample.getNumberOfDaysInWeek())));

  }

  @Test
  @SneakyThrows
  @DisplayName("Should update an example by its id")
  void shouldUpdateExampleByItsId() {

    var exampleRequest = getTestExampleRequest();
    var testExample = getTestExample();
    var testExampleResource = getTestExampleResourceResponse();

    given(exampleDTOMapperMock.from(any(ExampleRequest.class))).willReturn(testExample);

    given(exampleService.updateExample(testExample.getId(), testExample)).willReturn(testExample);

    given(exampleDTOMapperMock.toExampleResourceResponse(testExample)).willReturn(testExampleResource);

    mockMvc.perform(put("/examples/{id}", EXAMPLE_ID)
      .content(objectMapper.writeValueAsString(exampleRequest))
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath(ID_PATTERN, is(testExample.getId().intValue())))
      .andExpect(jsonPath(NAME_PATTERN, is(testExample.getName())))
      .andExpect(jsonPath(DESCRIPTION_PATTERN, is(testExample.getDescription())))
      .andExpect(jsonPath(CREATION_TIME_PATTERN, is(dateTimeFormatter.format(testExample.getCreationTime()))))
      .andExpect(jsonPath(IDENTIFICATION_TYPE_PATTERN, is(testExample.getIdentificationType().toString())))
      .andExpect(jsonPath(IDENTIFICATION_PATTERN, is(testExample.getIdentification())))
      .andExpect(jsonPath(NUMBER_OF_DAYS_IN_WEEK_PATTERN, is(testExample.getNumberOfDaysInWeek())));

  }

  @Test
  @SneakyThrows
  @DisplayName("Should return a not found status when the example to update does not exist")
  void shouldReturnANotFoundStatusWhenTheExampleToUpdateDoesNotExist() {

    var exampleRequest = getTestExampleRequest();
    var testExample = getTestExample();
    var testExampleResource = getTestExampleResourceResponse();

    given(exampleDTOMapperMock.from(any(ExampleRequest.class))).willReturn(testExample);

    given(exampleService.updateExample(testExample.getId(), testExample))
      .willThrow(new ExampleNotFoundException(String.format("Example with id %s not found.", EXAMPLE_ID),CODE_BUSINESS_ERROR));

    given(exampleDTOMapperMock.toExampleResourceResponse(testExample)).willReturn(testExampleResource);

    mockMvc.perform(put("/examples/{id}", EXAMPLE_ID)
      .content(objectMapper.writeValueAsString(exampleRequest))
      .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().is4xxClientError())
      .andExpect(jsonPath(ERROR_CODE_PATTERN, is(CODE_BUSINESS_ERROR)))
      .andExpect(jsonPath(ERROR_DETAILS_PATTERN + "[0]", is(String.format("Example with id %s not found.", EXAMPLE_ID))));
  }

  @Test
  @SneakyThrows
  @DisplayName("Should delete an example by its id")
  void shouldDeleteExampleByItsId() {

    willDoNothing().given(exampleService).deleteExample(EXAMPLE_ID);

    mockMvc.perform(delete("/examples/{id}", EXAMPLE_ID))
      .andExpect(status().isNoContent());
  }

  @Test
  @SneakyThrows
  @DisplayName("Should return a not found status when the example to delete does not exist")
  void shouldReturnANotFoundStatusWhenTheExampleToDeleteDoesNotExist() {

    doThrow(new ExampleNotFoundException(String.format("Example with id %s not found.", EXAMPLE_ID),CODE_BUSINESS_ERROR))
      .when(exampleService)
      .deleteExample(anyLong());

    mockMvc.perform(delete("/examples/{id}", EXAMPLE_ID))
      .andExpect(status().is4xxClientError())
      .andExpect(jsonPath(ERROR_CODE_PATTERN, is(CODE_BUSINESS_ERROR)))
      .andExpect(jsonPath(ERROR_DETAILS_PATTERN + "[0]", is(String.format("Example with id %s not found.", EXAMPLE_ID))));
  }


  private ExampleResource getTestExampleResource() {

    return ExampleResource.builder()
      .id(EXAMPLE_ID)
      .name("test name")
      .description("Test descripcion")
      .creationTime(TEST_OFFSET_DATE_TIME)
      .identificationType(IDENTIFICATION_TYPE_ENUM_NIE)
      .identification(IDENTIFICATION)
      .numberOfDaysInWeek(NUMBER_OF_DAYS_IN_WEEK)
      .build();

  }

  private Example getTestExample() {

    return Example.builder()
      .id(EXAMPLE_ID)
      .name("test name")
      .description("Test descripcion")
      .creationTime(TEST_OFFSET_DATE_TIME)
      .identificationType(IDENTIFICATION_TYPE)
      .identification(IDENTIFICATION)
      .numberOfDaysInWeek(NUMBER_OF_DAYS_IN_WEEK)
      .build();

  }

  private ExampleRequest getTestExampleRequest() {

    return ExampleRequest.builder()
      .name("test name")
      .description("Test descripcion")
      .creationTime(TEST_OFFSET_DATE_TIME)
      .identification("Y1111111F")
      .identificationType(IDENTIFICATION_TYPE_ENUM_NIE)
      .numberOfDaysInWeek(7)
      .build();

  }

  private ExampleResourceResponse getTestExampleResourceResponse() {

    return ExampleResourceResponse.builder()
      .data(getTestExampleResource())
      .build();

  }

  private ExampleResourceCollectionResponse getTestExampleResourceCollectionResponse() {

    return ExampleResourceCollectionResponse.builder()
      .data(
        ExampleResourceCollection
          .builder()
          .examples(Collections.singletonList(getTestExampleResource()))
          .build())
      .build();

  }

}

