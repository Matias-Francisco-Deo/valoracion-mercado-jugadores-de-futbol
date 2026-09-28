# Tasks: Player and User Controller Test Suite

**Feature**: Player and User Controller Test Suite
**Branch**: `003-player-user-controller-tests`
**Specification**: [`spec.md`](spec.md) | **Plan**: [`plan.md`](plan.md)

---

## Phase 1: Setup (Shared Test Infrastructure)

**Purpose**: Verify dependencies and test harness readiness

- [X] T001 Verify test environment prerequisites and existing test suite baseline with `./mvnw.cmd test`
- [X] T002 Verify database cleanup methods in `src/test/java/com/overcode/testUtils/TestService.java` for users and players

---

## Phase 2: Foundational (Test Class Scaffolding & Fixtures)

**Purpose**: Core test harness skeletons, RestClient setup, and centralized constants

- [X] T003 [P] Create `PlayerControllerTest.java` skeleton with `@SpringBootTest(webEnvironment = RANDOM_PORT)`, `RestClient`, `TestService`, and constant definitions (`PLAYER_NAME`, `NON_EXISTENT_ID`, `MALFORMED_ID`, `INVALID_BEARER_TOKEN`) in `src/test/java/com/overcode/controller/PlayerControllerTest.java`
- [X] T004 [P] Create `UserControllerTest.java` skeleton with `@SpringBootTest(webEnvironment = RANDOM_PORT)`, `RestClient`, `TestService`, and constant definitions (`TEST_USERNAME`, `TEST_EMAIL`, `TEST_PASSWORD`, `NON_EXISTENT_ID`, `MALFORMED_ID`, `INVALID_BEARER_TOKEN`) in `src/test/java/com/overcode/controller/UserControllerTest.java`

**Checkpoint**: Skeletons and authentication helper fixtures ready. User story test implementations can proceed.

---

## Phase 3: User Story 1 - Verify Player Catalog Endpoints (Priority: P1)

**Goal**: Verify happy paths and edge cases for `/players` and `/players/{id}` with authenticated requests.

**Independent Test**: Execute `./mvnw.cmd test -Dtest=PlayerControllerTest` to verify catalog listing and player ID lookup scenarios.

- [X] T005 [US1] Implement test `listarJugadoresConBaseVaciaDevuelveListaVacia` asserting 200 OK and empty list for `GET /players` in `src/test/java/com/overcode/controller/PlayerControllerTest.java`
- [X] T006 [US1] Implement test `listarJugadoresConJugadoresExistentesDevuelveListaCompleta` asserting 200 OK and non-empty list of `PlayerResponseDTO` for `GET /players` in `src/test/java/com/overcode/controller/PlayerControllerTest.java`
- [X] T007 [US1] Implement test `obtenerJugadorPorIdExistenteDevuelveOkConDatosCorrectos` asserting 200 OK and matching `PlayerResponseDTO` for `GET /players/{id}` in `src/test/java/com/overcode/controller/PlayerControllerTest.java`
- [X] T008 [US1] Implement test `obtenerJugadorPorIdInexistenteLanzaNotFound` asserting `HttpClientErrorException.NotFound` for non-existent ID on `GET /players/{id}` in `src/test/java/com/overcode/controller/PlayerControllerTest.java`
- [X] T009 [US1] Implement test `obtenerJugadorConIdInvalidoLanzaBadRequest` asserting `HttpClientErrorException.BadRequest` for non-numeric ID on `GET /players/{id}` in `src/test/java/com/overcode/controller/PlayerControllerTest.java` (annotate with `// TODO SDD TEST FAILURE` if existing framework behavior diverges)

**Checkpoint**: Player catalog happy paths and error boundaries are verified.

---

## Phase 4: User Story 2 - Verify User Profile Endpoints (Priority: P1)

**Goal**: Verify happy paths and edge cases for `GET /users/{id}`, ensuring profile retrieval works across authenticated users and never leaks sensitive credentials.

**Independent Test**: Execute `./mvnw.cmd test -Dtest=UserControllerTest` to verify user profile lookup scenarios.

- [X] T010 [US2] Implement test `obtenerUsuarioPorIdExistenteDevuelveOkConDatosCorrectos` asserting 200 OK and matching `UserResponseDTO` for `GET /users/{id}` in `src/test/java/com/overcode/controller/UserControllerTest.java`
- [X] T011 [US2] Implement test `obtenerUsuarioPorIdNoExponeContrasenaNiCredenciales` asserting that serialized JSON response does not leak password hash for `GET /users/{id}` in `src/test/java/com/overcode/controller/UserControllerTest.java`
- [X] T012 [US2] Implement test `obtenerUsuarioPorIdInexistenteLanzaNotFound` asserting `HttpClientErrorException.NotFound` for non-existent ID on `GET /users/{id}` in `src/test/java/com/overcode/controller/UserControllerTest.java`
- [X] T013 [US2] Implement test `obtenerUsuarioConIdInvalidoLanzaBadRequest` asserting `HttpClientErrorException.BadRequest` for non-numeric ID on `GET /users/{id}` in `src/test/java/com/overcode/controller/UserControllerTest.java` (annotate with `// TODO SDD TEST FAILURE` if existing framework behavior diverges)

**Checkpoint**: User profile retrieval, privacy checks, and error boundaries are verified.

---

## Phase 5: User Story 3 - Verify Access Control and Security Enforcement (Priority: P2)

**Goal**: Verify that player and user endpoints reject unauthenticated and invalidly credentialed requests before reaching business logic.

**Independent Test**: Execute `./mvnw.cmd test -Dtest=PlayerControllerTest,UserControllerTest` to verify 403 Forbidden responses.

- [X] T014 [P] [US3] Implement test `listarJugadoresSinTokenLanzaForbidden` asserting `HttpClientErrorException.Forbidden` for unauthenticated `GET /players` in `src/test/java/com/overcode/controller/PlayerControllerTest.java`
- [X] T015 [P] [US3] Implement test `listarJugadoresConTokenInvalidoLanzaForbidden` asserting `HttpClientErrorException.Forbidden` for invalid token `GET /players` in `src/test/java/com/overcode/controller/PlayerControllerTest.java`
- [X] T016 [P] [US3] Implement test `obtenerJugadorPorIdSinTokenLanzaForbidden` asserting `HttpClientErrorException.Forbidden` for unauthenticated `GET /players/{id}` in `src/test/java/com/overcode/controller/PlayerControllerTest.java`
- [X] T017 [P] [US3] Implement test `obtenerJugadorPorIdConTokenInvalidoLanzaForbidden` asserting `HttpClientErrorException.Forbidden` for invalid token `GET /players/{id}` in `src/test/java/com/overcode/controller/PlayerControllerTest.java`
- [X] T018 [P] [US3] Implement test `obtenerUsuarioPorIdSinTokenLanzaForbidden` asserting `HttpClientErrorException.Forbidden` for unauthenticated `GET /users/{id}` in `src/test/java/com/overcode/controller/UserControllerTest.java`
- [X] T019 [P] [US3] Implement test `obtenerUsuarioPorIdConTokenInvalidoLanzaForbidden` asserting `HttpClientErrorException.Forbidden` for invalid token `GET /users/{id}` in `src/test/java/com/overcode/controller/UserControllerTest.java`

**Checkpoint**: Security boundaries across all covered endpoints are verified.

---

## Phase 6: Polish & Cross-Cutting Verification

**Purpose**: End-to-end regression validation and quality checks

- [X] T020 Run full test suite `./mvnw.cmd test` to ensure zero regressions across existing and new test suites
- [X] T021 Verify that test names strictly follow Latin-American Spanish conventions and assert specific `HttpClientErrorException` subclasses without generic catches in `src/test/java/com/overcode/controller/PlayerControllerTest.java` and `src/test/java/com/overcode/controller/UserControllerTest.java`
- [X] T022 Confirm zero changes were made to production code in `src/main/` via `git status`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately.
- **Foundational (Phase 2)**: Depends on Setup (Phase 1). Creates test file skeletons and fixture constants.
- **User Story 1 (Phase 3)**: Depends on Foundational (Phase 2).
- **User Story 2 (Phase 4)**: Depends on Foundational (Phase 2). Independent of Story 1.
- **User Story 3 (Phase 5)**: Depends on Foundational (Phase 2). Exercises security barriers across both files.
- **Polish (Phase 6)**: Depends on all user story tests being written.

### Parallel Opportunities

- **Phase 2**: `T003` (`PlayerControllerTest`) and `T004` (`UserControllerTest`) can be created in parallel.
- **Phase 3 & Phase 4**: User Story 1 and User Story 2 touch separate test files (`PlayerControllerTest` vs `UserControllerTest`) and can run in parallel.
- **Phase 5**: Security tests within each test file (`T014-T017` in `PlayerControllerTest`, `T018-T019` in `UserControllerTest`) can be written in parallel.

---

## Parallel Example: User Story 1 & User Story 2

```bash
# Developer A working on PlayerControllerTest (User Story 1):
Task: "T005-T009 in src/test/java/com/overcode/controller/PlayerControllerTest.java"

# Developer B working on UserControllerTest (User Story 2):
Task: "T010-T013 in src/test/java/com/overcode/controller/UserControllerTest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1)

1. Complete Phase 1 (Setup) and Phase 2 (Foundational skeletons).
2. Implement Phase 3 (PlayerController happy and edge tests).
3. Validate independently with `./mvnw.cmd test -Dtest=PlayerControllerTest`.

### Incremental Delivery

1. Setup + Foundational -> test skeletons ready.
2. Add User Story 1 -> PlayerController verified (MVP).
3. Add User Story 2 -> UserController verified.
4. Add User Story 3 -> Access control verified across both controllers.
5. Run full test gate (Phase 6) -> build green, zero production changes.
