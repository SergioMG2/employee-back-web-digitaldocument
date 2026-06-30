package com.mercadona.employee.digitaldocument.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Example {

  private Long id;
  private String name;
  private String description;
  private OffsetDateTime creationTime;
  private IdentificationTypesEnum identificationType;
  private String identification;
  private Integer numberOfDaysInWeek;

}
