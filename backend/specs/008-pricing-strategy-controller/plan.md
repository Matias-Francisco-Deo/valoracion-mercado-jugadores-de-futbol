# Implementation Plan: Pricing Strategy Controller and End-to-End Test Suite

**Branch**: `008-pricing-strategy-controller` | **Date**: 2026-10-09 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `specs/008-pricing-strategy-controller/spec.md`

## Summary

This feature delivers the REST controller layer exposing four capabilities of `EstrategiaCotizacionService` (`recuperarTodos`, `seleccionarEstrategia`, `actualizar`, `cotizarJugadores`) under `/api/admin/estrategias-cotizacion/**`, protected by `ApiKeyAuthFilter` using the `X-API-KEY` header. Alongside the controller and its DTOs, a comprehensive End-to-End test suite (`EstrategiaCotizacionE2eTest`) is implemented in Latin American Spanish using `@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)` and `RestClient`. In accordance with project governance and user requirements, tests mock the dynamic strategy type resolution via `MockedStatic<TipoEstrategiaCotizacion>` without modifying production enums, use `PlayerService` for seeding player test records, and cover standard happy paths before border and error cases.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot, Spring Web, Spring Security, Spring Data JPA, Jakarta Validation, Lombok, Swagger / OpenAPI 3

**Storage**: PostgreSQL via Spring Data JPA; integration and E2E tests executed with PostgreSQL

**Testing**: JUnit 5, SpringBootTest (random port), RestClient, Mockito (MockedStatic for static enum mapping in test context)

**Target Platform**: JVM backend web service

**Project Type**: Web service / REST API

**Performance Goals**: Controller endpoints respond within typical HTTP latency; bulk player quotation recalculates catalog players efficiently in a single transactional operation

**Constraints**:
- Endpoints secured with `X-API-KEY` via `ApiKeyAuthFilter`
- Tests written in Latin American Spanish
- Zero modifications to production `TipoEstrategiaCotizacion` enum for testing purposes
- Player creation in test fixtures must use `PlayerService`
- Invalids parameters (negative scale factor, negative base value) must yield `400 Bad Request`

**Scale/Scope**: Focused REST controller layer, DTO package, security filter routing update, exception handling, and full E2E test coverage

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **I. Layered Architecture**: PASS. The controller (`EstrategiaCotizacionController`) communicates exclusively with `EstrategiaCotizacionService`. All DTOs are isolated under `com.overcode.controller.dto.cotizacion`. Persistence layer remains behind its repository interface.
- **II. Rich Model**: PASS. All domain rules (pricing calculations, factor thresholds) remain inside `EstrategiaCotizacion` and `ConfiguracionCotizaciones`. No business logic is placed in controllers. No unauthorized domain models are added to the model package.
- **III. Validation at Every Layer**: PASS. Input DTOs validate format, nullability, and positive numerical bounds (`@Positive`, `@PositiveOrZero`). Domain exceptions (`EstrategiaInvalidaException`) are mapped to `400 Bad Request` via `GlobalExceptionHandler`.
- **IV. Testing as a Delivery Gate**: PASS. The feature is verified by an E2E test suite in `src/test/java/com/overcode/e2e` using `@SpringBootTest` and `RestClient`. Tests are written in Latin American Spanish, ordered from happy path to boundary/error cases, and verify real database persistence.
- **V. Definition of Done**: PASS. The feature will be considered complete when all E2E tests pass, the controller is wired to the service, and the build succeeds without error.

## Project Structure

### Documentation (this feature)

```text
specs/008-pricing-strategy-controller/
├── plan.md              # Implementation plan (this document)
├── research.md          # Technical research & decisions (Phase 0 output)
├── data-model.md        # Entities and DTO specifications (Phase 1 output)
├── quickstart.md        # Validation guide and run commands (Phase 1 output)
├── contracts/           # API contract definitions (Phase 1 output)
│   └── strategy-admin-api.md
├── checklists/
│   └── requirements.md
└── spec.md              # Feature specification
```

### Source Code (repository root)

```text
src/main/java/com/overcode/
├── controller/
│   ├── EstrategiaCotizacionController.java
│   ├── dto/cotizacion/
│   │   ├── EstrategiaCotizacionResponseDTO.java
│   │   ├── CrearEstrategiaCotizacionRequestDTO.java
│   │   └── ActualizarFactorEscalaRequestDTO.java
│   └── exception/
│       └── GlobalExceptionHandler.java (handles EstrategiaInvalidaException)
├── config/
│   └── SecurityConfig.java (permit /api/admin/estrategias-cotizacion/** to ApiKeyAuthFilter)
├── security/
│   └── ApiKeyAuthFilter.java (protect /api/admin/estrategias-cotizacion/**)
└── service/interfaces/
    └── EstrategiaCotizacionService.java

src/test/java/com/overcode/
├── e2e/
│   └── EstrategiaCotizacionE2eTest.java
└── security/
    └── ApiKeyAuthFilterTest.java (additional route assertions if needed)
```

**Structure Decision**: Standard layered Maven structure. DTOs are scoped within `com.overcode.controller.dto.cotizacion`, controller lives under `com.overcode.controller`, and E2E tests reside strictly in `com.overcode.e2e`.

## Complexity Tracking

*No constitution violations. All designs strictly comply with established architecture guidelines.*
