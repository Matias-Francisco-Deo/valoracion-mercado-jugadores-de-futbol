# Implementation Plan: Login Streak Bonus (Updated)

**Branch**: `009-login-streak-bonus` | **Date**: 2026-10-22 | **Spec**: [spec.md](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/specs/009-login-streak-bonus/spec.md)

**Input**: Feature specification and updated constraints:
- `DiaDeLaSemana` enum (`LUNES`, `MARTES`, `MIERCOLES`, `JUEVES`, `VIERNES`, `SABADO`, `DOMINGO`).
- `LoginBonus` class encapsulates daily bonus values with `Map<DiaDeLaSemana, Double>` and streak calculation.
- Credit-related values use `Double` (not `Integer`), while streak counts remain `Integer`.
- Explicit custom exceptions for model, service, and persistence layers. **Java default exceptions (like `IllegalArgumentException`) CANNOT be used**.
- `DataInitializer` seeds base `LoginBonus` with values: `LUNES: 1.0, MARTES: 2.0, MIERCOLES: 2.0, JUEVES: 5.0, VIERNES: 5.0, SABADO: 10.0, DOMINGO: 15.0`.
- System supports multiple `LoginBonus` rows in the database, with one active instance linked to `ConfiguracionCotizaciones`.
- `GET /api/daily-bonus` returns current streak, today's bonus, claim status, and weekly bonus schedule.
- `POST /api/daily-bonus/claim` submits an explicit claim request.
- The bonus is **never** granted automatically on login or registration; the user MUST use the POST endpoint.
- All tests written in Latin American Spanish, covering edge cases, using static factory methods (`getUsuarioDeNombre("")`, `getLoginBonusBase()`), adhering strictly to existing project conventions and Constitution.

---

## Summary

The Login Streak Bonus feature provides daily credit rewards mapped through a dedicated `DiaDeLaSemana` enum and an internal `Map<DiaDeLaSemana, Double>` in the `LoginBonus` domain model. All credit values are typed as `Double`, streak counts as `Integer`. Errors and invalid states throw dedicated domain, service, or persistence exceptions without relying on Java default exceptions. Only one active `LoginBonus` is linked inside `ConfiguracionCotizaciones`, and bonus claims require an explicit HTTP `POST` action.

---

## Technical Context

**Language/Version**: Java 21
**Primary Dependencies**: Spring Boot 4.1.1, Spring Data JPA, Spring Security (JWT), Lombok, Hibernate
**Storage**: PostgreSQL (development/production), Testcontainers PostgreSQL (integration tests)
**Testing**: JUnit 5, Mockito, Testcontainers PostgreSQL, RestClient (`@SpringBootTest(webEnvironment = RANDOM_PORT)`)
**Target Platform**: JVM / Cloud / Containerized Web Service
**Project Type**: REST API Web Service
**Performance Goals**: Status query (`GET`) and claim processing (`POST`) latency $< 30$ ms
**Constraints**:
- Strict layered architecture (Constitution Principle I).
- Rich domain model (Constitution Principle II): `LoginBonus` and `User` own calculations and state transitions.
- Custom exceptions across all layers: no standard Java exceptions (`IllegalArgumentException`, etc.) allowed.
- Double precision for all credit values; Integer for streak counts.
- Testing in Latin American Spanish covering edge cases and utilizing scalable static factory methods.

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Requirement | Compliance Analysis | Status |
|---|---|---|---|
| **I. Layered Architecture** | Controller -> Service -> Model & Persistence. DTOs in `controller/dto`, repositories in `persistence/repository`. | Controller `DailyBonusController` communicates solely with `DailyBonusService`. Domain models remain independent of persistence annotations. JPA DTOs live in `persistence/dto/jpa/bonus`. Repositories live behind `interfaces` and `impl` packages. Custom persistence exceptions in `persistence/exception`. | PASS |
| **II. Rich Model** | All domain logic lives in model objects. New model object explicitly approved. | `LoginBonus` and `DiaDeLaSemana` approved. Scaling and calculation methods live inside `LoginBonus` and `User`. No business rules in controllers or repositories. | PASS |
| **III. Validation at Every Layer** | Validate at each layer; domain exceptions for invalid states; NO Java default exceptions. | Custom exceptions defined for each failure mode (`BonusYaReclamadoHoyException`, `RachaInvalidaException`, `DiaDeLaSemanaInvalidoException`, `ValorBonusInvalidoException`, `FechaReclamoInvalidaException`, `LoginBonusInvalidoException`, `LoginBonusNoConfiguradoException`, `LoginBonusPersistenciaException`). | PASS |
| **IV. Testing as Delivery Gate** | Unit tests for model; Testcontainers for services/repos; RestClient for E2E. Tests in Latin American Spanish covering edge cases. | Pure unit tests without Spring context for models/enums; Testcontainers integration tests for repository/service; RestClient E2E tests. Tests in Latin American Spanish using static helpers. | PASS |
| **V. Definition of Done** | All tests pass, local build clean, compliant with architecture. | Deliverables satisfy all functional and technical constraints. | PASS |

---

## Project Structure

### Documentation (this feature)

```text
specs/009-login-streak-bonus/
├── spec.md              # Feature specification
├── plan.md              # Implementation plan (this file)
├── research.md          # Phase 0 architectural decisions and research
├── data-model.md        # Phase 1 domain entities, persistence entities, state transitions
├── contracts/
│   └── daily-bonus-api.yaml # OpenAPI contract with Double credit values and DiaDeLaSemana
└── quickstart.md        # Phase 1 test and runnable validation guide
```

### Source Code Layout

```text
backend/src/main/java/com/overcode/
├── model/
│   ├── bonus/
│   │   ├── DiaDeLaSemana.java                      # [NEW] Enum for days of week/streak (LUNES..DOMINGO)
│   │   ├── LoginBonus.java                         # [NEW] Domain class with Map<DiaDeLaSemana, Double>
│   │   └── exception/
│   │       ├── BonusYaReclamadoHoyException.java   # [NEW] Model exception: double claim on same day
│   │       ├── RachaInvalidaException.java         # [NEW] Model exception: streak out of range
│   │       ├── DiaDeLaSemanaInvalidoException.java # [NEW] Model exception: null or unknown day
│   │       ├── ValorBonusInvalidoException.java    # [NEW] Model exception: bonus <= 0.0 or null
│   │       ├── FechaReclamoInvalidaException.java  # [NEW] Model exception: invalid claim date
│   │       └── LoginBonusInvalidoException.java    # [NEW] Model exception: malformed LoginBonus
│   ├── cotizacion/
│   │   └── ConfiguracionCotizaciones.java          # [UPDATE] Add LoginBonus reference and setter
│   └── User.java                                   # [UPDATE] Double creditBalance, streakCount, lastBonusClaimDate
├── persistence/
│   ├── dto/jpa/
│   │   ├── bonus/
│   │   │   └── LoginBonusJPADTO.java               # [NEW] JPA Entity with ElementCollection of rewards
│   │   ├── cotizacion/
│   │   │   └── ConfiguracionCotizacionesJPADTO.java# [UPDATE] Add ManyToOne to LoginBonusJPADTO
│   │   └── UserJPADTO.java                         # [UPDATE] streak_count and last_bonus_claim_date
│   └── repository/
│       ├── dao/jpa/
│       │   └── LoginBonusDAOJPA.java               # [NEW] Spring Data JPA interface
│       ├── interfaces/
│       │   └── LoginBonusRepository.java           # [NEW] Repository abstraction
│       └── impl/
│           └── LoginBonusRepositoryImpl.java       # [NEW] Repository implementation
├── service/
│   ├── exception/
│   │   ├── LoginBonusNoConfiguradoException.java   # [NEW] Service exception: no active LoginBonus
│   │   └── ReclamoBonusNoPermitidoException.java   # [NEW] Service exception: claim rejected
│   ├── interfaces/
│   │   └── DailyBonusService.java                  # [NEW] Service interface
│   └── impl/
│       └── DailyBonusServiceImpl.java              # [NEW] Service implementation
├── controller/
│   ├── DailyBonusController.java                   # [NEW] GET /api/daily-bonus, POST /api/daily-bonus/claim
│   └── dto/bonus/
│       ├── DailyBonusStatusResponseDTO.java        # [NEW] Response with Double todayBonus & Map
│       └── ClaimBonusResponseDTO.java              # [NEW] Response with Double creditsAwarded
└── config/
    └── DataInitializer.java                        # [UPDATE] Seed base LoginBonus Map<DiaDeLaSemana, Double>

backend/src/test/java/com/overcode/
├── testUtils/
│   ├── TestUserUtil.java                           # [UPDATE/NEW] Static factories: getUsuarioDeNombre(""), etc.
│   └── TestLoginBonusUtil.java                     # [NEW] Static factories: getLoginBonusBase(), etc.
├── model/
│   ├── bonus/
│   │   ├── DiaDeLaSemanaTest.java                  # [NEW] Unit tests for enum mapping & custom exceptions
│   │   └── LoginBonusTest.java                     # [NEW] Unit tests in Latin American Spanish
│   └── UserBonusTest.java                          # [NEW] Unit tests for User streak progression & Double credits
├── persistence/repository/
│   └── LoginBonusRepositoryTest.java               # [NEW] Integration test with PostgreSQL / Testcontainers
├── service/
│   └── DailyBonusServiceImplTest.java              # [NEW] Integration tests for service layer & edge cases
└── e2e/
    └── DailyBonusE2eTest.java                      # [NEW] E2E API tests with RestClient and SpringBootTest
```

---

## Detailed Implementation Phases

### Phase 0: Outline & Research
- Defined `DiaDeLaSemana` enum and its integration with streak indexing (`1..7`).
- Selected `Map<DiaDeLaSemana, Double>` for bonus rewards.
- Designed custom exception hierarchy eliminating Java default exceptions across all layers.
- Formatted findings in `research.md`.

### Phase 1: Design & Contracts
- **Data Model**: Updated `data-model.md` specifying `DiaDeLaSemana`, `LoginBonus` with `Map<DiaDeLaSemana, Double>`, `Double` credit balances, custom exception hierarchy, and JPA mappings.
- **API Contracts**: Updated `contracts/daily-bonus-api.yaml` with `Double` numbers and `DiaDeLaSemana` schema.
- **Quickstart Guide**: Updated `quickstart.md` with assertion patterns for custom exceptions and Latin American Spanish test specifications.

### Phase 2: Implementation Preparation (Next Step: `/speckit-tasks`)
- Execution tasks will be structured in `tasks.md` following dependency order:
  1. Custom model exceptions (`BonusYaReclamadoHoyException`, `RachaInvalidaException`, `DiaDeLaSemanaInvalidoException`, `ValorBonusInvalidoException`, `FechaReclamoInvalidaException`, `LoginBonusInvalidoException`).
  2. `DiaDeLaSemana` enum with mapping methods & unit tests.
  3. `LoginBonus` class with `Map<DiaDeLaSemana, Double>` & unit tests (`LoginBonusTest`).
  4. `User` adaptations (Double credits, streak tracking, `UserBonusTest`, `TestUserUtil`, `TestLoginBonusUtil`).
  5. Persistence layer (`LoginBonusJPADTO`, `LoginBonusDAOJPA`, `LoginBonusRepository`, `LoginBonusPersistenciaException`).
  6. `ConfiguracionCotizaciones` & `DataInitializer` integration.
  7. Service layer (`DailyBonusService`, custom service exceptions, integration tests).
  8. Controller & DTOs (`DailyBonusController`, `GlobalExceptionHandler` mapping).
  9. End-to-End API tests (`DailyBonusE2eTest`).
