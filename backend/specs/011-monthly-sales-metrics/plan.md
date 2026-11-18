# Implementation Plan: Monthly Sales Metrics

**Branch**: `011-monthly-sales-metrics` | **Date**: 2026-11-18 | **Spec**: [spec.md](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/specs/011-monthly-sales-metrics/spec.md)

**Input**: Feature specification from `/specs/011-monthly-sales-metrics/spec.md` with explicit user design constraints:
- No complex model logic
- No extreme in-memory mapping in service or repository
- Direct `@Query` on `TransactionDAOJPA` to count executed `SELL` records
- Minimal and focused scope: total count of sales in a month
- Robust real-data testing with Testcontainers PostgreSQL and RestClient E2E

---

## Summary

Provide an administrative endpoint `GET /api/admin/metrics/sales` to retrieve the count of peer-to-peer sales executed in a given calendar month (defaulting to the current month-to-date). The solution uses a direct Spring Data JPA `@Query` on `TransactionDAOJPA` filtering by `type = 'SELL'` and `date` boundaries. This ensures O(1) memory consumption and sub-second execution without creating unnecessary model entities or loading heavy entity graphs.

---

## Technical Context

**Language/Version**: Java 21  
**Primary Dependencies**: Spring Boot 3.x, Spring Data JPA, Spring Security, Hibernate, Lombok  
**Storage**: PostgreSQL
**Testing**: JUnit 5, Mockito, AssertJ, ArchUnit (`ArchitectureTest`), RestClient (@SpringBootTest)  
**Target Platform**: Linux / Containerized JVM web service  
**Project Type**: RESTful Web Application Backend  
**Performance Goals**: Query latency < 500ms for months with up to 100,000 transactions  
**Constraints**: Zero entity-to-domain mapping overhead for counting; protected under `/api/admin/**` via `X-API-KEY`  
**Scale/Scope**: Marketplace transaction records spanning multiple years and active users  

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-checked after Phase 1 design.*

| Principle / Rule | Compliance Status | Justification & Verification |
|---|---|---|
| **I. Layered Architecture** | **PASS** | Strict flow: `SalesMetricsController` -> `SalesMetricsService` -> `TransactionRepository` -> `TransactionDAOJPA`. DTO located in `com.overcode.controller.dto.metrics`. Controller returns only DTO, never JPA entities. |
| **II. Rich Model** | **PASS** | No arbitrary model entities added. The user requested counting existing transactional records. The response is a pure analytical summary DTO without bloating domain models. |
| **III. Validation at Every Layer** | **PASS** | DTO/Controller checks parameter types; Service validates month (1-12) and year (>0) ranges and date boundaries; deterministic validation exceptions thrown for invalid inputs. |
| **IV. Testing as Delivery Gate** | **PASS** | Repository tests run against real PostgreSQL Testcontainers; Service tests test boundary logic; E2E API tests in dedicated `com.overcode.e2e` package using `RestClient`. Test names written in Latin-American Spanish. |
| **V. Definition of Done** | **PASS** | Plan requires unit tests, integration tests, E2E tests, and `ArchitectureTest` to pass with successful `mvn clean compile`. |

---

## Project Structure

### Documentation (this feature)

```text
specs/011-monthly-sales-metrics/
├── spec.md              # Feature specification & clarifications
├── plan.md              # This file (/speckit-plan output)
├── research.md          # Phase 0 output: decisions, rationale, alternatives
├── data-model.md        # Phase 1 output: DTOs, parameters, query definitions
├── quickstart.md        # Phase 1 output: run & test instructions
├── contracts/           # Phase 1 output: OpenAPI specification
│   └── admin-sales-metrics-api.yaml
└── checklists/
    └── requirements.md  # Quality checklist
```

### Source Code Impact (repository root)

```text
src/main/java/com/overcode/
├── controller/
│   ├── SalesMetricsController.java                          # GET /api/admin/metrics/sales
│   └── dto/
│       └── metrics/
│           └── MonthlySalesResponseDTO.java                 # Response record (year, month, totalSales)
├── service/
│   ├── interfaces/
│   │   └── SalesMetricsService.java                         # Service interface
│   └── impl/
│       └── SalesMetricsServiceImpl.java                     # Defaulting, validation, boundary math
└── persistence/
    └── repository/
        ├── dao/
        │   └── jpa/
        │       └── TransactionDAOJPA.java                   # @Query countCompletedSalesBetween(...)
        ├── interfaces/
        │   └── TransactionRepository.java                   # contarVentasEntre(...)
        └── impl/
            └── TransactionRepositoryImpl.java               # Delegation to DAO

src/test/java/com/overcode/
├── e2e/
│   └── SalesMetricsE2eTest.java                             # E2E tests with RestClient & X-API-KEY
├── service/impl/
│   └── SalesMetricsServiceImplTest.java                     # Unit tests for service & date logic
└── persistence/repository/
    └── TransactionRepositoryTest.java                       # Integration tests with real PostgreSQL
```

---

## Implementation Phases & Strategy

### Phase 0: Outline & Research *(Completed)*
- Documented `@Query` count optimization on `TransactionDAOJPA` to avoid heap allocations.
- Derived boundary resolution with `java.time.YearMonth`.
- Verified admin security integration via `SecurityConfig.adminFilterChain`.

### Phase 1: Design & Contracts *(Completed)*
- Created `data-model.md` covering query parameters and `MonthlySalesResponseDTO`.
- Authored `contracts/admin-sales-metrics-api.yaml` describing OpenAPI contract.
- Authored `quickstart.md` specifying validation flows and test commands.

### Phase 2: Tasks & Implementation *(Next: `/speckit-tasks`)*
1. **Repository Layer**:
   - Add `@Query countCompletedSalesBetween(LocalDateTime startDate, LocalDateTime endDate)` to `TransactionDAOJPA`.
   - Expose `Long contarVentasEntre(LocalDateTime inicio, LocalDateTime fin)` on `TransactionRepository` and implement in `TransactionRepositoryImpl`.
   - Add integration tests in `TransactionRepositoryTest` inserting real test records (with Testcontainers PostgreSQL).
2. **Service Layer**:
   - Define `SalesMetricsService` interface.
   - Implement `SalesMetricsServiceImpl` with parameter validation (throwing validation exception for invalid month/year), defaulting to current month, and date boundary calculation.
   - Author unit tests `SalesMetricsServiceImplTest`.
3. **Controller Layer**:
   - Define `MonthlySalesResponseDTO` under `com.overcode.controller.dto.metrics`.
   - Implement `SalesMetricsController` at `/api/admin/metrics/sales`.
4. **End-to-End & Architecture Validation**:
   - Author `SalesMetricsE2eTest` in `com.overcode.e2e`.
   - Run `ArchitectureTest` and full build validation.
