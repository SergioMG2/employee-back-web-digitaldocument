package com.mercadona.employee.digitaldocument.driven.repositories.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "o_outbox")
public class OutboxMO {

    @Id
    @SequenceGenerator(name = "outbox_seq", sequenceName = "o_outbox_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "outbox_seq")
    @Column(name = "cod_n_idoutbox")
    private Long id;

    @Column(name = "aggregateid")
    private byte[] aggregateId;

    @Column(name = "aggregatetype")
    private String aggregateType;

    @Column(name = "payload")
    private byte[] payload;

    @Column(name = "fec_dt_creacion")
    private LocalDateTime creationDate;

    @Column(name = "headers")
    private String headers;
}
