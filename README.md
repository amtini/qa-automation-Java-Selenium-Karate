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

- Java JDK 17 or higher
- Apache Maven 3.9.x
- Chrome or Firefox installed locally (Selenium 4 auto-resolves driver binaries)

## Installation (Windows)

This guide covers Windows 10/11 only. On macOS and Linux the installation steps differ (Homebrew/apt for installing, `~/.zshrc` or `~/.bashrc` for configuring PATH and `JAVA_HOME`) and are **not documented here**. The goal is the same on any OS: `java -version` and `mvn -version` responding with the correct versions in the terminal.

1. **Java JDK 17** — [Eclipse Temurin recommended](https://adoptium.net/temurin/releases/?version=17). Download the MSI and run it. During the wizard, leave the options *"Set JAVA_HOME variable"* and *"Add to PATH"* checked (they are by default). The installer handles all the env vars for you.

2. **Apache Maven 3.9.x** — Download the *"Binary zip archive"* from [maven.apache.org/download.cgi](https://maven.apache.org/download.cgi) (NOT the source or the preview 4.x). Extract the zip into your user folder so it ends up at `C:\Users\<your-user>\apache-maven-3.9.X\`.

3. **Add Maven to your User PATH** — pick one:
   - PowerShell (one-liner):
     ```powershell
     [Environment]::SetEnvironmentVariable("Path", [Environment]::GetEnvironmentVariable("Path", "User") + ";$env:USERPROFILE\apache-maven-3.9.X\bin", "User")
     ```
   - UI: `Win + R` → `sysdm.cpl` → Advanced → Environment Variables → under User variables edit `Path` → New → paste the path to `bin\`.

4. **Close VSCode (or your IDE/terminal) entirely and reopen.** If it was open during the previous steps, it won't see the new PATH or JAVA_HOME until restart.

5. **Verify** in a fresh terminal:
   ```bash
   java -version    # should report Temurin 17.x.x
   mvn -version     # should report Maven 3.9.X recognizing the JDK 17
   ```

If both respond correctly, you're ready to run the tests. The first `mvn test` may take a few minutes downloading dependencies, and Windows SmartScreen may prompt you the first time Chrome starts (it's Selenium Manager downloading the chromedriver — accept it, it gets cached afterward).

For detailed per-exercise instructions in Spanish, see `e2e/readme.txt` and `api/readme.txt`.

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

Per-exercise deliverables:

- `e2e/readme.txt`: step-by-step execution instructions for the SauceDemo E2E suite
- `e2e/conclusiones.txt`: findings and design decisions for the E2E suite
- `api/readme.txt`: step-by-step execution instructions for the PetStore API suite
- `api/conclusiones.txt`: findings and design decisions for the API suite
