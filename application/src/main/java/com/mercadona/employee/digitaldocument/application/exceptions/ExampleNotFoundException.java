package com.mercadona.employee.digitaldocument.application.exceptions;

import com.mercadona.framework.cna.commons.domain.MercadonaBusinessException;

public class ExampleNotFoundException extends MercadonaBusinessException {

	public ExampleNotFoundException (String message, String errorCode) {

		super(message, errorCode);

	}

}
