/*==============================================================*/
/* Table: O_DIGITAL_DOCUMENTS                                   */
/*==============================================================*/

CREATE TABLE o_digital_documents (
    id               BIGSERIAL    NOT NULL,
    document_id      VARCHAR(36)  NOT NULL,
    employee_id      VARCHAR(255) NOT NULL,
    managed_group_id VARCHAR(255) NOT NULL,
    status           VARCHAR(50)  NOT NULL,
    failed_step      VARCHAR(50)  NULL,
    bucket_path      VARCHAR(500) NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP    NOT NULL DEFAULT NOW(),
    CONSTRAINT o_digital_documents_pkey             PRIMARY KEY (id),
    CONSTRAINT uq_digital_documents_document_id     UNIQUE (document_id),
    CONSTRAINT uq_digital_documents_employee        UNIQUE (employee_id, managed_group_id)
);
