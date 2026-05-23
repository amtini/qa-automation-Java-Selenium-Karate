-----------
DESCRIPTION
-----------

Test of the CRUD flow over the /pet resource of the public PetStore API
(https://petstore.swagger.io) using Karate, with JUnit 5 as the runner.
Java 17, Maven.

The test covers the 4 chained operations over the same pet:

- POST /pet: add a pet to the store with status "available".
- GET /pet/{id}: query the just-created pet using its ID.
- PUT /pet: update the name and change the status to "sold".
- GET /pet/findByStatus?status=sold: search for the updated pet by its
  status and validate that it appears with the modified name.

The petId is generated randomly on each run to avoid collisions with
other users of the public PetStore sandbox.

------------
REQUIREMENTS
------------

- Java JDK 17 or higher
- Apache Maven 3.9.x
- Internet connection to reach https://petstore.swagger.io

Installation steps in the next section (they assume Windows 10/11).

----------------------
INSTALLATION (Windows)
----------------------

If you already did the setup for the E2E module (see e2e/readme.txt),
skip this section: it's exactly the same Java 17 + Maven 3.9.x, there's
no need to install them again.

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

    mvn test -Dtest=PetRunner

Or, run the full suite (also includes the E2E with Selenium):

    mvn test

With env switching:

    mvn test -Dtest=PetRunner -Dkarate.env=staging

Supported values: dev (default), staging, mock, prod.

The first execution takes longer because it downloads the dependencies
to the local Maven repository. The following ones are immediate.

-------
REPORTS
-------

After running the tests, the reports are left in target/:

- target/karate-reports/karate-summary.html
    Native Karate HTML report. It has the scenario tree, full request and
    response of each step, the validated asserts and the timings. Open it
    in any browser to inspect the flow step by step. In the same folder
    there is karate-summary.json with the same info in JSON format,
    useful for integrations.

- target/test-results.json
    JSON from the E2E module (written by the TestListener). If you run
    only PetRunner it is not updated, because the listener hooks into
    JUnit's @Test and not into @Karate.Test. For the API result, look at
    the karate-summary.html or the .json above.

- target/surefire-reports/
    Native Maven Surefire reports (XML and .txt).

---------------
TROUBLESHOOTING
---------------

- "mvn" not recognized: Maven is not in the PATH. Open a new terminal
  after installing it.
- Connection refused / timeout reaching petstore.swagger.io: the public
  API may be down or saturated. Verify with:
      curl "https://petstore.swagger.io/v2/pet/findByStatus?status=available"
- findByStatus sometimes doesn't immediately reflect the changes made by
  the PUT (eventual consistency of the public sandbox). Retry.
