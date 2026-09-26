# Implementation Plan: Player and User Controller Test Suite

**Branch**: `003-player-user-controller-tests` | **Date**: 2026-09-11 | **Spec**: `specs/003-player-user-controller-tests/spec.md`

**Input**: Feature specification from `/specs/003-player-user-controller-tests/spec.md`

## Summary

Implement an automated test suite covering `PlayerController` and `UserController` inside the backend. The suite uses the exact same architecture and pattern as `AuthControllerTest`: `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` with Spring's fluent `RestClient`. All test methods are named in Latin-American Spanish, reusable test inputs/objects are declared as `private static final` constants, assertions target specific `HttpClientErrorException` subclasses (avoiding generic exception assertions), and each test verifies a single, isolated scenario. In accordance with project instructions, zero production code will be modified, and any test that reveals an existing backend limitation or unexpected status will be tagged with `// TODO SDD TEST FAILURE`.

## Technical Context

**Language/Version**: Java 21 with Spring Boot 4.1.1

**Primary Dependencies**: Spring Web, Spring Security, Spring Data JPA, Spring Boot Test, Spring Security Test, RestClient, Testcontainers PostgreSQL 1.20.4, JJWT 0.13.0

**Storage**: PostgreSQL managed via SpringBoot

**Testing**: JUnit 5, Spring Boot Test, RestClient

**Target Platform**: JVM / Java 21 Spring Boot backend runtime

**Project Type**: REST API Controller Integration Test Suite

**Performance Goals**: Test execution completes deterministically within the standard Maven build lifecycle (<10s total test execution time)

**Constraints**:
- Zero changes to production source code (`src/main/`)
- Test format identical to `AuthControllerTest` (`@SpringBootTest(webEnvironment = RANDOM_PORT)` + `RestClient`)
- Test names in Latin-American Spanish (`<accion><Condicion><ResultadoEsperado>`)
- Centralized `private static final` constants for test objects and fixtures
- Specific exception assertions only (`HttpClientErrorException.NotFound`, `BadRequest`, `Forbidden`)
- Granular test methods (single concern per test)
- Tag unexpected test failures with `// TODO SDD TEST FAILURE`

**Scale/Scope**: 2 test files (`PlayerControllerTest.java` and `UserControllerTest.java`) containing approximately 15 targeted test methods covering happy paths, edge cases, input validation, and access control.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Layered Architecture**: PASS. Test classes are placed strictly under `com.overcode.controller` in `src/test/java`, interacting only through HTTP REST calls via `RestClient`.
- **Rich Model**: PASS. No domain logic or new domain models are introduced.
- **Validation at Every Layer**: PASS. Tests explicitly verify HTTP responses for DTO serialization, missing/malformed path variables, and exception mapping.
- **Testing as a Delivery Gate**: PASS. Tests are ordered logically from standard happy paths to border and security cases. Existing tests are untouched.
- **Definition of Done**: PASS. The feature consists of passing automated tests without modifying production source code.

## Project Structure

### Documentation (this feature)

```text
specs/003-player-user-controller-tests/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
│   └── controller-api.yaml
└── checklists/
    └── requirements.md
```

### Source Code (repository root)

```text
backend/
├── src/
│   ├── main/
│   │   └── java/com/overcode/
│   │       ├── controller/
│   │       │   ├── PlayerController.java
│   │       │   ├── UserController.java
│   │       │   ├── dto/
│   │       │   │   ├── player/PlayerResponseDTO.java
│   │       │   │   └── user/UserResponseDTO.java
│   │       │   └── exception/GlobalExceptionHandler.java
│   │       ├── model/
│   │       │   ├── Player.java
│   │       │   └── User.java
│   │       └── service/
│   └── test/
│       └── java/com/overcode/
│           ├── controller/
│           │   ├── AuthControllerTest.java       # Pre-existing test suite (preserved)
│           │   ├── PlayerControllerTest.java     # NEW test suite
│           │   └── UserControllerTest.java       # NEW test suite
│           └── testUtils/
│               └── TestService.java              # Database cleanup helper
├── pom.xml
└── specs/
```

**Structure Decision**: The test classes `PlayerControllerTest.java` and `UserControllerTest.java` will be placed directly in `src/test/java/com/overcode/controller/`, mirroring the location and structure of `AuthControllerTest.java`. Reusable test utilities (`TestService`) are already available in `com.overcode.testUtils`.

## Phase 0: Research & Design Decisions

1. **Test Architecture**: Replicated from `AuthControllerTest` using `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` and `RestClient`.
2. **Suite Separation**: Divided into `PlayerControllerTest` and `UserControllerTest` for clear separation of concerns.
3. **Naming & Language**: Latin-American Spanish method names (`listarJugadoresConBaseVaciaDevuelveListaVacia`, `obtenerJugadorPorIdExistenteDevuelveOkConDatosCorrectos`, etc.).
4. **Fixture Constants**: Declared as `private static final` in each test class (`DEFAULT_USERNAME`, `DEFAULT_EMAIL`, `PLAYER_NAME`, `NON_EXISTENT_ID`, `MALFORMED_ID`, `INVALID_BEARER_TOKEN`).
5. **Specific Throws**: Strict assertion on `HttpClientErrorException.NotFound.class`, `BadRequest.class`, and `Forbidden.class`.
6. **Failure Annotation**: No production code changes; if a test fails due to existing application behavior, annotate with `// TODO SDD TEST FAILURE`.

## Phase 1: Design Artifacts

- **Data Model**: Detailed in [`data-model.md`](data-model.md) covering `PlayerResponseDTO`, `UserResponseDTO`, `ErrorResponseDTO`, and test constants.
- **Contract Definition**: OpenAPI 3.0 specification in [`contracts/controller-api.yaml`](contracts/controller-api.yaml) defining `/players`, `/players/{id}`, and `/users/{id}`.
- **Quickstart Guide**: Detailed verification instructions in [`quickstart.md`](quickstart.md).

## Complexity Tracking

> No violations of project constitution or architectural constraints. Zero production code changes required.
