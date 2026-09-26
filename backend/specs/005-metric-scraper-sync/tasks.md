# Implementation Tasks: Metric Scraper Synchronization

**Branch**: `005-metric-scraper-sync` | **Date**: 2026-09-19 | **Spec**: [spec.md](./spec.md) | **Plan**: [plan.md](./plan.md)

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure

- [x] T001 Verify existing project structure and testing dependencies (JUnit 5, Testcontainers)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core architectural compliance that MUST be complete before the user story can be implemented (Tarea 1 of Plan).

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [x] T002 Move Scraper HTTP client and data adapters to the `persistence` layer in `src/main/java/com/dapp/valoracionmercadojugadoresdefutbol/persistence/scraper/`
- [x] T003 Move Scraper domain/business services to `service.impl` in `src/main/java/com/dapp/valoracionmercadojugadoresdefutbol/service/impl/`

**Checkpoint**: Foundation ready - architectural boundaries are clean and ready for orchestration.

---

## Phase 3: User Story 1 - Manual Synchronization (Priority: P1) 🎯 MVP

**Goal**: A system administrator triggers the synchronization process to fetch the latest metrics from the external scraper, updating all player profiles and persisting the data.

**Independent Test**: Trigger `POST /players/sync-metrics` and verify via Database or GET endpoint that the `WeeklyMetrics` for existing players have been updated correctly with external data.

### Tests for User Story 1 ⚠️

> **NOTE: Write these tests FIRST, ensure they FAIL before implementation (Constitution IV)**

- [x] T004 [P] [US1] Write unit tests for `Player.actualizarMetricas` in `src/test/java/com/dapp/valoracionmercadojugadoresdefutbol/model/PlayerTest.java`
- [x] T005 [P] [US1] Write integration test for the synchronization orchestrator in `src/test/java/com/dapp/valoracionmercadojugadoresdefutbol/service/PlayerServiceIntegrationTest.java` (using Testcontainers)
- [x] T006 [P] [US1] Write API test for the trigger endpoint in `src/test/java/com/dapp/valoracionmercadojugadoresdefutbol/controller/PlayerControllerTest.java` (using RestClient/@SpringBootTest)

### Implementation for User Story 1

- [x] T007 [P] [US1] Create `@Embeddable WeeklyMetrics` model in `src/main/java/com/dapp/valoracionmercadojugadoresdefutbol/model/WeeklyMetrics.java`
- [x] T008 [US1] Update `Player` entity with `clubName`, `@Embedded metrics`, and `actualizarMetricas()` logic in `src/main/java/com/dapp/valoracionmercadojugadoresdefutbol/model/Player.java`
- [x] T009 [US1] Implement `sincronizarMetricas()` batch orchestration logic in `src/main/java/com/dapp/valoracionmercadojugadoresdefutbol/service/impl/PlayerServiceImpl.java`
- [x] T010 [US1] Expose `POST /players/sync-metrics` endpoint in `src/main/java/com/dapp/valoracionmercadojugadoresdefutbol/controller/PlayerController.java`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase N: Polish & Cross-Cutting Concerns

**Purpose**: Improvements that affect multiple user stories

- [x] T011 Run `quickstart.md` validation scenario locally to verify end-to-end functionality

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately.
- **Foundational (Phase 2)**: Depends on Phase 1 - BLOCKS the user story (must fix architecture first).
- **User Stories (Phase 3)**: Depends on Foundational phase completion.
- **Polish (Final Phase)**: Depends on all desired user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories.

### Within Each User Story

- Tests MUST be written and FAIL before implementation.
- Models (T007, T008) before services (T009).
- Services (T009) before endpoints (T010).
- Core implementation before integration.

### Parallel Opportunities

- All tests for US1 (T004, T005, T006) can be written in parallel.
- Creation of `WeeklyMetrics` (T007) can be done in parallel with testing tasks.

---

## Parallel Example: User Story 1

```bash
# Launch all tests for User Story 1 together:
Task: "Write unit tests for Player.actualizarMetricas" (T004)
Task: "Write integration test for the synchronization orchestrator" (T005)
Task: "Write API test for the trigger endpoint" (T006)
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (CRITICAL - fixes scraper architecture)
3. Complete Phase 3: User Story 1 (Adds Model, Service, Endpoint)
4. **STOP and VALIDATE**: Test User Story 1 independently using quickstart.
5. Deploy/demo if ready.

## Notes

- `[P]` tasks = different files, no dependencies.
- `[US1]` label maps task to specific user story for traceability.
- Verify tests fail before implementing.
- Commit after each task or logical group.
