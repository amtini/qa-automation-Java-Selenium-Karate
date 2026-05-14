# QA Automation Suite

End-to-end and API test automation built with Java 17, Selenium WebDriver, JUnit 5 and Karate.

## Project layout

```
src/test/java/
  e2e/         Selenium UI tests against https://www.saucedemo.com/
  api/         Karate API tests against https://petstore.swagger.io/
src/test/resources/
  config.properties
  karate-config.js
```

## Requirements

- Java JDK 17 or higher (JAVA_HOME must point to the JDK, not the JRE). Recommended: [Eclipse Temurin 17](https://adoptium.net/temurin/releases/?version=17). Verify with `java -version`.
- [Apache Maven 3.9.x](https://maven.apache.org/download.cgi). Verify with `mvn -version`.
- Chrome, Firefox or Edge installed locally (Selenium 4 auto-resolves driver binaries).

## Run

```bash
mvn test                              # everything (E2E + API)
mvn test -Dtest=CheckoutTest          # E2E only
mvn test -Dtest=PetRunner             # API only
mvn test -Dbrowser=firefox            # E2E in Firefox
mvn test -Dheadless=true              # headless mode
mvn test -Denv=staging                # E2E env switching
mvn test -Dkarate.env=staging         # API env switching
```

## Reports

After a run, reports are written to:

- `target/test-results.json`: custom JSON with PASSED / BUG / FAILED / SKIPPED status, durations, failure reasons and screenshot paths. Useful for feeding trackers like Jira or Linear, or custom dashboards.
- `target/surefire-reports/`: native JUnit 5 / Surefire output (XML, plain text).
- `target/karate-reports/karate-summary.html`: Karate HTML report for the API suite.
- `screenshots/`: PNG screenshots captured automatically on test failure.

## Configuration

Default values live in `src/test/resources/config.properties` (E2E) and `src/test/resources/karate-config.js` (API). Override via `-D` system properties at runtime, as shown in the Run section above. See `.env.example` for the full list of supported variables.

## See also

Per-exercise deliverables (Spanish):

- `e2e/readme.txt`: step-by-step execution instructions for the SauceDemo E2E suite
- `e2e/conclusiones.txt`: findings and design decisions for the E2E suite
- `api/readme.txt`: step-by-step execution instructions for the PetStore API suite
- `api/conclusiones.txt`: findings and design decisions for the API suite
