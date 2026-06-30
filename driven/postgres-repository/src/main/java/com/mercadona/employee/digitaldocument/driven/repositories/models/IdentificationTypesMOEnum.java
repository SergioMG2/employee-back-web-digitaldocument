package com.mercadona.employee.digitaldocument.driven.repositories.models;

import lombok.Getter;

@Getter
public enum IdentificationTypesMOEnum {

  DNI("DNI"),
  NIE("NIE");

  private final String value;

  IdentificationTypesMOEnum(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    return String.valueOf(this.value);
  }

}
