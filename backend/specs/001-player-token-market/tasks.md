# Tasks: Player Token Market

Feature: Player Token Market (specs/001-player-token-market)

---

## Phase 1: Setup (Project initialization)

- [X] T001 Ensure backend Maven project and Spring Boot setup in backend/pom.xml (add Spring WebMVC, Spring Data JPA, PostgreSQL, Lombok, Springdoc, Testcontainers)
- [X] T002 Create application configuration for local and test profiles in backend/src/main/resources/application.properties
- [X] T003 Add Testcontainers configuration and dependency placeholders in backend/pom.xml
- [X] T004 Create CI-friendly DB initialization notes in specs/001-player-token-market/quickstart.md
- [X] T005 [P] Add basic README and docs in specs/001-player-token-market/quickstart.md (verify the quickstart examples)

---

## Phase 2: Foundational (Blocking prerequisites)

- [X] T006 Setup domain model package and base classes in backend/src/main/java/com/overcode/model/ (create package and README)
- [X] T007 [P] Create domain model: Player in backend/src/main/java/com/overcode/model/Player.java (id, name, currentPrice, totalIssued, invariants)
- [X] T008 [P] Create domain model: User in backend/src/main/java/com/overcode/model/User.java (id, username, email, password, creditBalance, invariants)
- [X] T009 [P] Create domain model: Position in backend/src/main/java/com/overcode/model/Position.java (userId, playerId, quantity)
- [X] T010 [P] Create domain model: Transaction (ledger) in backend/src/main/java/com/overcode/model/Transaction.java (immutable audit fields)
- [X] T011 [P] Create persistence DTO records in backend/src/main/java/com/overcode/persistency/dto/ (PlayerRecord.java, UserRecord.java, PositionRecord.java, TransactionRecord.java)
- [X] T012 [P] Create JPA repositories interfaces in backend/src/main/java/com/overcode/persistency/repository/ (PlayerRepository.java, UserRepository.java, PositionRepository.java, TransactionRepository.java)
- [X] T013 Implement a DataInitializer to create the superuser and seed players in backend/src/main/java/com/overcode/config/DataInitializer.java
- [X] T014 Create repository integration test base using Testcontainers in src/test/java/com/overcode/integration/RepositoryTestBase.java
- [X] T015 [P] Implement GlobalExceptionHandler in backend/src/main/java/com/overcode/controller/exception/GlobalExceptionHandler.java
- [X] T016 [P] Add common DTOs under backend/src/main/java/com/overcode/controller/dto/ (UserDto.java, PlayerDto.java, PortfolioDto.java, TransactionDto.java)
- [X] T017 Configure application.properties settings for testcontainers in backend/src/test/resources/application-test.properties

---

## Phase 3: User Story 1 - Register user (Priority: P1)  🎯 MVP

Goal: Allow creating users with an initial credit balance (0) and retrieve user profile.

Independent Test: POST /users creates a user; GET /users/{id} returns profile with zero positions and creditBalance = 0.

- [X] T018 [P] [US1] Create UserController with POST /users and GET /users/{id} in backend/src/main/java/com/overcode/controller/UserController.java
- [X] T019 [US1] Implement UserService interface in backend/src/main/java/com/overcode/service/interfaces/UserService.java
- [X] T020 [US1] Implement UserServiceImpl in backend/src/main/java/com/overcode/service/impl/UserServiceImpl.java (validate unique username/email, default creditBalance=0)
- [X] T021 [US1] Add repository-backed User creation flow using UserRepository in backend/src/main/java/com/overcode/persistency/repository/UserRepository.java
- [X] T022 [US1] Add unit tests for User domain invariants in backend/src/test/java/com/overcode/model/UserTest.java
- [X] T023 [US1] Add integration test for user registration and GET /users/{id} in backend/src/test/java/com/overcode/integration/UserRegistrationIT.java (use Testcontainers)

---

## Phase 4: User Story 2 - Buy tokens (Priority: P1)

Goal: Buyer purchases N tokens from a seller (often superuser) using current player price; atomic transfer of tokens and credits; ledger entry created.

Independent Test: POST /orders/buy with valid buyerId, sellerId, playerId, quantity succeeds; buyer positions and balances updated; ledger row created; token conservation holds.

- [X] T024 [US2] Define buy order DTO in backend/src/main/java/com/overcode/controller/dto/BuyOrderRequest.java
- [X] T025 [US2] Implement OrdersController endpoint POST /orders/buy in backend/src/main/java/com/overcode/controller/OrdersController.java
- [X] T026 [US2] Add TradeService interface in backend/src/main/java/com/overcode/service/interfaces/TradeService.java (declare executeTransaction/buy/sell operations)
- [X] T027 [US2] Implement TradeServiceImpl in backend/src/main/java/com/overcode/service/impl/TradeServiceImpl.java (shared executeTransaction flow, validation, atomic updates, ledger creation)
- [X] T028 [US2] Implement PositionRepository-backed updates to positions and balances in backend/src/main/java/com/overcode/persistency/repository/PositionRepository.java
- [X] T029 [US2] Add integration tests for buy flow in backend/src/test/java/com/overcode/integration/BuyFlowIT.java (Testcontainers, assert token conservation and ledger)
- [X] T030 [US2] Add contract test validating the /orders/buy request/response vs specs/001-player-token-market/contracts/market-api.yaml in backend/tests/contract/ (or tests/contract/BuyContractTest.java)

---

## Phase 5: User Story 3 - Sell tokens (Priority: P2)

Goal: Seller sells N tokens to a buyer; atomic transfer and ledger creation.

Independent Test: POST /orders/sell with valid sellerId, buyerId, playerId, quantity succeeds and ledger and positions reflect the change.

- [X] T031 [US3] Define sell order DTO in backend/src/main/java/com/overcode/controller/dto/SellOrderRequest.java
- [X] T032 [US3] Implement POST /orders/sell in backend/src/main/java/com/overcode/controller/OrdersController.java
- [X] T033 [US3] Extend TradeServiceImpl to support sell semantics and additional validations in backend/src/main/java/com/overcode/service/impl/TradeServiceImpl.java
- [X] T034 [US3] Add integration tests for sell flow in backend/src/test/java/com/overcode/integration/SellFlowIT.java (Testcontainers)
- [X] T035 [US3] Add contract test for /orders/sell in backend/tests/contract/SellContractTest.java

---

## Phase 6: User Story 4 - View portfolio & history (Priority: P3)

Goal: Retrieve a user's positions and transaction history via API.

Independent Test: GET /users/{id}/portfolio returns positions and credit; GET /users/{id}/transactions returns ledger entries.

- [X] T036 [US4] Implement GET /users/{id}/portfolio in backend/src/main/java/com/overcode/controller/UserController.java
- [X] T037 [US4] Implement GET /users/{id}/transactions in backend/src/main/java/com/overcode/controller/UserController.java
- [X] T038 [US4] Add service methods to aggregate portfolio and ledger data in backend/src/main/java/com/overcode/service/impl/UserServiceImpl.java
- [X] T039 [US4] Add integration tests validating portfolio and transactions endpoints in backend/src/test/java/com/overcode/integration/PortfolioAndLedgerIT.java

---

## Phase 7: Polish & Cross-Cutting Concerns

- [X] T040 [P] Update specs/001-player-token-market/quickstart.md with exact run and smoke-check commands
- [X] T041 [P] Add API docs generation using springdoc OpenAPI integration in backend/src/main/java/com/overcode/config/OpenApiConfig.java
- [X] T042 [P] Add logging and structured error messages across service layer in backend/src/main/java/com/overcode/service/ (ensure GlobalExceptionHandler maps them)
- [X] T043 [P] Security notice: add a top-level docs/security-note.md documenting plaintext credential risk for v1
- [X] T044 [P] Run and fix integration tests using ./mvnw test and ensure Testcontainers-based tests pass locally

---

## Dependencies & Execution Order

- Setup (Phase 1) → Foundational (Phase 2) → User Stories (Phases 3-6) → Polish (Phase 7)
- User Story execution order (recommended): [US1] Register user (MVP) → [US2] Buy tokens → [US3] Sell tokens → [US4] Portfolio & history
- Within each story: Tests (unit/integration) → Models → Services → Controllers → Integration checks

---

## Parallel opportunities

- Any task marked [P] can run in parallel (different files). Examples: T007/T008/T009/T010/T011/T012 model/persistency work; tests for different stories; documentation tasks.

---

## Implementation strategy

- MVP: Complete Phase 1 + Phase 2 + Phase 3 ([US1] Register user) and validate with integration tests. Then implement [US2] Buy tokens as next priority.
- Incremental: After foundation, implement and validate each user story independently with integration tests using Testcontainers before proceeding.
- Always write the tests that assert the acceptance criteria in spec.md (FR-01..FR-09) and ensure they fail before implementation.

---

## File locations referenced

- Backend Java sources: backend/src/main/java/com/overcode/
- Tests: backend/src/test/java/com/overcode/
- Feature docs: specs/001-player-token-market/

---

Generated by speckit-tasks using spec: specs/001-player-token-market/spec.md

