package com.mercadona.employee.digitaldocument.driving.controllers.adapters;

import jakarta.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import lombok.AllArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import com.mercadona.employee.digitaldocument.driving.controllers.mappers.ExampleDTOMapper;
import com.mercadona.employee.digitaldocument.application.ports.driving.ExampleServicePort;

import com.mercadona.framework.cna.api.demotest.arq.web.definition.server.ExamplesApi;
import com.mercadona.framework.cna.api.demotest.arq.web.model.ExampleRequest;
import com.mercadona.framework.cna.api.demotest.arq.web.model.ExampleResourceCollectionResponse;
import com.mercadona.framework.cna.api.demotest.arq.web.model.ExampleResourceResponse;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping
public class ExampleControllerAdapter implements ExamplesApi {

  private final ExampleServicePort exampleService;
  private final ExampleDTOMapper exampleDTOMapper;

  @Override
  public ResponseEntity<ExampleResourceCollectionResponse> getExampleCollection(Integer firstPage, Integer pageSize, String sort) {

    var exampleEntities = exampleService.getAllExamples(firstPage, pageSize, sort);

    var exampleResourceCollection = exampleDTOMapper.toExampleResourceCollectionResponse(exampleEntities);

    return ResponseEntity.ok(exampleResourceCollection);

  }

  @Override
  public ResponseEntity<ExampleResourceResponse> getExample(Long id) {

    var example = exampleService.getExample(id);

    if(example.isPresent()) {
        var exampleResourceResponse = exampleDTOMapper.toExampleResourceResponse(example.get());

        return ResponseEntity.ok(exampleResourceResponse);

    }else {
    	return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

  }

  @Override
  public ResponseEntity<ExampleResourceResponse> createExample(@Valid @RequestBody ExampleRequest exampleRequest) {

    var exampleEntity = exampleDTOMapper.from(exampleRequest);

    var exampleSaved = exampleService.createExample(exampleEntity);

    var exampleResourceResponse = exampleDTOMapper.toExampleResourceResponse(exampleSaved);

    return ResponseEntity.status(HttpStatus.CREATED).body(exampleResourceResponse);

  }

  @Override
  public ResponseEntity<ExampleResourceResponse> updateExample(Long id, @Valid ExampleRequest exampleRequest) {

    var exampleEntity = exampleDTOMapper.from(exampleRequest);

    var updatedExample = exampleService.updateExample(id, exampleEntity);

    var exampleResourceResponse = exampleDTOMapper.toExampleResourceResponse(updatedExample);

    return ResponseEntity.ok(exampleResourceResponse);

  }

  @Override
  public ResponseEntity<Void> deleteExample(Long id) {

    exampleService.deleteExample(id);

    return ResponseEntity.noContent().build();

  }

}
