---

description: "Task list for Monthly Sales Metrics feature implementation"
---

# Tasks: Monthly Sales Metrics

**Input**: Design documents from `/specs/011-monthly-sales-metrics/`  
**Prerequisites**: [plan.md](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/specs/011-monthly-sales-metrics/plan.md), [spec.md](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/specs/011-monthly-sales-metrics/spec.md), [data-model.md](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/specs/011-monthly-sales-metrics/data-model.md), [contracts/admin-sales-metrics-api.yaml](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/specs/011-monthly-sales-metrics/contracts/admin-sales-metrics-api.yaml), [research.md](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/specs/011-monthly-sales-metrics/research.md), [quickstart.md](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/specs/011-monthly-sales-metrics/quickstart.md)  
**Tests**: Tests are required per project Constitution (Principle IV) and user request ("Make sure to test properly, so the data is real").

## Format: `[ID] [P?] [Story] Description with file path`

- **[P]**: Can run in parallel (different files, no blocking dependency)
- **[Story]**: User story identifier ([US1], [US2])
- Exact file paths included in all tasks

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Initialize directory structure for new DTO package

- [ ] T001 Create metrics DTO package directory in src/main/java/com/overcode/controller/dto/metrics

---

## Phase 2: Foundational (Data Access & Persistence)

**Purpose**: Core data access infrastructure on `TransactionDAOJPA` and `TransactionRepository` required by all user stories

**⚠️ CRITICAL**: Must be completed and verified before starting User Story endpoints

- [ ] T002 Add countCompletedSalesBetween query method with @Query in src/main/java/com/overcode/persistence/repository/dao/jpa/TransactionDAOJPA.java
- [ ] T003 Expose contarVentasEntre in src/main/java/com/overcode/persistence/repository/interfaces/TransactionRepository.java and implement in src/main/java/com/overcode/persistence/repository/impl/TransactionRepositoryImpl.java
- [ ] T004 Add repository integration tests with real PostgreSQL Testcontainers for contarVentasEntre in src/test/java/com/overcode/persistence/repository/TransactionRepositoryTest.java

**Checkpoint**: Repository layer tested with real PostgreSQL data — ready for service and controller implementation.

---

## Phase 3: User Story 1 - View Executed Sales Count in a Month (Priority: P1) 🎯 MVP

**Goal**: Enable administrators to query the total executed sales count for a specified month and year via `GET /api/admin/metrics/sales?year={year}&month={month}` protected by `X-API-KEY`.

**Independent Test**: Send an authenticated request with `year=YYYY&month=MM` for a period with known real transactions and verify the returned count matches actual `SELL` records.

### Implementation for User Story 1

- [ ] T005 [P] [US1] Create MonthlySalesResponseDTO record in src/main/java/com/overcode/controller/dto/metrics/MonthlySalesResponseDTO.java
- [ ] T006 [P] [US1] Create SalesMetricsService interface in src/main/java/com/overcode/service/interfaces/SalesMetricsService.java
- [ ] T007 [US1] Implement SalesMetricsServiceImpl with month validation and boundary calculation in src/main/java/com/overcode/service/impl/SalesMetricsServiceImpl.java
- [ ] T008 [P] [US1] Create unit tests for SalesMetricsServiceImpl in src/test/java/com/overcode/service/impl/SalesMetricsServiceImplTest.java
- [ ] T009 [US1] Implement SalesMetricsController mapped to /api/admin/metrics/sales in src/main/java/com/overcode/controller/SalesMetricsController.java
- [ ] T010 [US1] Create E2E tests for monthly sales metrics with explicit parameters and API key security in src/test/java/com/overcode/e2e/SalesMetricsE2eTest.java

**Checkpoint**: User Story 1 MVP fully functional and independently testable end-to-end.

---

## Phase 4: User Story 2 - Query Monthly Sales for Current Active Month (Priority: P2)

**Goal**: Automatically default omitted month and year parameters to the current calendar month-to-date when calling `GET /api/admin/metrics/sales`.

**Independent Test**: Send an authenticated request to `GET /api/admin/metrics/sales` without parameters and verify that the calculation covers day 1 of the active month up to the current date.

### Implementation for User Story 2

- [ ] T011 [US2] Update SalesMetricsServiceImpl to default null year and month parameters to current calendar month in src/main/java/com/overcode/service/impl/SalesMetricsServiceImpl.java
- [ ] T012 [P] [US2] Add unit tests for current month defaulting in src/test/java/com/overcode/service/impl/SalesMetricsServiceImplTest.java
- [ ] T013 [US2] Add E2E tests for omitting year and month parameters in src/test/java/com/overcode/e2e/SalesMetricsE2eTest.java

**Checkpoint**: User Stories 1 and 2 complete, working seamlessly together and testable independently.

---

## Phase 5: Polish & Cross-Cutting Concerns

**Purpose**: Quality gates, architecture validation, and full regression verification

- [ ] T014 [P] Run and verify architecture compliance tests in src/test/java/com/overcode/architecture/ArchitectureTest.java
- [ ] T015 Run full test suite and build verification via ./mvnw clean test
- [ ] T016 Execute manual quickstart verification scenarios per specs/011-monthly-sales-metrics/quickstart.md

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately.
- **Foundational (Phase 2)**: Depends on Phase 1 — BLOCKS all user story endpoint work.
- **User Story 1 (Phase 3)**: Depends on Phase 2 completion.
- **User Story 2 (Phase 4)**: Depends on Phase 3 (enhances service and controller with defaulting logic).
- **Polish (Phase 5)**: Depends on Phases 1–4 completion.

### User Story Dependencies

- **User Story 1 (P1)**: Foundations (T002-T004) must be complete.
- **User Story 2 (P2)**: Builds upon the `SalesMetricsServiceImpl` created in US1 to add defaulting behavior.

### Parallel Opportunities

- In Phase 3: `T005` (DTO) and `T006` (Service interface) can be created in parallel.
- `T008` (Unit tests) can be drafted alongside `T007` (Service implementation).
- In Phase 5: `T014` (Architecture test) can run in parallel with review.

---

## Parallel Example: User Story 1

```bash
# Launch DTO and Service interface together:
Task: "Create MonthlySalesResponseDTO record in src/main/java/com/overcode/controller/dto/metrics/MonthlySalesResponseDTO.java"
Task: "Create SalesMetricsService interface in src/main/java/com/overcode/service/interfaces/SalesMetricsService.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (`T001`).
2. Complete Phase 2: Foundational persistence `@Query` and repository tests (`T002`-`T004`).
3. Complete Phase 3: User Story 1 (`T005`-`T010`).
4. **STOP and VALIDATE**: Run `TransactionRepositoryTest`, `SalesMetricsServiceImplTest`, and `SalesMetricsE2eTest`.

### Incremental Delivery

1. Add User Story 2 (`T011`-`T013`) to support parameter defaulting.
2. Run Polish phase (`T014`-`T016`) for full regression and `ArchitectureTest` gate.
