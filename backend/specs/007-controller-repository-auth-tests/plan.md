# Implementation Plan: Controller, Repository, Auth, and JSON Extraction Test Coverage

**Branch**: `007-controller-repository-auth-tests` | **Date**: 2026-10-06 | **Spec**: `backend/specs/007-controller-repository-auth-tests/spec.md`

**Input**: Feature specification from `/specs/007-controller-repository-auth-tests/spec.md`

## Summary

This feature focuses on proving the correct behavior of four backend boundaries without depending on real external scrapers or long-running jobs. The implementation strategy is to validate the controller at the HTTP boundary, the repository at the persistence boundary, the API-key filter at the security boundary, and the JSON extractor at the utility boundary. For the controller path, the expensive `actualizarJugadoresAsync()` flow is mocked so the E2E request test exercises the real web layer while avoiding a multi-hour scraper execution. All test names will be written in Spanish latinoamericano and will prioritize the happy path before boundary errors.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 4.1.1, Spring Web, Spring Security, Spring Data JPA, PostgreSQL, JUnit 5, Testcontainers, MockMvc, Lombok

**Storage**: PostgreSQL via Spring Data JPA; repository and DAO tests run with Testcontainers-backed instances

**Testing**: JUnit 5, SpringBootTest, MockMvc, Testcontainers, Mockito; E2E-style controller tests use random-port web environment and mocked async service behavior

**Target Platform**: JVM backend web service running locally or in CI

**Project Type**: Web service / backend API

**Performance Goals**: Controller endpoints should accept the admin request immediately and return within normal HTTP latency; tests must remain deterministic and avoid long-running scraper jobs

**Constraints**: No live external network access in tests; no real scraper execution during validation; controller tests must mock the expensive background update; parser tests must cover malformed HTML and empty input explicitly

**Scale/Scope**: Focused feature covering four classes and their integration boundaries, not the entire application surface

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Architecture compliance: PASS. The controller is tested at the HTTP boundary, repository at persistence boundary, security filter at filter boundary, and JSON parsing utility at utility boundary; no mixed-layer assertions are planned.
- Rich model ownership: PASS. The planned tests do not move business rules into the controller or service layer; they validate the existing domain boundaries.
- Validation at every layer: PASS. The tests assert request validation, filter authorization, persistence mapping, and failure handling with explicit invalid or missing data paths.
- Testing as a delivery gate: PASS. The plan requires a happy path first, then boundary and failure assertions, and uses deterministic test infrastructure (Testcontainers, MockMvc, mocking) without real scraper traffic.
- Definition of done: PASS. The feature will be considered complete only when the targeted suite passes and the architecture boundaries remain intact.

## Project Structure

### Documentation (this feature)

```text
specs/007-controller-repository-auth-tests/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── admin-player-api.md
└── spec.md
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/overcode/
│   ├── controller/
│   │   └── ExternalPlayerController.java
│   ├── persistence/
│   │   ├── dto/jpa/
│   │   ├── repository/dao/
│   │   └── repository/impl/
│   ├── security/
│   │   └── ApiKeyAuthFilter.java
│   └── persistence/repository/dao/external/scrapper/util/
│       └── JsonExtractorUtil.java
├── src/test/java/com/overcode/
│   ├── e2e/
│   ├── persistence/repository/
│   ├── security/
│   └── persistence/repository/dao/external/scrapper/util/
└── pom.xml
```

**Structure Decision**: This feature remains entirely within the backend service. Test coverage is organized by boundary: `e2e` for the controller and security web flow, persistence repository tests for data access, and utility tests for extraction/parsing logic.

## Complexity Tracking

No constitution violations or scope expansion are expected. No additional complexity exceptions are required for this feature.
