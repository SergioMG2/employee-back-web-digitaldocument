package com.mercadona.employee.digitaldocument.driven.repositories.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "o_examples")
public class ExampleMO {

  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "creation_time")
  private OffsetDateTime creationTime;

  @Column(name = "identification_type")
  private IdentificationTypesMOEnum identificationType;

  @Column(name = "identification")
  private String identification;

  @Column(name = "days_in_week")
  private Integer numberOfDaysInWeek;
}
