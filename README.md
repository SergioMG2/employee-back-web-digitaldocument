# employee-back-web-digitaldocument

Microservicio de generación de documentos digitales de certificación de IA para empleados.

---

## Requisitos previos

- Java 21
- Maven 3.8+
- Docker + Docker Compose

---

## 1. Levantar la infraestructura local

Desde la carpeta `devops/docker`:

```bash
cd devops/docker
docker-compose up -d
```

Servicios que se levantan:

| Servicio         | URL local                     | Credenciales              |
|------------------|-------------------------------|---------------------------|
| PostgreSQL       | `localhost:5433`              | sa / root · BD: `prueba`  |
| Kafka            | `localhost:29092`             | sin auth                  |
| Schema Registry  | `localhost:8085`              | sin auth                  |
| AKHQ (Kafka UI)  | http://localhost:8090         | —                         |
| MinIO (bucket)   | http://localhost:9001 (UI)    | minioadmin / minioadmin   |
| Card Generator   | http://localhost:8081         | —                         |

---

## 2. Crear el esquema de base de datos

Conecta a PostgreSQL en `localhost:5433`, base de datos `prueba`, con credenciales `sa` / `root` y ejecuta los siguientes scripts en orden:

**V1 — Tabla de documentos digitales**
```
driven/postgres-repository/sql/migration/versions/1.0.0-create-digital-documents/V1.0.0__create-digital-documents.sql
```

**V2 — Tabla outbox**
```
driven/postgres-repository/sql/migration/versions/2.0.0-create-outbox-tables/V2.0.0__create-outbox-tables.sql
```

> Si la aplicación arranca correctamente con Flyway habilitado (`spring.flyway.enabled: true` en `application-local.yml`), estos scripts se ejecutan automáticamente al arrancar. Ejecutarlos a mano solo es necesario si Flyway no los aplica.

---

## 3. Crear el tópico Kafka en AKHQ

1. Abre **http://localhost:8090**
2. Ve a **Topics** → **Create topic**
3. Configura:
   - **Name:** `thirdparty.employee.employee.event.public.v0.table.cpd`
   - **Partitions:** 1
   - **Replication factor:** 1
4. Pulsa **Create**

---

## 4. Registrar los schemas Avro en AKHQ

El consumidor Kafka usa deserialización Avro. Los schemas deben estar registrados en el Schema Registry antes de publicar mensajes.

### 3.1 Pasos en AKHQ

1. Abre **http://localhost:8090**
2. Ve a **Schema Registry** → **Create schema**
3. Crea los dos schemas siguientes (uno por uno)

---

### 3.2 Schema de la Key

**Subject:** `thirdparty.employee.employee.event.public.v0.table.cpd-key`
**Schema Type:** `AVRO`

```json
{
  "type": "record",
  "name": "EmployeeEventPublicKey",
  "namespace": "thirdparty.employee.employee",
  "fields": [
    {
      "name": "id",
      "type": { "type": "string", "avro.java.string": "String" }
    },
    {
      "name": "managedGroupId",
      "type": {
        "type": "record",
        "name": "ManagedGroupIds",
        "namespace": "thirdparty.employee.employee",
        "fields": [
          { "name": "id", "type": { "type": "string", "avro.java.string": "String" } }
        ]
      }
    }
  ]
}
```

---

### 3.3 Schema del Value

**Subject:** `thirdparty.employee.employee.event.public.v0.table.cpd-value`
**Schema Type:** `AVRO`

```json
{
  "type": "record",
  "name": "EmployeeEventPublicValue",
  "namespace": "thirdparty.employee.employee",
  "fields": [
    {
      "name": "eventType",
      "type": {
        "type": "enum",
        "name": "EventTypeV2",
        "namespace": "com.mercadona.commons.architecture",
        "doc": "Event type",
        "symbols": ["CREATE", "UPDATE", "UPSERT", "DELETE", "TIMER"],
        "default": "CREATE"
      }
    },
    {
      "name": "metadata",
      "doc": "Metadata information",
      "type": [
        "null",
        {
          "type": "array",
          "items": {
            "type": "record",
            "name": "MetadataEventV2",
            "namespace": "com.mercadona.commons.architecture",
            "doc": "Metadata event information",
            "fields": [
              {
                "name": "dateTimeCreated",
                "type": { "type": "string", "avro.java.string": "String" },
                "doc": "Date and time created"
              },
              {
                "name": "producerApp",
                "type": ["null", { "type": "string", "avro.java.string": "String" }],
                "doc": "Producer application",
                "default": null
              },
              {
                "name": "eventId",
                "type": ["null", { "type": "string", "avro.java.string": "String" }],
                "doc": "Event identifier",
                "default": null
              }
            ]
          }
        }
      ],
      "default": null
    },
    {
      "name": "payload",
      "type": {
        "type": "record",
        "name": "Employee",
        "namespace": "thirdparty.employee.employee",
        "fields": [
          { "name": "id", "type": { "type": "string", "avro.java.string": "String" } },
          { "name": "managedGroupId", "type": "thirdparty.employee.employee.ManagedGroupIds" },
          { "name": "name", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null },
          { "name": "firstSurname", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null },
          { "name": "secondSurname", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null },
          { "name": "favouriteName", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null },
          { "name": "isActive", "type": ["null", "boolean"], "default": null },
          {
            "name": "professionalData",
            "type": [
              "null",
              {
                "type": "record",
                "name": "EmployeeProfessionalData",
                "fields": [
                  {
                    "name": "accessData",
                    "type": [
                      "null",
                      {
                        "type": "record",
                        "name": "EmployeeAccessData",
                        "fields": [
                          {
                            "name": "user",
                            "type": [
                              "null",
                              {
                                "type": "record",
                                "name": "UserIds",
                                "fields": [
                                  { "name": "id", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null }
                                ]
                              }
                            ],
                            "default": null
                          }
                        ]
                      }
                    ],
                    "default": null
                  },
                  {
                    "name": "professionalEmail",
                    "type": [
                      "null",
                      {
                        "type": "record",
                        "name": "EmailAddress",
                        "fields": [
                          { "name": "emailAddress", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null }
                        ]
                      }
                    ],
                    "default": null
                  },
                  {
                    "name": "professionalPhones",
                    "type": [
                      "null",
                      {
                        "type": "array",
                        "items": {
                          "type": "record",
                          "name": "EmployeeProfessionalPhone",
                          "fields": [
                            { "name": "id", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null },
                            {
                              "name": "type",
                              "type": [
                                "null",
                                {
                                  "type": "record",
                                  "name": "EmployeePhoneType",
                                  "fields": [
                                    { "name": "id", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null },
                                    {
                                      "name": "names",
                                      "type": [
                                        "null",
                                        {
                                          "type": "array",
                                          "items": {
                                            "type": "record",
                                            "name": "Names",
                                            "fields": [
                                              {
                                                "name": "nameLocale",
                                                "type": [
                                                  "null",
                                                  {
                                                    "type": "record",
                                                    "name": "LocaleLanguageIds",
                                                    "fields": [
                                                      { "name": "code", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null }
                                                    ]
                                                  }
                                                ],
                                                "default": null
                                              },
                                              { "name": "name", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null }
                                            ]
                                          }
                                        }
                                      ],
                                      "default": null
                                    }
                                  ]
                                }
                              ],
                              "default": null
                            },
                            {
                              "name": "countryPhonePrefix",
                              "type": [
                                "null",
                                {
                                  "type": "record",
                                  "name": "CountryPhonePrefix",
                                  "fields": [
                                    { "name": "code", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null }
                                  ]
                                }
                              ],
                              "default": null
                            },
                            { "name": "phoneNumber", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null },
                            { "name": "extension", "type": ["null", { "type": "string", "avro.java.string": "String" }], "default": null }
                          ]
                        }
                      }
                    ],
                    "default": null
                  }
                ]
              }
            ],
            "default": null
          }
        ]
      }
    }
  ]
}
```

---

## 5. Crear el bucket en MinIO

1. Abre **http://localhost:9001**
2. Login: `minioadmin` / `minioadmin`
3. Ve a **Buckets** → **Create Bucket**
4. **Bucket Name:** `digital-documents`
5. Pulsa **Create Bucket**

---

## 6. Compilar y arrancar la aplicación

```bash
mvn clean install -DskipTests
mvn spring-boot:run -pl boot -Dspring-boot.run.profiles=local
```

La aplicación arranca en **http://localhost:8080**.

Flyway ejecuta automáticamente las migraciones al arrancar:
- `V1.0.0` — tabla `o_digital_documents`
- `V2.0.0` — tabla `o_outbox` + secuencia `o_outbox_id_seq`

---

## 7. Probar el flujo completo

### 5.1 Publicar un evento en AKHQ

1. Abre **http://localhost:8090** → **Topics** → `thirdparty.employee.employee.event.public.v0.table.cpd`
2. Ve a la pestaña **Produce**
3. Publica un mensaje con el siguiente formato (ajusta `employeeId` y `managedGroupId`):

**Key (JSON):**
```json
{
  "id": "2115279",
  "managedGroupId": { "id": "81" }
}
```

**Value (JSON):**
```json
{
  "eventType": "CREATE",
  "metadata": [{ "dateTimeCreated": "30/06/2026 12:15:26", "producerApp": null, "eventId": null }],
  "payload": {
    "id": "2115279",
    "managedGroupId": { "id": "81" },
    "name": "NOMBRE",
    "firstSurname": "APELLIDO1",
    "secondSurname": "APELLIDO2",
    "favouriteName": "NOMBRE APELLIDO1",
    "isActive": true,
    "professionalData": {
      "accessData": { "user": { "id": "usuario" } },
      "professionalEmail": { "emailAddress": "usuario@ejemplo.com" },
      "professionalPhones": []
    }
  }
}
```

El microservicio consumirá el evento y ejecutará el flujo completo:
`PENDING → ENRICHED → PDF_GENERATED → STORED`

Al finalizar, se insertará un registro en `o_outbox` con el payload JSON listo para que el segundo microservicio publique en Kafka.

### 5.2 Consultar el documento generado

Importa la colección Postman desde `driving/api-rest/postman/` y selecciona el entorno **Digital Document - Local**.

| Endpoint | Descripción |
|---|---|
| `GET /digital-documents/employees/{employeeId}` | PDF del documento (inline) |
| `GET /digital-documents/{documentId}` | PDF por UUID |
| `GET /digital-documents/{documentId}/status` | Estado actual + failedStep |

---

## 8. Configuración local resumida

| Propiedad | Valor |
|---|---|
| Base de datos | `jdbc:postgresql://localhost:5433/prueba` |
| Kafka bootstrap | `localhost:29092` |
| Schema Registry | `http://localhost:8085` |
| Bucket MinIO | `http://localhost:9000` · bucket: `digital-documents` |
| API enriquecimiento | `http://localhost:8081` |
| Swagger Card Generator | http://localhost:8081/swagger-ui/index.html |

---

## 9. Arquitectura

```
[Kafka: employee] → EmployeeEventConsumerAdapter
                          ↓
                   DigitalDocumentConsumerUseCase
                     ├── Enriquecer (Card Generator API)
                     ├── Generar PDF (iText 7)
                     ├── Subir a MinIO
                     └── Escribir en o_outbox (JSON)
                          ↓
                   [Segundo micro OBX publica en Kafka: employee-digital-document]

[REST API] → DigitalDocumentControllerAdapter
                ├── GET /digital-documents/employees/{employeeId} → PDF
                ├── GET /digital-documents/{documentId} → PDF
                └── GET /digital-documents/{documentId}/status → JSON
```
