package com.mercadona.employee.digitaldocument.domain;

import lombok.Getter;

@Getter
public enum IdentificationTypesEnum {

  DNI("DNI"),
  NIE("NIE");

  private final String value;

  IdentificationTypesEnum(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(this.value);
  }

}
