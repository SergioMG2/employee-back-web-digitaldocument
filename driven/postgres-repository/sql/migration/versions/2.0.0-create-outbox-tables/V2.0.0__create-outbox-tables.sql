/*==============================================================*/
/* Table: O_OUTBOX                                              */
/* DDL estándar requerido por fwkcna-starter-outbox-avro-jpa    */
/*==============================================================*/

CREATE TABLE o_outbox (
    cod_n_idoutbox  BIGINT       NOT NULL,
    aggregateid     BYTEA        NULL,
    aggregatetype   VARCHAR(255) NULL,
    payload         BYTEA        NULL,
    fec_dt_creacion TIMESTAMP    NULL,
    headers         TEXT         NULL,
    CONSTRAINT o_outbox_pkey PRIMARY KEY (cod_n_idoutbox)
);

CREATE SEQUENCE o_outbox_id_seq START WITH 1 INCREMENT BY 1 CACHE 1 CYCLE;

