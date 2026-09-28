# Tasks: Player Service

**Input**: Design documents from `/specs/002-player-service/`

**Prerequisites**: `plan.md`, `spec.md`, `research.md`, `data-model.md`, and `contracts/player-service.md`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Confirm the service boundary and repository contract before implementation begins.

- [X] T001 Review the existing player model and persistence boundary in `backend/src/main/java/com/overcode/model/Player.java`, `backend/src/main/java/com/overcode/persistence/repository/interfaces/PlayerRepository.java`, and `backend/src/main/java/com/overcode/persistence/repository/impl/PlayerRepositoryImpl.java`
- [X] T002 Confirm project conventions for service exceptions and transaction boundaries in `backend/src/main/java/com/overcode/service/exception/` and `backend/src/main/java/com/overcode/service/impl/UserServiceImpl.java`
- [X] T003 [P] Create the player service test scaffold in `backend/src/test/java/com/overcode/service/` for create, get-by-id, and list flows

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish the repository and service contracts required by all user stories.

**Checkpoint**: Foundation ready — user story implementation can now begin.

- [X] T004 Extend `PlayerRepository` with a list-all operation in `backend/src/main/java/com/overcode/persistence/repository/interfaces/PlayerRepository.java`
- [X] T005 Implement the repository list behavior in `backend/src/main/java/com/overcode/persistence/repository/impl/PlayerRepositoryImpl.java`
- [X] T006 Create the player service interface in `backend/src/main/java/com/overcode/service/interfaces/PlayerService.java`

---

## Phase 3: User Story 1 - Register a new player in the catalog (Priority: P1) 🎯 MVP

**Goal**: Allow the system to create a valid player record through the service and ensure invalid data is rejected.

**Independent Test**: Create a valid player with the service and verify it is persisted and retrievable.

### Tests for User Story 1

- [X] T007 [P] [US1] Add a happy-path service test in `backend/src/test/java/com/overcode/service/PlayerServiceTest.java` covering valid creation
- [X] T008 [P] [US1] Add a validation failure test in `backend/src/test/java/com/overcode/service/PlayerServiceTest.java` covering invalid player creation

### Implementation for User Story 1

- [X] T009 [US1] Implement `PlayerServiceImpl.create(Player)` in `backend/src/main/java/com/overcode/service/impl/PlayerServiceImpl.java` with validation (no repeated names) and repository delegation
- [X] T010 [US1] Add creation validation (no repeated names) and domain-specific exceptions in `backend/src/main/java/com/overcode/service/exception/` for invalid player input
- [X] T011 [US1] Verify repository persistence output is mapped back to the `Player` model in `backend/src/main/java/com/overcode/persistence/dto/PlayerJPADTO.java` and `backend/src/main/java/com/overcode/model/Player.java`

**Checkpoint**: User Story 1 should be fully functional and independently testable.

---

## Phase 4: User Story 2 - Retrieve a player by identifier (Priority: P1)

**Goal**: Resolve a single player by id and return a clear not-found result when the record is absent.

**Independent Test**: Persist a player, execute the service lookup by id, and verify both the record match and the missing-record failure.

### Tests for User Story 2

- [X] T012 [P] [US2] Add a successful lookup test in `backend/src/test/java/com/overcode/service/PlayerServiceTest.java`
- [X] T013 [P] [US2] Add a missing-player test in `backend/src/test/java/com/overcode/service/PlayerServiceTest.java`

### Implementation for User Story 2

- [X] T014 [US2] Implement `PlayerServiceImpl.getById(Long)` in `backend/src/main/java/com/overcode/service/impl/PlayerServiceImpl.java` using the repository and `EntidadNoEncontradaException`
- [X] T015 [US2] Ensure lookup failures return the project-standard service exception contract in `backend/src/main/java/com/overcode/service/exception/NotFoundException.java`

**Checkpoint**: User Story 2 should work independently from other stories.

---

## Phase 5: User Story 3 - View the full player list (Priority: P2)

**Goal**: Return the complete list of players from the service without exposing endpoints.

**Independent Test**: Create multiple players and verify the service returns all stored records and an empty collection when there are none.

### Tests for User Story 3

- [X] T016 [P] [US3] Add a list-all success test in `backend/src/test/java/com/overcode/service/PlayerServiceTest.java`
- [X] T017 [P] [US3] Add an empty-list test in `backend/src/test/java/com/overcode/service/PlayerServiceTest.java`

### Implementation for User Story 3

- [X] T018 [US3] Implement `PlayerServiceImpl.getAll()` in `backend/src/main/java/com/overcode/service/impl/PlayerServiceImpl.java` and map repository results to `Player`. Return empty list when there are no players.
- [X] T019 [US3] Confirm repository ordering and empty collection behavior in `backend/src/main/java/com/overcode/persistence/repository/impl/PlayerRepositoryImpl.java`

**Checkpoint**: All player service operations are available and independently testable.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final validation and small cleanup across the player service feature.

- [X] T020 [P] Review `backend/specs/002-player-service/quickstart.md` and confirm the service validation scenarios match the implemented behavior
- [X] T021 Run the targeted backend test suite for player service behavior and confirm the repository/service contracts remain consistent

---

## Dependencies & Execution Order

### Phase dependencies

- Setup (Phase 1): no dependencies
- Foundational (Phase 2): depends on Setup completion
- User Story phases (3-5): depend on Foundational completion
- Polish (Phase 6): depends on all user stories being complete

### User story dependencies

- User Story 1 (P1): no dependencies on other stories
- User Story 2 (P1): depends on Story 1’s persistence and validation flow, but remains independently testable
- User Story 3 (P2): depends on the repository and service foundation and can be tested after Story 1/2 are stable

### Parallel opportunities

- T003, T007, T008, T012, T013, T016, T017 can run in parallel when different test scopes are being written
- The repository and service contract tasks can be split across files without conflicts once the foundation is in place
- Story-level work can proceed in parallel if the team is staffed, though Story 2 and Story 3 should still validate against the same service contract

---

## Parallel Example: User Story 1

```bash
# Run test-first validation for the player creation story
Task: "Add happy-path service test in backend/src/test/java/com/overcode/service/PlayerServiceTest.java"
Task: "Add validation failure test in backend/src/test/java/com/overcode/service/PlayerServiceTest.java"

# Then implement the creation flow
Task: "Implement PlayerServiceImpl.create(Player) in backend/src/main/java/com/overcode/service/impl/PlayerServiceImpl.java"
Task: "Add creation validation and exception handling in backend/src/main/java/com/overcode/service/exception/"
```

---

## Implementation Strategy

### MVP First

1. Complete Phase 1 and Phase 2 to establish the repository/service foundation.
2. Complete User Story 1: player creation and validation.
3. Validate the creation flow independently before moving on.

### Incremental Delivery

1. Add Story 1 (create)
2. Add Story 2 (get by id)
3. Add Story 3 (get all)
4. Run the focused backend tests and document the validation results.

### Parallel Team Strategy

- Developer A: User Story 1 service validation and tests
- Developer B: User Story 2 lookup and not-found handling
- Developer C: User Story 3 listing behavior and repository verification

This keeps each story independently testable while preserving the shared repository/service foundation.

---

## Notes

- All tasks follow the required checklist format: checkbox, task ID, optional [P], and story labels where applicable.
- Exact file paths are included for each task.
- The service remains within the project architecture and does not include controller or endpoint work.
