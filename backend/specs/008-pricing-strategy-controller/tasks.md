# Tasks: Pricing Strategy Controller and End-to-End Test Suite

**Feature Branch**: `008-pricing-strategy-controller`
**Spec Reference**: `specs/008-pricing-strategy-controller/spec.md`
**Plan Reference**: `specs/008-pricing-strategy-controller/plan.md`

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create DTO package structure and define request/response data contracts.

- [ ] T001 Create DTO package structure at `src/main/java/com/overcode/controller/dto/cotizacion`
- [ ] T002 [P] Implement `EstrategiaCotizacionResponseDTO` with factory method `desdeModelo` in `src/main/java/com/overcode/controller/dto/cotizacion/EstrategiaCotizacionResponseDTO.java`
- [ ] T003 [P] Implement `ActualizarFactorEscalaRequestDTO` with positive validation annotations in `src/main/java/com/overcode/controller/dto/cotizacion/ActualizarFactorEscalaRequestDTO.java`
- [ ] T004 [P] Implement `CrearEstrategiaCotizacionRequestDTO` with validation annotations and `aModelo` instantiation in `src/main/java/com/overcode/controller/dto/cotizacion/CrearEstrategiaCotizacionRequestDTO.java`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Configure security routing, exception handling, and E2E test harness required before implementing user stories.

- [ ] T005 Add `@ExceptionHandler(EstrategiaInvalidaException.class)` returning `400 Bad Request` in `src/main/java/com/overcode/controller/exception/GlobalExceptionHandler.java`
- [ ] T006 Update `ApiKeyAuthFilter` to enforce `X-API-KEY` on `/api/admin/estrategias-cotizacion` in `src/main/java/com/overcode/security/ApiKeyAuthFilter.java`
- [ ] T007 Update `SecurityConfig` to authorize `/api/admin/estrategias-cotizacion/**` to pass to `ApiKeyAuthFilter` in `src/main/java/com/overcode/config/SecurityConfig.java`
- [ ] T008 Initialize E2E test harness `EstrategiaCotizacionE2eTest` with random port, `RestClient`, `TestService`, and API key helper in `src/test/java/com/overcode/e2e/EstrategiaCotizacionE2eTest.java`

---

## Phase 3: User Story 1 - Consult Available Pricing Strategies (Priority: P1) 🎯 MVP

**Goal**: Allow authorized callers to retrieve all registered pricing strategies or an empty list when none exist.

**Independent Test**: Request `GET /api/admin/estrategias-cotizacion` with valid `X-API-KEY` and verify `200 OK` returning an empty collection when empty, and an array of strategies with `id`, `valorBase`, and `factorEscala` when populated.

### Tests for User Story 1

> **NOTE: Write these tests FIRST and verify they fail before implementation**

- [ ] T009 [P] [US1] Add E2E tests in Latin American Spanish for listing pricing strategies (`debeRetornarListaVaciaCuandoNoHayEstrategias` and `debeListarTodasLasEstrategiasCuandoExisten`) in `src/test/java/com/overcode/e2e/EstrategiaCotizacionE2eTest.java`

### Implementation for User Story 1

- [ ] T010 [US1] Implement `recuperarTodos()` endpoint (`GET /api/admin/estrategias-cotizacion`) delegating to `EstrategiaCotizacionService` in `src/main/java/com/overcode/controller/EstrategiaCotizacionController.java`

**Checkpoint**: User Story 1 is functional and verifiable independently.

---

## Phase 4: User Story 2 - Select and Activate a Pricing Strategy (Priority: P1)

**Goal**: Allow administrators to designate an existing pricing strategy as the active strategy for platform valuations.

**Independent Test**: Request `POST /api/admin/estrategias-cotizacion/{id}/seleccionar` with an existing strategy ID and verify `200 OK`, confirming active configuration. Verify `404 Not Found` for non-existent IDs.

### Tests for User Story 2

- [ ] T011 [P] [US2] Add E2E tests in Latin American Spanish for strategy selection (`debeSeleccionarEstrategiaExitosamente`, `debeMantenerEstrategiaAlSeleccionarLaMisma`, `debeRetornarNotFoundAlSeleccionarEstrategiaInexistente`, and `debeRetornarBadRequestConIdInvalido`) in `src/test/java/com/overcode/e2e/EstrategiaCotizacionE2eTest.java`

### Implementation for User Story 2

- [ ] T012 [US2] Implement `seleccionarEstrategia()` endpoint (`POST /api/admin/estrategias-cotizacion/{id}/seleccionar`) delegating to `EstrategiaCotizacionService` in `src/main/java/com/overcode/controller/EstrategiaCotizacionController.java`

**Checkpoint**: User Stories 1 and 2 are functional and verifiable independently.

---

## Phase 5: User Story 3 - Register a New Pricing Strategy (Priority: P1)

This phase was wrong, please skip.

---

## Phase 6: User Story 4 - Update Strategy Scale Factor (Priority: P2)

**Goal**: Allow administrators to adjust the scale factor of an existing pricing strategy.

**Independent Test**: Request `PATCH /api/admin/estrategias-cotizacion/{id}` with a new positive scale factor and verify `200 OK` and persistence of the updated value. Verify `400 Bad Request` for scale factor ≤ 0 and `404 Not Found` for non-existent IDs.

### Tests for User Story 4

- [ ] T015 [P] [US4] Add E2E tests in Latin American Spanish for updating scale factor (`debeActualizarFactorEscalaExitosamente`, `debeFallarAlActualizarConFactorEscalaCeroONegativo`, and `debeRetornarNotFoundAlActualizarEstrategiaInexistente`) in `src/test/java/com/overcode/e2e/EstrategiaCotizacionE2eTest.java`

### Implementation for User Story 4

- [ ] T016 [US4] Implement `actualizar()` endpoint (`PATCH /api/admin/estrategias-cotizacion/{id}`) with `@Valid` request body in `src/main/java/com/overcode/controller/EstrategiaCotizacionController.java`

**Checkpoint**: User Stories 1 through 4 are functional and verifiable independently.

---

## Phase 7: User Story 5 - Trigger Bulk Player Valuation Recalculation (Priority: P2)

**Goal**: Allow administrators to trigger bulk recalculation of player market prices using the active pricing strategy.

**Independent Test**: Seed players using `PlayerService`, select an active strategy, request `POST /api/admin/estrategias-cotizacion/cotizar`, and verify `200 OK` with confirmation message `"Cotización de jugadores finalizada exitosamente"`. Verify player prices updated in database and `404 Not Found` when no active strategy is configured.

### Tests for User Story 5

- [ ] T017 [P] [US5] Add E2E tests in Latin American Spanish for player quotation recalculation (`debeCotizarJugadoresYActualizarPreciosDeMercado`, `debeCotizarSinErrorCuandoNoHayJugadores`, and `debeRetornarNotFoundAlCotizarSinEstrategiaActiva`) using `PlayerService` for player seeding in `src/test/java/com/overcode/e2e/EstrategiaCotizacionE2eTest.java`

### Implementation for User Story 5

- [ ] T018 [US5] Implement `cotizarJugadores()` endpoint (`POST /api/admin/estrategias-cotizacion/cotizar`) returning `200 OK` and confirmation text message in `src/main/java/com/overcode/controller/EstrategiaCotizacionController.java`

**Checkpoint**: User Stories 1 through 5 are functional and verifiable independently.

---

## Phase 8: User Story 6 - Access Control and Security Enforcement (Priority: P3)

**Goal**: Verify that all endpoints under `/api/admin/estrategias-cotizacion/**` reject requests lacking a valid `X-API-KEY`.

**Independent Test**: Issue requests to all five endpoints without `X-API-KEY` or with an invalid key and verify `401 Unauthorized` in all cases.

### Tests for User Story 6

- [ ] T019 [P] [US6] Add E2E security tests in Latin American Spanish (`debeRechazarPeticionesSinApiKeyCon401` and `debeRechazarPeticionesConApiKeyInvalidaCon401`) across all strategy endpoints in `src/test/java/com/overcode/e2e/EstrategiaCotizacionE2eTest.java`

**Checkpoint**: All user stories are fully implemented and verified.

---

## Phase 9: Polish & Cross-Cutting Concerns

**Purpose**: Validate build health, test execution, documentation, and verify production enum purity.

- [ ] T020 Run full test suite and verify test execution via `.\mvnw.cmd test -Dtest=EstrategiaCotizacionE2eTest`
- [ ] T021 [P] Add OpenAPI `@Tag` and `@Operation` annotations to `src/main/java/com/overcode/controller/EstrategiaCotizacionController.java`
- [ ] T022 Confirm zero modifications were made to `TipoEstrategiaCotizacion` in `src/main` and execute quickstart validation steps from `specs/008-pricing-strategy-controller/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately.
- **Foundational (Phase 2)**: Depends on Phase 1 — BLOCKS all user story implementations.
- **User Stories (Phase 3 through Phase 8)**: Depend on Foundational (Phase 2) completion.
  - US1 (P1): Can execute immediately after Phase 2.
  - US2 (P1): Can execute immediately after Phase 2.
  - US3 (P1): Can execute immediately after Phase 2.
  - US4 (P2): Depends on strategy existence (US3 or direct persistence) for verification.
  - US5 (P2): Depends on active strategy selection (US2) and player seeding (`PlayerService`).
  - US6 (P3): Validates security across all endpoints.
- **Polish (Phase 9)**: Depends on all user story phases being complete.

### Within Each User Story

- Tests written first and verified failing before endpoint implementation.
- Endpoint added to controller.
- Checkpoint assertion verifying the story works independently.

---

## Parallel Opportunities

```bash
# Phase 1 Parallel tasks:
Task T002: EstrategiaCotizacionResponseDTO.java
Task T003: ActualizarFactorEscalaRequestDTO.java
Task T004: CrearEstrategiaCotizacionRequestDTO.java

# Phase 2 Parallel tasks:
Task T005: GlobalExceptionHandler.java
Task T006: ApiKeyAuthFilter.java
Task T007: SecurityConfig.java
```

---

## Implementation Strategy

### MVP Scope (User Story 1 Only)

1. Complete Phase 1: Setup DTOs
2. Complete Phase 2: Foundational (Filter, Security, Global Exception Handler)
3. Complete Phase 3: User Story 1 (Listing strategies endpoint + tests)
4. Validate MVP: Querying `/api/admin/estrategias-cotizacion` returns `200 OK`.

### Incremental Delivery

1. Setup + Foundational → Security and error handling gates active.
2. US1 → Catalog visibility operational (MVP).
3. US2 → Strategy selection active.
4. US3 → Dynamic strategy registration operational.
5. US4 → Scale factor tuning operational.
6. US5 → Player quotation recalculation operational.
7. US6 → Complete security enforcement verified.
8. Polish → Full test run and quickstart verification.
