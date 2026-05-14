-----------
DESCRIPCION
-----------

Test del flujo CRUD sobre el recurso /pet de la API publica de PetStore
(https://petstore.swagger.io) usando Karate, con JUnit 5 como runner.
Java 17, Maven.

El test cubre las 4 operaciones encadenadas sobre una misma mascota:

- POST /pet: agregar una mascota a la tienda con status "available".
- GET /pet/{id}: consultar la mascota recien creada usando su ID.
- PUT /pet: actualizar el nombre y cambiar el status a "sold".
- GET /pet/findByStatus?status=sold: buscar la mascota actualizada por
  su status y validar que aparezca con el name modificado.

El petId se genera aleatoriamente en cada corrida para evitar colisiones
con otros usuarios del sandbox publico de PetStore.

----------
REQUISITOS
----------

- Java JDK 17 o superior (JAVA_HOME apuntando al JDK, no al JRE).
  Recomendado: Eclipse Temurin 17
  (https://adoptium.net/temurin/releases/?version=17).
  Verificar con:
      java -version
- Apache Maven 3.9.x (https://maven.apache.org/download.cgi).
  Verificar con:
      mvn -version
- Conexion a internet para alcanzar https://petstore.swagger.io

---------
EJECUCION
---------

Desde la raiz del proyecto:

    mvn test -Dtest=PetRunner

O bien, correr el suite completo (incluye tambien el E2E con Selenium):

    mvn test

Con env switching:

    mvn test -Dtest=PetRunner -Dkarate.env=staging

Valores soportados: dev (default), staging, mock, prod.

La primera ejecucion tarda mas porque descarga las dependencias al
repositorio local de Maven. Las siguientes son inmediatas.

--------
REPORTES
--------

Despues de correr los tests, los reportes quedan en target/:

- target/karate-reports/karate-summary.html
    Reporte HTML nativo de Karate. Tiene el scenario tree, request y
    response completos de cada step, los asserts validados y los tiempos.
    Abrirlo en cualquier browser para inspeccionar el flujo paso a paso.
    En la misma carpeta queda karate-summary.json con la misma info en
    formato JSON, util para integraciones.

- target/test-results.json
    JSON del modulo E2E (escrito por el TestListener). Si corres solo
    PetRunner no se actualiza, porque el listener engancha en @Test de
    JUnit y no en @Karate.Test. Para el resultado del API mirar el
    karate-summary.html o el .json de arriba.

- target/surefire-reports/
    Reportes nativos de Maven Surefire (XML y .txt).

---------------
TROUBLESHOOTING
---------------

- "mvn" no se reconoce: Maven no esta en el PATH. Abrir una terminal nueva
  despues de instalarlo.
- Connection refused / timeout al alcanzar petstore.swagger.io: la API
  publica puede estar caida o saturada. Verificar con:
      curl "https://petstore.swagger.io/v2/pet/findByStatus?status=available"
- El findByStatus a veces no refleja inmediatamente los cambios hechos
  por el PUT (eventual consistency del sandbox publico). Reintentar.
