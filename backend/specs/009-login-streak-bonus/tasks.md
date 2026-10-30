# Tasks: Login Streak Bonus

**Input**: Design documents from `/specs/009-login-streak-bonus/` (`plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/daily-bonus-api.yaml`, `quickstart.md`, `.specify/memory/constitution.md`)

**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/`

**Tests**: Testing is MANDATORY per Constitution Principle IV and user instructions. All tests MUST be in Latin American Spanish, test edge cases, and use scalable static factory methods (`TestUserUtil.getUsuarioDeNombre("")`, `TestLoginBonusUtil.getLoginBonusBase()`).

**Organization**: Tasks are grouped by user story to enable independent implementation and testing.

## Format: `[ID] [P?] [Story?] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: User story identifier (`[US1]`, `[US2]`, `[US3]`, `[US4]`, `[US5]`)
- Exact file paths referenced relative to project root (`src/...`)

---

## Phase 1: Setup (Test Infrastructure & Static Factories)

**Purpose**: Establish reusable static factory utilities and test harnesses required for scalable testing across all subsequent phases.

- [X] T001 [P] Create test factory utility `TestLoginBonusUtil` with static helpers `getLoginBonusBase()` (seeding base rewards map: LUNES: 1.0, MARTES: 2.0, MIERCOLES: 2.0, JUEVES: 5.0, VIERNES: 5.0, SABADO: 10.0, DOMINGO: 15.0) and `getLoginBonusPersonalizado(Map<DiaDeLaSemana, Double>)` in `src/test/java/com/overcode/testUtils/TestLoginBonusUtil.java`
- [X] T002 [P] Create or update test factory utility `TestUserUtil` with static helper methods `getUsuarioDeNombre(String nombre)` and `getUsuarioConRacha(String nombre, int racha, LocalDate ultimoReclamo)` in `src/test/java/com/overcode/testUtils/TestUserUtil.java`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core domain enums, custom exceptions across all layers, rich model structures, persistence entities, repositories, and database initialization.

**⚠️ CRITICAL**: Must be completed before any user story endpoint or service workflow is implemented.

### Custom Exception Hierarchy (Zero Java Default Exceptions)
- [X] T003 [P] Create custom domain exceptions for model layer: `BonusYaReclamadoHoyException`, `DiaInvalidoException`, `DiaDeLaSemanaInvalidoException`, `ValorBonusInvalidoException`, `FechaReclamoBonusLoginInvalidaException`, and `LoginBonusInvalidoException` in `src/main/java/com/overcode/model/bonus/exception/`
- [X] T004 [P] Create custom service exceptions: `LoginBonusNoConfiguradoException` and `ReclamoBonusNoPermitidoException` in `src/main/java/com/overcode/service/exception/`

### Domain Enums and Core Models
- [X] T006 [P] Create `DiaDeLaSemana` enum (`LUNES`, `MARTES`, `MIERCOLES`, `JUEVES`, `VIERNES`, `SABADO`, `DOMINGO`) with `desdeRacha(int racha)` and `desdeDayOfWeek(DayOfWeek dayOfWeek)` methods enforcing custom exceptions in `src/main/java/com/overcode/model/bonus/DiaDeLaSemana.java`
- [X] T007 [P] Create unit test in Spanish `DiaDeLaSemanaTest` verifying valid mappings and that invalid values throw `DiaInvalidoException` and `DiaDeLaSemanaInvalidoException` in `src/test/java/com/overcode/model/bonus/DiaDeLaSemanaTest.java`
- [X] T008 Implement `LoginBonus` domain class with `Map<DiaDeLaSemana, Double> bonusRewards`, `getBonusParaDia(DiaDeLaSemana)`, `getBonusParaRacha(int)`, `calcularSiguienteRacha(LocalDate, LocalDate, int)`, `puedeReclamarHoy(LocalDate, LocalDate)`, and unmodifiable schedule getter in `src/main/java/com/overcode/model/bonus/LoginBonus.java`
- [X] T009 Update `ConfiguracionMercado` domain class to include `loginBonus` field, getter/setter, and `cambiarLoginBonusPor(LoginBonus)` in `src/main/java/com/overcode/model/cotizacion/ConfiguracionCotizaciones.java`
- [X] T010 Update `User` domain model to support `Double creditBalance`, add `Integer streakCount = 0`, `LocalDate lastBonusClaimDate`, and domain methods `puedeReclamarBonusDiarioHoy` and `reclamarBonusDiario` in `src/main/java/com/overcode/model/User.java`

### Persistence Layer
- [X] T011 [P] Create JPA entity `LoginBonusJPADTO` with `@ElementCollection` map of `DiaDeLaSemana` to `Double` and bidirectional `aModelo()` / `desdeModelo()` mappers in `src/main/java/com/overcode/persistence/dto/jpa/bonus/LoginBonusJPADTO.java`
- [X] T012 Update `ConfiguracionMercadoJPADTO` to add `@ManyToOne` relation to `LoginBonusJPADTO` and bidirectional domain mappings in `src/main/java/com/overcode/persistence/dto/jpa/cotizacion/ConfiguracionCotizacionesJPADTO.java`
- [X] T013 Update `UserJPADTO` to persist `Double creditBalance`, `streak_count`, and `last_bonus_claim_date` with domain mappers in `src/main/java/com/overcode/persistence/dto/jpa/UserJPADTO.java`
- [X] T014 [P] Create Spring Data JPA repository `LoginBonusDAOJPA` in `src/main/java/com/overcode/persistence/repository/dao/jpa/LoginBonusDAOJPA.java`
- [X] T015 [P] Create repository interface `LoginBonusRepository` in `src/main/java/com/overcode/persistence/repository/interfaces/LoginBonusRepository.java`
- [X] T016 Implement repository `LoginBonusRepositoryImpl` wrapping `LoginBonusDAOJPA` and throwing `LoginBonusPersistenciaException` upon failure in `src/main/java/com/overcode/persistence/repository/impl/LoginBonusRepositoryImpl.java`
- [X] T017 Create repository integration test `LoginBonusRepositoryTest` with PostgreSQL in `src/test/java/com/overcode/persistence/repository/LoginBonusRepositoryTest.java`

### Database Seeding & Global Configuration
- [X] T018 Update `DataInitializer` to instantiate default `LoginBonus` with base credit streak map (`LUNES: 1.0, MARTES: 2.0, MIERCOLES: 2.0, JUEVES: 5.0, VIERNES: 5.0, SABADO: 10.0, DOMINGO: 15.0`), save it via repository if absent, and link it in `ConfiguracionMercado` in `src/main/java/com/overcode/config/DataInitializer.java`

**Checkpoint**: Foundation ready - all models, repositories, exceptions, and baseline data initialized.

---

## Phase 3: User Story 1 - Explicit Daily Claim & Consecutive Day Streak Progression (Priority: P1) 🎯 MVP

**Goal**: Authenticated user explicitly submits a claim via `POST /api/daily-bonus/claim`. Their streak progresses consecutively (Day 1 -> Day 2 -> ... -> Day 7), credits are awarded according to `Map<DiaDeLaSemana, Double>`, and NO bonus is ever granted implicitly on login.

**Independent Test**: Register/login a user, verify balance is 0.0. Claim on Day 1 (1.0 credit, streak 1). Advance to Day 2, claim again (2.0 credits, cumulative balance 3.0, streak 2). Verify login alone never grants credits.

### Tests for User Story 1
- [X] T019 [P] [US1] Unit test in Spanish `LoginBonusTest` testing consecutive streak calculations and bonus value retrieval in `src/test/java/com/overcode/model/bonus/LoginBonusTest.java`
- [X] T020 [P] [US1] Unit test in Spanish `UserBonusTest` verifying `reclamarBonusDiario` updates `streakCount`, credits balance with `Double`, and verifies login never alters balance in `src/test/java/com/overcode/model/UserBonusTest.java`
- [X] T021 [P] [US1] Service integration test in Spanish `BonusServiceImplTest` testing consecutive daily claims and credit awards in `src/test/java/com/overcode/service/DailyBonusServiceImplTest.java`
- [X] T022 [P] [US1] End-to-End API test in Spanish `DailyBonusE2eTest` verifying `POST /api/daily-bonus/claim` for Day 1 and Day 2 progression and ensuring `POST /auth/login` does not award bonus in `src/test/java/com/overcode/e2e/DailyBonusE2eTest.java`

### Implementation for User Story 1
- [X] T023 [P] [US1] Create DTO `ClaimBonusResponseDTO` (creditsAwarded Double, newStreak Integer, currentCreditBalance Double, claimDate LocalDate, message String) in `src/main/java/com/overcode/controller/dto/bonus/ClaimBonusResponseDTO.java`
- [X] T024 [US1] Define service interface `BonusService` with method `reclamarBonus(String username, LocalDate fechaActual)` in `src/main/java/com/overcode/service/interfaces/DailyBonusService.java`
- [X] T025 [US1] Implement `BonusServiceImpl` resolving active `LoginBonus` from `ConfiguracionMercado`, executing `user.reclamarBonusDiario()`, persisting updated user, and returning claim result in `src/main/java/com/overcode/service/impl/DailyBonusServiceImpl.java`
- [X] T026 [US1] Create controller `DailyBonusController` with `POST /api/daily-bonus/claim` secured with `@AuthenticationPrincipal UserAuth` in `src/main/java/com/overcode/controller/DailyBonusController.java`

**Checkpoint**: User Story 1 (MVP) is fully functional, independently tested, and proven to never grant bonuses on login.

---

## Phase 4: User Story 2 - Streak Reset on Missed Days (Priority: P1)

**Goal**: When an authenticated user misses one or more calendar days between claims, the streak counter resets to 1 (LUNES) and the Day 1 reward (1.0 credit) is awarded.

**Independent Test**: Claim on Day T. Advance clock to Day T + 2 (or 30 days later). Call `POST /api/daily-bonus/claim`. Verify streak becomes 1 and 1.0 credit is awarded.

### Tests for User Story 2
- [X] T027 [P] [US2] Unit test in Spanish `alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1()` and long inactivity edge case in `src/test/java/com/overcode/model/bonus/LoginBonusTest.java`
- [X] T028 [P] [US2] Service integration test in Spanish testing streak reset after missing days in `src/test/java/com/overcode/service/DailyBonusServiceImplTest.java`
- [X] T029 [P] [US2] End-to-End API test in Spanish in `DailyBonusE2eTest` verifying streak reset to 1 after a missed calendar day in `src/test/java/com/overcode/e2e/DailyBonusE2eTest.java`

### Implementation for User Story 2
- [X] T030 [US2] Ensure `verificarPuedeReclamarBonus` in `LoginBonus` handles gaps $> 1$ day returning 1 (LUNES) and awards base reward in `src/main/java/com/overcode/model/bonus/LoginBonus.java`

**Checkpoint**: User Story 2 fully functional and verified with edge cases.

---

## Phase 5: User Story 3 - Weekly Streak Cycle Reset (Priority: P2)

**Goal**: When a user completes a 7-day streak (claiming Day 7 / DOMINGO), the 8th consecutive day claim cleanly rolls over back to Day 1 (LUNES) and starts a new weekly cycle.

**Independent Test**: Simulate 7 consecutive daily claims reaching Day 7 (15.0 credits). On Day 8, execute `POST /api/daily-bonus/claim`. Verify streak becomes 1 and 1.0 credit is awarded.

### Tests for User Story 3
- [X] T031 [P] [US3] Unit test in Spanish `alReclamarEnElDia8LaRachaSeReiniciaADia1CicloSemanal()` in `src/test/java/com/overcode/model/bonus/LoginBonusTest.java`
- [X] T032 [P] [US3] Service integration test in Spanish verifying 7-day progression followed by Day 8 cycle rollover in `src/test/java/com/overcode/service/DailyBonusServiceImplTest.java`
- [X] T033 [P] [US3] End-to-End API test in Spanish in `DailyBonusE2eTest` testing Day 7 to Day 8 transition in `src/test/java/com/overcode/e2e/DailyBonusE2eTest.java`

### Implementation for User Story 3
- [X] T034 [US3] Validate and enforce `rachaActual >= 7` transition to 1 in `LoginBonus.calcularSiguienteRacha()` in `src/main/java/com/overcode/model/bonus/LoginBonus.java`

**Checkpoint**: Weekly 7-day loop complete and validated.

---

## Phase 6: User Story 4 - Single Bonus Award Per Calendar Day (Priority: P2)

**Goal**: Prevent duplicate claims on the same UTC calendar day. Subsequent claim calls on the same day must throw `BonusYaReclamadoHoyException` and return HTTP 400 Bad Request with a clear message, leaving balance and streak unchanged.

**Independent Test**: Call `POST /api/daily-bonus/claim` twice on the same UTC date. First call returns 200 OK; second call returns 400 Bad Request with `"El bonus diario de hoy ya ha sido reclamado."`.

### Tests for User Story 4
- [X] T035 [P] [US4] Unit test in Spanish `alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException()` in `src/test/java/com/overcode/model/bonus/LoginBonusTest.java`
- [X] T036 [P] [US4] Service integration test in Spanish verifying `BonusYaReclamadoHoyException` is thrown on duplicate same-day claim in `src/test/java/com/overcode/service/DailyBonusServiceImplTest.java`
- [X] T037 [P] [US4] End-to-End API test in Spanish verifying HTTP 400 Bad Request and error response payload on same-day duplicate claim in `src/test/java/com/overcode/e2e/DailyBonusE2eTest.java`

### Implementation for User Story 4
- [X] T038 [US4] Enforce same-day check in `LoginBonus.calcularSiguienteRacha()` throwing `BonusYaReclamadoHoyException` in `src/main/java/com/overcode/model/bonus/LoginBonus.java`
- [X] T039 [US4] Update `GlobalExceptionHandler` to catch `BonusYaReclamadoHoyException` and return HTTP 400 Bad Request with `ErrorResponseDTO` in `src/main/java/com/overcode/controller/exception/GlobalExceptionHandler.java`

**Checkpoint**: Idempotency and duplicate claim protection fully enforced.

---

## Phase 7: User Story 5 - Bonus and Streak Status Query (Priority: P3)

**Goal**: Authenticated user queries `GET /api/daily-bonus` to inspect their current streak count, today's projected bonus (`Double`), whether today has already been claimed (`alreadyClaimedToday`), and the entire weekly bonus schedule (`Map<DiaDeLaSemana, Double>`).

**Independent Test**: Query `GET /api/daily-bonus` before claiming (shows `alreadyClaimedToday: false`, `todayBonus: 1.0`, full map). Claim bonus, then query again (shows `alreadyClaimedToday: true`, `currentStreak: 1`, `todayBonus: 1.0`).

### Tests for User Story 5
- [X] T040 [P] [US5] Service integration test in Spanish testing `obtenerEstadoBonus()` before and after claiming in `src/test/java/com/overcode/service/DailyBonusServiceImplTest.java`
- [X] T041 [P] [US5] End-to-End API test in Spanish in `DailyBonusE2eTest` verifying `GET /api/daily-bonus` response payload and status before/after claim in `src/test/java/com/overcode/e2e/DailyBonusE2eTest.java`

### Implementation for User Story 5
- [X] T042 [P] [US5] Create DTO `DailyBonusStatusResponseDTO` (`currentStreak Integer`, `todayBonus Double`, `alreadyClaimedToday Boolean`, `weeklyBonuses Map<DiaDeLaSemana, Double>`) in `src/main/java/com/overcode/controller/dto/bonus/DailyBonusStatusResponseDTO.java`
- [X] T043 [US5] Add `obtenerEstadoBonus(String username, LocalDate fechaActual)` to `BonusService` and `BonusServiceImpl` in `src/main/java/com/overcode/service/impl/DailyBonusServiceImpl.java`
- [X] T044 [US5] Add endpoint `GET /api/daily-bonus` in `DailyBonusController` in `src/main/java/com/overcode/controller/DailyBonusController.java`

**Checkpoint**: Streak query endpoint fully functional and integrated with status response.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Validate compliance with architecture, ensure clean test run, and verify documentation integrity.

- [X] T045 [P] Verify all unit, service, repository, and E2E tests have Latin American Spanish test method names, proper assertions, and clean logging
- [X] T046 [P] Audit codebase to confirm ZERO occurrences of Java default exceptions (`IllegalArgumentException`, `IllegalStateException`) in custom bonus code
- [X] T047 Execute full build and test verification (`./mvnw clean test`) ensuring 100% pass rate
- [X] T048 Update OpenAPI documentation / Swagger config if needed in `src/main/java/com/overcode/config/OpenApiConfig.java`

---

## Dependencies & Story Execution Order

```
Phase 1: Setup (Test Factories)
   ↓
Phase 2: Foundational (Enums, Exceptions, Models, Persistence, Seeding)
   ↓
Phase 3: User Story 1 (P1) - Explicit Claim & Consecutive Streak Progression (MVP)
   ↓
Phase 4: User Story 2 (P1) - Streak Reset on Missed Days
   ↓
Phase 5: User Story 3 (P2) - Weekly Streak Cycle Reset
   ↓
Phase 6: User Story 4 (P2) - Idempotency & Same-Day Claim Rejection
   ↓
Phase 7: User Story 5 (P3) - Bonus and Streak Status Query (GET endpoint)
   ↓
Phase 8: Polish & Build Verification
```

---

## Parallel Execution Opportunities

- In **Phase 1**: T001 (`TestLoginBonusUtil`) and T002 (`TestUserUtil`) can run in parallel.
- In **Phase 2**:
  - Exceptions T003, T004, T005 can be created in parallel.
  - Enum T006, tests T007, and JPA DTO T011 can run in parallel.
  - DAO T014 and Interface T015 can be defined in parallel.
- In **Phase 3**:
  - Tests T019 (`LoginBonusTest`), T020 (`UserBonusTest`), T021 (`BonusServiceImplTest`), T022 (`DailyBonusE2eTest`), and DTO T023 can all be authored in parallel before implementation.
- In **Phase 7**: DTO T042 and test T040 can run in parallel.
- In **Phase 8**: T045 and T046 can be executed concurrently.

---

## Implementation Strategy (MVP First)

1. **MVP Focus**: Deliver **Phase 1**, **Phase 2**, and **Phase 3 (User Story 1)** first. This guarantees a working, demonstrable claim system where users can claim Day 1 (1.0 credit) and advance their streak consecutively, and confirms that logging in does not grant bonus credits.
2. **Incremental Enhancements**:
   - Add missed day handling (**US2**) and 7-day rollover (**US3**).
   - Add same-day duplicate protection (**US4**).
   - Add status query endpoint (**US5**).
3. **Quality Gate**: Every phase is backed by unit tests without Spring context, Testcontainers integration tests, and RestClient E2E tests written in Latin American Spanish.
