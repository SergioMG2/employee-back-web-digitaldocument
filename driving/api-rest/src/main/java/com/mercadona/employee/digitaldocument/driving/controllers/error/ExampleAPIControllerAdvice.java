package com.mercadona.employee.digitaldocument.driving.controllers.error;

import java.util.Arrays;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.mercadona.employee.digitaldocument.application.exceptions.ExampleNotFoundException;
import com.mercadona.framework.cna.commons.rest.api.model.ErrorResource;
import com.mercadona.framework.cna.commons.rest.api.model.ErrorResourceResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class ExampleAPIControllerAdvice {

    @ExceptionHandler({ExampleNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResourceResponse handleExampleNotFoundException(ExampleNotFoundException exception) {
        ErrorResource errorResource = ErrorResource.builder()
	            .code(exception.getErrorCode())
	            .description(exception.getMessage())
	            .details(Arrays.asList(exception.getMessage()))
	            .build();

        return new ErrorResourceResponse(errorResource);
    }
}
