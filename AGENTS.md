# Repository Guidelines

## Project Structure & Module Organization
The project is a Gradle multi-module Spring Boot suite. Shared utilities live in `common/snowflake/src/main/java`, service-specific code sits under `service/<domain>/src/main/java/gc/board/<domain>/`, with configuration in `src/main/resources/application.yml`. Integration and unit tests mirror the main source tree in `src/test/java`. Generated build outputs land in each module’s `build/` directory; do not edit anything inside `build/`.

## Build, Test, and Development Commands
- `./gradlew clean build` re-compiles every module, runs all unit/integration tests, and produces bootable JARs.
- `./gradlew :service:article:bootRun` starts an individual service locally using the module’s `application.yml`; swap the scope (`article`, `comment`, etc.) as needed.
- `./gradlew test --info` executes the full test suite with verbose logging—use this when chasing flaky behavior.

## Coding Style & Naming Conventions
Code is written in Java 21 with Lombok; rely on 4-space indentation and UTF-8 source files. Follow the `gc.board.<domain>` package layout, use `UpperCamelCase` for types, and `lowerCamelCase` for methods, fields, and request/response DTOs. HTTP controllers should stay thin: delegate business logic to `service` classes and use `repository` interfaces for persistence. Keep configuration and constants in `application.yml` or dedicated `*Config` classes instead of scattering magic numbers.

## Testing Guidelines
Spring Boot’s starter test stack and JUnit 5 are already configured via `./gradlew test`. Mirror production packages, naming classes `*Test`; data fixtures belong in `data/` helpers as shown in `service/article/src/test/java/gc/board/article/data/DataInitializer.java`. Target >80% line coverage for new features and ensure every REST path has at least one `MockMvc` or `WebTestClient` exercise. Use `./gradlew :service:comment:test` (replace scope as needed) to iterate on a single module.

## Commit & Pull Request Guidelines
No existing history defines a convention, so adopt `type(scope): imperative summary` (for example `feat(article): support paged listings`). Keep the subject ≤72 chars and expand on motivation in the body. Pull requests should link the tracking issue, summarize behavioral changes, list affected modules, and attach screenshots or curl samples for API-facing work. Include test evidence (`./gradlew test` output snippet) before requesting review.

## Configuration & Security Notes
Environment-specific settings belong in `application-<profile>.yml`; keep secrets out of version control and load them via externalized configuration. Review module-level `application.yml` defaults before enabling new endpoints to avoid accidental exposure.
