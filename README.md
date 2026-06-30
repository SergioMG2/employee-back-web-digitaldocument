Framework Backend Arquetipo Web
===================================


```bash
mvn archetype:generate -DarchetypeGroupId=com.mercadona.framework.cna.archetype -DarchetypeArtifactId=fwkcna-archetype-web -DarchetypeVersion=5.0.0
```



Podemos ejecutar y ver en funcionamiento el ejemplo incorporado en la generación del proyecto.
Prerequsitos:

- Docker
- Postman
- Cliente de postgres (para poder ejecutar SQLs de creación de tablas)
  Nos posicionamos en el directorio `raiz` del proyecto generado, y realizamos los siguientes pasos:

1. Levantamos una bbdd en local utilizando `docker`, en la carpeta `docker`

   ```bash
    docker-compose up
    ```
2. Creamos las tablas de base de datos del ejemplo utilizando el script situado en la carpeta
   `driven/repository-jpa/sql/migration/versions.1.0.0-create-schema-examples/V1.0.0__create-schema-examples.sql`

3. Hacemos la instalación en local del proyecto

   ```bash
    mvn clean install
    ```

4. Ejecutamos la aplicación desde la raíz del proyecto con:

   ```bash
    mvn clean spring-boot:run -pl boot -Dspring-boot.run.profiles=local
    ```

   o navegando a la carpeta `boot` del proyecto:

    ```bash
    mvn clean spring-boot:run -Dspring-boot.run.profiles=local
    ```

6. Probamos la aplicación desde `postman`, importamos las collections de la carpeta `driving/api-rest/postman`
   
# Microservicio generado a partir de arquetipo

Toda la documentación relevante al desarrollo de este tipo de proyectos se encuentra en la guía del
desarrollador: [Guía del desarrollador](https://fwk.srv.mercadona.com/framework/spring-boot?pathname=/latest/getting-started/first-api-rest/)

Versión del arquetipo: `5.1.0`
