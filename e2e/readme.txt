-----------
DESCRIPTION
-----------

E2E test of the checkout flow on SauceDemo (https://www.saucedemo.com)
with Selenium WebDriver 4, JUnit 5 and Page Object Model. Java 17, Maven.

The test covers:
- Login with standard_user / secret_sauce.
- Adding two products to the cart (one from the inventory list and
  another from the detail page).
- Validating the cart contents (name, price, description, subtotal).
- Filling out the checkout form with generated data (DataFaker).
- Validating checkout step 2 (items, subtotal, payment info, shipping info).
- Completing the purchase and verifying the confirmation message.

------------
REQUIREMENTS
------------

- Java JDK 17 or higher
- Apache Maven 3.9.x
- Chrome or Firefox installed locally

Installation steps in the next section (they assume Windows 10/11).
Selenium 4 resolves the drivers automatically via Selenium Manager, so
there is no need to install chromedriver or geckodriver.

----------------------
INSTALLATION (Windows)
----------------------

This guide covers Windows 10/11 only. On Mac and Linux the installation
steps are different (brew/apt to install, ~/.zshrc or ~/.bashrc to
configure PATH and JAVA_HOME) and are not documented here. The goal is
the same on any OS: that java -version and mvn -version respond in the
terminal with the correct versions.

1. Java JDK 17 (Eclipse Temurin recommended):
       https://adoptium.net/temurin/releases/?version=17
   Download the MSI and run it. During the wizard, keep the options
   "Set JAVA_HOME variable" and "Add to PATH" checked (they are active
   by default). This way the installer handles the env vars on its own
   and you don't have to touch anything by hand.

2. Apache Maven 3.9.x:
       https://maven.apache.org/download.cgi
   Download the "Binary zip archive" (do NOT download the Source or the
   Preview 4.x, those are different things). Extract the zip into your
   user folder, it ends up at:
       C:\Users\<your-user>\apache-maven-3.9.X\

3. Add Maven to the user PATH (choose one):
   - PowerShell, one line:
       [Environment]::SetEnvironmentVariable("Path", [Environment]::GetEnvironmentVariable("Path", "User") + ";$env:USERPROFILE\apache-maven-3.9.X\bin", "User")
     (Adjust 3.9.X to the version you downloaded)
   - UI:
       Win + R -> sysdm.cpl -> "Advanced" ->
       "Environment Variables..." -> under "User variables"
       edit Path -> "New" -> paste the path to bin\.

4. Close VSCode (or your IDE/terminal) completely and reopen it. If it
   was open during the previous steps, it doesn't see the PATH or
   JAVA_HOME changes until it restarts.

5. Verify in a new terminal:
       java -version       (should say Temurin 17.x.x)
       mvn -version        (should say Maven 3.9.X recognizing the JDK 17)

If all 3 respond correctly, everything is ready to run the tests.

---------
EXECUTION
---------

From the project root:

    mvn test -Dtest=CheckoutTest

Optional parameters:

    -Dbrowser=chrome | firefox               (default: chrome)
    -Dheadless=true | false                  (default: false)
    -DbaseUrl=<url>                          (default: saucedemo.com)
    -Denv=dev | staging | mock | prod        (default: dev)

Example combining flags:

    mvn test -Dtest=CheckoutTest -Dbrowser=firefox -Dheadless=true

The first execution takes longer because it downloads the dependencies
to the local Maven repository. The following ones are immediate.

-------
REPORTS
-------

After running the tests, the reports are left in target/:

- target/test-results.json
    Custom JSON with totals (passed / bugs / failed / skipped) and detail
    per test: status, duration, failure reason, location in the code and
    path to the screenshot if it failed. Designed to feed trackers like
    Jira or Linear, or custom dashboards.

- target/surefire-reports/
    Native JUnit 5/Surefire reports (HTML and .txt).

- screenshots/
    Automatic screenshots when a test fails. Naming:
    {testName}-{yyyyMMdd-HHmmss}.png

---------------
TROUBLESHOOTING
---------------

- "mvn" not recognized: Maven is not in the PATH. Open a new terminal
  after installing it.
- Slow tests or timeouts: increase explicitWaitSeconds in
  src/test/resources/config.properties (default is 10 seconds).
- Browser doesn't open: update Chrome or Firefox to the latest version.
- Windows asks you for admin permissions or shows you a SmartScreen
  prompt the first time Chrome starts: it's Selenium Manager downloading
  the chromedriver for your version of Chrome. Accept it; afterwards it
  stays cached in %USERPROFILE%\.cache\selenium and doesn't bother you
  anymore.
- Edge is not supported. Microsoft retired the azureedge.net CDN where
  Selenium 4.16.1 looks for the msedgedriver, so -Dbrowser=edge fails
  with a DNS error. To add it, you would have to bump selenium.version
  in pom.xml to >= 4.21, which was left out of scope. Use Chrome or
  Firefox.
