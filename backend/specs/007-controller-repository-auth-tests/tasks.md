# Tasks: Controller, Repository, Auth, and JSON Extraction Test Coverage

**Input**: Design documents from `/specs/007-controller-repository-auth-tests/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

**Tests**: The feature explicitly requests boundary-focused tests for controller, repository, security filter, and JSON extraction utility.

**Organization**: Tasks are grouped by user story to preserve independent validation and to keep the implementation aligned with the project constitution.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Confirm the backend test harness and fixture structure before covering the four responsibilities.

- [ ] T001 Create the required test package structure under backend/src/test/java/com/overcode/e2e/, backend/src/test/java/com/overcode/security/, backend/src/test/java/com/overcode/persistence/repository/, and backend/src/test/java/com/overcode/testUtils/
- [ ] T002 [P] Verify backend/pom.xml includes JUnit 5, Spring Boot test support, MockMvc, Mockito, and Testcontainers needed by the targeted suite
- [ ] T003 [P] Add deterministic fixture helpers for HTML payloads and sample player data under backend/src/test/java/com/overcode/testUtils/

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish the shared test scaffolding that all user stories depend on.

**⚠️ CRITICAL**: No user story work can begin until this phase is complete.

- [ ] T004 Configure a reusable Spring Boot test context for the admin controller and filter boundary in backend/src/test/java/com/overcode/e2e/ExternalPlayerControllerE2eTest.java
- [ ] T005 [P] Create the repository test container setup and entity fixture scaffold for backend/src/test/java/com/overcode/persistence/repository/PlayerGameDataRepositoryTest.java
- [ ] T006 [P] Create parser HTML fixtures covering valid, empty, malformed, and fallback-position cases in backend/src/test/java/com/overcode/testUtils/HtmlFixtures.java
- [ ] T007 Add the shared API-key test harness and mocked request setup for backend/src/test/java/com/overcode/security/ApiKeyAuthFilterTest.java

**Checkpoint**: Foundation ready - the boundary tests for controller, repository, security, and parser can now proceed independently.

---

## Phase 3: User Story 1 - Verify the external player admin endpoint behavior (Priority: P1) 🎯 MVP

**Goal**: Validate the admin trigger endpoint at the HTTP boundary while mocking the expensive async scraper workflow.

**Independent Test**: Start the Spring MVC layer, invoke POST /api/admin/players/actualizar-jugadores, and assert the accepted response plus exactly one service call without running the real scraper.

### Tests for User Story 1

- [ ] T008 [P] [US1] Add controller happy-path test for accepted 202 response and single async call in backend/src/test/java/com/overcode/e2e/ExternalPlayerControllerE2eTest.java
- [ ] T009 [P] [US1] Add controller failure-path test for service exception handling in backend/src/test/java/com/overcode/e2e/ExternalPlayerControllerE2eTest.java

### Implementation for User Story 1

- [ ] T010 [US1] Confirm or adjust the admin endpoint contract in backend/src/main/java/com/overcode/controller/ExternalPlayerController.java to return 202 Accepted with the required message and invoke externalPlayerService.actualizarJugadoresAsync() exactly once
- [ ] T011 [US1] Ensure the mocked service path preserves the response body and does not mask exceptions at the controller boundary in backend/src/main/java/com/overcode/controller/ExternalPlayerController.java

**Checkpoint**: At this point, User Story 1 is functionally validated at the HTTP boundary and remains independent from persistence or parser tests.

---

## Phase 4: User Story 2 - Verify persistence behavior for player game data (Priority: P1)

**Goal**: Validate the repository boundary for save and retrieval semantics using the JPA DTO conversion.

**Independent Test**: Create a valid PlayerGameData payload, persist it through PlayerGameDataRepositoryImpl, and verify the repository returns the same values or an empty Optional when absent.

### Tests for User Story 2

- [ ] T012 [P] [US2] Add repository save test asserting model-to-JPA mapping preserves required stats in backend/src/test/java/com/overcode/persistence/repository/PlayerGameDataRepositoryTest.java
- [ ] T013 [P] [US2] Add repository retrieval test for existing and missing records in backend/src/test/java/com/overcode/persistence/repository/PlayerGameDataRepositoryTest.java

### Implementation for User Story 2

- [ ] T014 [US2] Implement the repository save path in backend/src/main/java/com/overcode/persistence/repository/impl/PlayerGameDataRepositoryImpl.java using PlayerGameDataJPADTO.desdeModelo(playerGameData)
- [ ] T015 [US2] Implement the repository retrieval path in backend/src/main/java/com/overcode/persistence/repository/impl/PlayerGameDataRepositoryImpl.java so it returns Optional.empty() when no record exists and existing values when present
- [ ] T016 [US2] Validate that persistence mapping preserves all required fields defined in backend/specs/007-controller-repository-auth-tests/data-model.md for PlayerGameData and PlayerGameDataJPADTO

**Checkpoint**: User Story 2 is independently testable through the repository boundary without depending on the controller or scraper flow.

---

## Phase 5: User Story 3 - Verify API key enforcement for scraper admin routes (Priority: P1)

**Goal**: Secure the admin scraper route by rejecting missing, empty, or invalid API keys while allowing valid keys and unrelated routes through.

**Independent Test**: Run the request through the real filter chain with a mock HttpServletRequest/HttpServletResponse and assert the chain continues or stops based on the header content.

### Tests for User Story 3

- [ ] T017 [P] [US3] Add filter test for missing and invalid X-API-KEY values in backend/src/test/java/com/overcode/security/ApiKeyAuthFilterTest.java
- [ ] T018 [P] [US3] Add filter test for valid key requests to the scraper admin route in backend/src/test/java/com/overcode/security/ApiKeyAuthFilterTest.java
- [ ] T019 [P] [US3] Add filter test ensuring non-scraper routes bypass API-key enforcement in backend/src/test/java/com/overcode/security/ApiKeyAuthFilterTest.java

### Implementation for User Story 3

- [ ] T020 [US3] Confirm or fix the path guard and header validation in backend/src/main/java/com/overcode/security/ApiKeyAuthFilter.java to reject only authorized scraper admin routes with 401 Unauthorized and the exact message
- [ ] T021 [US3] Ensure the filter continues the chain for valid requests and unrelated routes without forcing API-key checks outside /api/admin/players in backend/src/main/java/com/overcode/security/ApiKeyAuthFilter.java

**Checkpoint**: User Story 3 is complete when the admin scraper route is secured and non-admin traffic remains unaffected.

---

## Phase 6: User Story 4 - Verify JSON extraction utilities and position parsing (Priority: P2)

**Goal**: Validate the parser boundary for expected extraction success and controlled failures when HTML is malformed or absent.

**Independent Test**: Feed valid and invalid HTML fixtures into JsonExtractorUtil and assert successful extraction, exception throwing, and null fallback for missing positions.

### Tests for User Story 4

- [ ] T022 [P] [US4] Add success-path parser test for valid require.config.params['args'] payload extraction in backend/src/test/java/com/overcode/persistence/repository/dao/external/scrapper/util/JsonExtractorUtilTest.java
- [ ] T023 [P] [US4] Add failure-path parser test for null, empty, and malformed HTML in backend/src/test/java/com/overcode/persistence/repository/dao/external/scrapper/util/JsonExtractorUtilTest.java
- [ ] T024 [P] [US4] Add position-extraction tests for recognized values and unsupported or absent markers in backend/src/test/java/com/overcode/persistence/repository/dao/external/scrapper/util/JsonExtractorUtilTest.java

### Implementation for User Story 4

- [ ] T025 [US4] Harden backend/src/main/java/com/overcode/persistence/repository/dao/external/scrapper/util/JsonExtractorUtil.java to throw ScraperExtractionException when HTML is null, empty, or missing the JSON block
- [ ] T026 [US4] Ensure the position parser in backend/src/main/java/com/overcode/persistence/repository/dao/external/scrapper/util/JsonExtractorUtil.java normalizes valid metadata and returns null for unsupported or absent markers without crashing

**Checkpoint**: User Story 4 is ready when the utility behaves correctly on both valid and malformed HTML without changing the boundary contract.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Final validation, cleanup, and boundary review across all four test suites.

- [ ] T027 [P] Run the focused validation command from backend/specs/007-controller-repository-auth-tests/quickstart.md: `./mvnw test -Dtest='*ExternalPlayerControllerE2eTest,*PlayerGameDataRepositoryTest,*ApiKeyAuthFilterTest,*JsonExtractorUtilTest'`
- [ ] T028 Review the four targeted test files for naming consistency in Spanish latinoamericano and ensure each test targets exactly one responsibility boundary
- [ ] T029 [P] Confirm the repository, controller, security, and parser assertions remain isolated to their correct layers and no unrelated feature scope was broadened
- [ ] T030 Final cleanup of fixture names, assertions, and comments in backend/src/test/java/com/overcode/ before closing the feature

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion and blocks all user stories
- **User Stories (Phase 3-6)**: Each depends on Foundational completion; they can be executed in parallel when staffing allows
- **Polish (Phase 7)**: Depends on all required user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: No dependency on other stories; validates the controller boundary independently
- **User Story 2 (P1)**: No dependency on other stories; validates repository persistence independently
- **User Story 3 (P1)**: No dependency on other stories; validates filter authorization independently
- **User Story 4 (P2)**: No dependency on other stories; validates parser utility independently

### Parallel Opportunities

- All Setup tasks marked [P] can run in parallel.
- All Foundational tasks marked [P] can run in parallel.
- The happy-path and failure-path tests within each story are independent and can be written together.
- The four user stories are independent and should be implementable in parallel by different contributors if needed.

---

## Parallel Example: User Story 1

```bash
# Controller E2E tasks can be developed in parallel once foundation is ready
Task: "Add controller happy-path test for accepted 202 response and single async call in backend/src/test/java/com/overcode/e2e/ExternalPlayerControllerE2eTest.java"
Task: "Add controller failure-path test for service exception handling in backend/src/test/java/com/overcode/e2e/ExternalPlayerControllerE2eTest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. Validate the endpoint contract independently and stop if the controller boundary fails
5. Proceed to repository, filter, and parser stories only after the controller path is proven

### Incremental Delivery

1. Setup + Foundational -> shared test harness ready
2. User Story 1 -> controller boundary validated
3. User Story 2 -> repository boundary validated
4. User Story 3 -> filter boundary validated
5. User Story 4 -> parser boundary validated
6. Polish -> focused suite passes and no architecture violations remain

### Parallel Team Strategy

With multiple developers:

1. One developer runs the controller E2E setup and tests.
2. One developer handles repository persistence tests.
3. One developer handles security filter tests.
4. One developer handles parser utility tests.
5. Final validation phase checks all targets together.

---

## Notes

- [P] tasks are different files or independent test fixtures with no blocking dependencies.
- [USx] labels map tasks to the exact user story in backend/specs/007-controller-repository-auth-tests/spec.md.
- Each story remains independently testable without relying on a live scraper or real external services.
- The validation command in quickstart.md is the final gate for this feature.
- This feature intentionally stays within the backend service boundaries described in backend/specs/007-controller-repository-auth-tests/plan.md.
