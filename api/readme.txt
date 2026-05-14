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

- Java JDK 17 o superior
- Apache Maven 3.9.x
- Conexion a internet para alcanzar https://petstore.swagger.io

Pasos de instalacion en la seccion siguiente (asumen Windows 10/11).

---------------------
INSTALACION (Windows)
---------------------

Si ya hiciste el setup para el modulo E2E (ver e2e/readme.txt), saltate
esta seccion: es exactamente el mismo Java 17 + Maven 3.9.x, no hace
falta instalarlos de nuevo.

Esta guia cubre Windows 10/11 unicamente. En Mac y Linux los pasos
de instalacion son distintos (brew/apt para instalar, ~/.zshrc o
~/.bashrc para configurar PATH y JAVA_HOME) y no estan documentados
aca. El objetivo es el mismo en cualquier OS: que java -version y
mvn -version respondan en la terminal con las versiones correctas.

1. Java JDK 17 (Eclipse Temurin recomendado):
       https://adoptium.net/temurin/releases/?version=17
   Bajar el MSI y correrlo. Durante el wizard, dejar marcadas las
   opciones "Set JAVA_HOME variable" y "Add to PATH" (vienen activas
   por default). Asi el instalador hace el laburo de env vars solo
   y no hay que tocar nada a mano.

2. Apache Maven 3.9.x:
       https://maven.apache.org/download.cgi
   Bajar el "Binary zip archive" (NO bajar el Source ni el Preview
   4.x, son cosas distintas). Extraer el zip en tu carpeta de
   usuario, queda:
       C:\Users\<tu-usuario>\apache-maven-3.9.X\

3. Agregar Maven al PATH del usuario (eleji una):
   - PowerShell, una linea:
       [Environment]::SetEnvironmentVariable("Path", [Environment]::GetEnvironmentVariable("Path", "User") + ";$env:USERPROFILE\apache-maven-3.9.X\bin", "User")
     (Ajustar 3.9.X a la version que descargaste)
   - UI:
       Win + R -> sysdm.cpl -> "Opciones avanzadas" ->
       "Variables de entorno..." -> bajo "Variables de usuario"
       editar Path -> "Nuevo" -> pegar la ruta a bin\.

4. Cerrar VSCode (o tu IDE/terminal) entero y reabrirlo. Si estaba
   abierto durante los pasos anteriores, no ve los cambios de PATH
   ni JAVA_HOME hasta que reinicia.

5. Verificar en una terminal nueva:
       java -version       (deberia decir Temurin 17.x.x)
       mvn -version        (deberia decir Maven 3.9.X reconociendo el JDK 17)

Si los 3 responden bien, todo listo para correr los tests.

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
