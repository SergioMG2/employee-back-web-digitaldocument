# Documentación del microservicio employee-back-web-digitaldocument

Coloca aquí los documentos de análisis y diseño antes de generar el código.

## Carpetas

- **contratos/**: Ficheros OpenAPI/Swagger, contratos de API REST (yaml, json)
- **modelos/**: Modelo de datos, diagramas ER, scripts SQL de las tablas
- **eventos/**: Schemas Avro, definición de tópicos Kafka (nombre, dirección, grupo)
- **casos-de-uso/**: Descripción funcional de los casos de uso y reglas de negocio
- **integraciones/**: APIs externas consumidas, URLs base, tipo de autenticación

## Uso

Una vez completada la documentación, ejecuta la skill `cna-develop-from-docs`
desde el directorio raíz del proyecto para generar automáticamente el código
completo siguiendo la arquitectura hexagonal de Mercadona CNA.
