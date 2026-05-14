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

- Java JDK 17 o superior (JAVA_HOME apuntando al JDK, no al JRE).
  Recomendado: Eclipse Temurin 17
  (https://adoptium.net/temurin/releases/?version=17).
  Verificar con:
      java -version
- Apache Maven 3.9.x (https://maven.apache.org/download.cgi).
  Verificar con:
      mvn -version
- Chrome, Firefox o Edge instalado localmente.

Selenium 4 resuelve los drivers automaticamente via Selenium Manager,
asi que no hace falta instalar chromedriver, geckodriver ni edgedriver.

---------
EJECUCION
---------

Desde la raiz del proyecto:

    mvn test -Dtest=CheckoutTest

Parametros opcionales:

    -Dbrowser=chrome | firefox | edge        (default: chrome)
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
- Browser no abre: actualizar Chrome/Firefox/Edge a la ultima version.
