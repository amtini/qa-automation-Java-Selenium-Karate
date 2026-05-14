-----------
DESCRIPCION
-----------

Test E2E del flujo de compra en SauceDemo (https://www.saucedemo.com) con
Selenium WebDriver 4, JUnit 5 y Page Object Model. Java 17, Maven.

El test cubre:
- Login con standard_user / secret_sauce.
- Agregar dos productos al carrito (uno desde la lista de inventario y
  otro desde la pagina de detalle).
- Validar contenido del carrito (nombre, precio, descripcion, subtotal).
- Completar el formulario de checkout con datos generados (DataFaker).
- Validar el checkout step 2 (items, subtotal, payment info, shipping info).
- Finalizar la compra y verificar el mensaje de confirmacion.

----------
REQUISITOS
----------

- Java JDK 17 o superior
- Apache Maven 3.9.x
- Chrome o Firefox instalado localmente

Pasos de instalacion en la seccion siguiente (asumen Windows 10/11).
Selenium 4 resuelve los drivers automaticamente via Selenium Manager,
asi que no hace falta instalar chromedriver ni geckodriver.

---------------------
INSTALACION (Windows)
---------------------

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

    mvn test -Dtest=CheckoutTest

Parametros opcionales:

    -Dbrowser=chrome | firefox               (default: chrome)
    -Dheadless=true | false                  (default: false)
    -DbaseUrl=<url>                          (default: saucedemo.com)
    -Denv=dev | staging | mock | prod        (default: dev)

Ejemplo combinando flags:

    mvn test -Dtest=CheckoutTest -Dbrowser=firefox -Dheadless=true

La primera ejecucion tarda mas porque descarga las dependencias al
repositorio local de Maven. Las siguientes son inmediatas.

--------
REPORTES
--------

Despues de correr los tests, los reportes quedan en target/:

- target/test-results.json
    JSON custom con totales (passed / bugs / failed / skipped) y detalle
    por test: status, duracion, motivo de la falla, ubicacion en el codigo
    y path al screenshot si fallo. Pensado para alimentar trackers como
    Jira o Linear, o dashboards propios.

- target/surefire-reports/
    Reportes nativos de JUnit 5/Surefire (HTML y .txt).

- screenshots/
    Screenshots automaticos cuando un test falla. Naming:
    {nombreDelTest}-{yyyyMMdd-HHmmss}.png

---------------
TROUBLESHOOTING
---------------

- "mvn" no se reconoce: Maven no esta en el PATH. Abrir una terminal nueva
  despues de instalarlo.
- Tests lentos o timeouts: aumentar explicitWaitSeconds en
  src/test/resources/config.properties (default es 10 segundos).
- Browser no abre: actualizar Chrome o Firefox a la ultima version.
- Windows te pide permisos de admin o te muestra una pantalla de
  SmartScreen la primera vez que arranca Chrome: es Selenium Manager
  descargando el chromedriver para tu version de Chrome. Aceptalo;
  despues queda cacheado en %USERPROFILE%\.cache\selenium y no molesta mas.
- Edge no esta soportado. Microsoft retiro el CDN azureedge.net donde
  Selenium 4.16.1 busca el msedgedriver, asi que -Dbrowser=edge falla
  con un DNS error. Para sumarlo habria que subir selenium.version en
  pom.xml a >= 4.21, cosa que quedo fuera de scope. Usar Chrome o Firefox.
