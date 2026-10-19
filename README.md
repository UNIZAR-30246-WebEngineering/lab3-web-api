# Lab 3 Web API

Individual starter for Web Engineering 2026–27. Stack matches the group project: **Java 25 LTS**, **Kotlin 2.4.0**, **Spring Boot 4.1.0**, **Gradle 9.6.0**.

The assignment, AI rules, and deadline are in [`docs/GUIDE.md`](docs/GUIDE.md). You may add an `AGENTS.md` or a project skill so an agent follows that guide (see AI use there). Fill [`REPORT.md`](REPORT.md) before you submit. Delivery is the Moodle zip only (`docs/GUIDE.md`).

## Run

Java 25 is required (`./gradlew` uses the wrapper). GitHub Codespaces is optional (`docs/GUIDE.md`). Clone this course repository; you do not fork it to submit.

```bash
git clone https://github.com/UNIZAR-30246-WebEngineering/lab3-web-api.git
cd lab3-web-api
./gradlew check
./gradlew bootRun
```

`./gradlew check` fails on this starter until the tests in `ControllerTests` are finished. The API listens on <http://localhost:8080>.

```bash
./gradlew test
./gradlew ktlintCheck
```

## Layout

```
src/main/kotlin/es/unizar/webeng/lab3/Application.kt
src/main/kotlin/es/unizar/webeng/lab3/Controller.kt
src/main/kotlin/es/unizar/webeng/lab3/Employee.kt
src/main/kotlin/es/unizar/webeng/lab3/EmployeeRepository.kt
src/test/kotlin/es/unizar/webeng/lab3/ControllerTests.kt
docs/GUIDE.md
```

The controller is already implemented. You complete the SETUP and VERIFY blocks in the tests.
