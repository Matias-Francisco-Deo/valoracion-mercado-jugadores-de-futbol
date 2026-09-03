# Tasks: Player Token Market

Feature: Player Token Market (specs/001-player-token-market)

## Phase 1: Setup (Shared Infrastructure)

- [ ] T001 Create/verify Maven project and pom.xml with Spring Boot 4.1.1 and Java 21 (backend/pom.xml)
- [ ] T002 Initialize primary dependencies: Spring Web, Spring Data JPA, PostgreSQL driver, Spring Security, Lombok, Springdoc OpenAPI, JUnit 5, Testcontainers (backend/pom.xml)
- [ ] T003 [P] Add Testcontainers and integration-test profile configuration (backend/src/test/resources/test-application.properties)
- [ ] T004 Create application.properties template with datasource placeholders (backend/src/main/resources/application.properties)

## Phase 2: Foundational (Blocking Prerequisites)

- [ ] T005 Setup database schema and initial migration file (backend/src/main/resources/db/migration/V1__init.sql)
- [ ] T006 [P] Create Player JPA entity with fields (id,name,currentPrice,totalIssued,positions,transactions) (backend/src/main/java/com/overcode/model/Player.java)
- [ ] T007 [P] Create User JPA entity with fields (id,username,email,password,creditBalance,positions) (backend/src/main/java/com/overcode/model/User.java)
- [ ] T008 [P] Create Position JPA entity with fields (id,user,player,quantity) (backend/src/main/java/com/overcode/model/Position.java)
- [ ] T009 [P] Create Transaction (ledger) JPA entity with immutable fields (backend/src/main/java/com/overcode/model/Transaction.java)
- [ ] T010 [P] Add JPA repository interfaces for Player, User, Position, Transaction (backend/src/main/java/com/overcode/persistency/repository/*Repository.java)
- [ ] T011 Implement DataInitializer to create superuser and seed player catalog (backend/src/main/java/com/overcode/config/DataInitializer.java)
- [ ] T012 Configure Spring transaction management and a service base class (backend/src/main/java/com/overcode/config/TransactionConfig.java)
- [ ] T013 Create base integration test class using Testcontainers and Test profile (backend/src/test/java/com/overcode/integration/AbstractIntegrationTest.java)

## Phase 3: User Story US1 - User Registration & Superuser (Priority: P1)

Goal: Allow creating users and verify superuser holds initial token supply.

Independent test: GET /users/{id} returns profile with creditBalance and positions; superuser exists with 100 tokens per player.

- [ ] T014 [US1] Create UserController with GET /users/{id} and POST /users (backend/src/main/java/com/overcode/controller/UserController.java)
- [ ] T015 [US1] Implement UserService and persistence logic (backend/src/main/java/com/overcode/service/impl/UserServiceImpl.java)
- [ ] T016 [US1] [P] Add DTOs for User creation and response (backend/src/main/java/com/overcode/controller/dto/UserDto.java)
- [ ] T017 [US1] Add unit tests for UserService (backend/src/test/java/com/overcode/service/UserServiceTest.java)
- [ ] T018 [US1] Integration test to validate user registration and superuser initial holdings (backend/src/test/java/com/overcode/integration/UserRegistrationIT.java)

## Phase 4: User Story US2 - Buy Tokens (Priority: P1) — MVP

Goal: Implement atomic buy flow where buyer pays credits, seller transfers tokens, ledger records transaction.

Independent test: POST /orders/buy with valid buyer/seller/player/quantity updates positions, balances, and creates ledger row while preserving token conservation.

- [ ] T019 [US2] Create BuyOrderRequest and TradeResponse DTOs (backend/src/main/java/com/overcode/controller/dto/BuyOrderRequest.java)
- [ ] T020 [US2] Implement OrderController POST /orders/buy endpoint wiring to OrderService (backend/src/main/java/com/overcode/controller/OrderController.java)
- [ ] T021 [US2] Implement OrderService.buyTokens ensuring validation and transactional atomicity (backend/src/main/java/com/overcode/service/impl/OrderServiceImpl.java)
- [ ] T022 [US2] [P] Add repository-level checks or DB constraints to prevent oversell (backend/src/main/java/com/overcode/persistency/repository/* and backend/src/main/resources/db/migration/V1__init.sql)
- [ ] T023 [US2] Integration test for buy flow verifying positions, balances, ledger, and token conservation (backend/src/test/java/com/overcode/integration/BuyFlowIT.java)
- [ ] T024 [US2] Concurrency test simulating competing buys to assert no oversell (backend/src/test/java/com/overcode/integration/ConcurrentBuyIT.java)

## Phase 5: User Story US3 - Sell Tokens (Priority: P2)

Goal: Implement sell flow to transfer tokens from seller to buyer, update balances, and write ledger entry.

Independent test: POST /orders/sell executes correctly and preserves invariants.

- [ ] T025 [US3] Create SellOrderRequest DTO (backend/src/main/java/com/overcode/controller/dto/SellOrderRequest.java)
- [ ] T026 [US3] Implement OrderService.sellTokens with validation and transactional guarantees (backend/src/main/java/com/overcode/service/impl/OrderServiceImpl.java)
- [ ] T027 [US3] Implement controller endpoint POST /orders/sell (backend/src/main/java/com/overcode/controller/OrderController.java)
- [ ] T028 [US3] Integration test for sell flow verifying positions, balances, ledger (backend/src/test/java/com/overcode/integration/SellFlowIT.java)

## Phase 6: User Story US4 - Portfolio & Transaction History (Priority: P3)

Goal: Expose portfolio and ledger endpoints for users.

Independent test: GET /users/{id}/portfolio and /users/{id}/transactions return correct data matching positions and ledger.

- [ ] T029 [US4] Implement Portfolio endpoint GET /users/{id}/portfolio and DTOs (backend/src/main/java/com/overcode/controller/PortfolioController.java)
- [ ] T030 [US4] Implement Transactions endpoint GET /users/{id}/transactions (backend/src/main/java/com/overcode/controller/TransactionController.java)
- [ ] T031 [US4] Integration tests for portfolio and ledger queries (backend/src/test/java/com/overcode/integration/PortfolioIT.java)

## Final Phase: Polish & Cross-Cutting Concerns

- [ ] T032 [P] Add global exception handler and map domain errors to HTTP responses (backend/src/main/java/com/overcode/controller/exception/GlobalExceptionHandler.java)
- [ ] T033 [P] Add logging, OpenAPI annotations and verify springdoc output (backend/src/main/java/com/overcode/config/OpenApiConfig.java)
- [ ] T034 [P] Update quickstart.md and docs with exact startup and test commands (specs/001-player-token-market/quickstart.md)
- [ ] T035 [P] Run full mvnw test and fix failing tests (command: ./mvnw test) (repository root)

---

## Dependencies & Execution Order

- Phase 1 (Setup) → Phase 2 (Foundational) must complete before any user story tasks begin.
- US1 (Registration & Superuser) must finish early because other flows (buy/sell) depend on user records and seeded superuser, but it remains a story phase (US1).
- US2 (Buy) and US3 (Sell) depend on Foundational completion; US2 is recommended MVP (complete first) and can be implemented in parallel with US1 after foundational tasks if team capacity allows.
- Within each story: write failing tests first (unit/integration) → models → services → controllers → integration verification.

## Parallel execution examples

- Create entities Player/User/Position/Transaction (T006-T009) can be implemented in parallel by different developers.
- DTOs, unit tests, and controller wiring per story (T016, T021, T026, etc.) are parallelizable when they touch different files.
- Buy and Sell services (T021,T026) can be independently implemented and tested after foundational tasks are complete.

## Implementation strategy (MVP first)

- MVP scope: US2 (Buy Tokens) + foundational tasks required to run it (entities, repositories, DataInitializer, transaction management). This provides the end-to-end trading loop from superuser to buyer.
- Iterative delivery: finish Setup + Foundational → implement US2 → validate with integration tests → release demo. Then implement US1, US3, US4 in priority order.

---

## Tasks Summary (for completion report)

- Path to generated tasks.md: specs/001-player-token-market/tasks.md
- Total tasks: 35
- Tasks per story:
  - Setup/Foundation (Phases 1–2): 13 tasks (T001–T013)
  - US1 (Registration & Superuser): 5 tasks (T014–T018)
  - US2 (Buy - MVP): 6 tasks (T019–T024)
  - US3 (Sell): 4 tasks (T025–T028)
  - US4 (Portfolio & History): 3 tasks (T029–T031)
  - Polish & Cross-cutting: 3 tasks (T032–T035)
- Parallel opportunities identified: entity creation (T006–T009), DTOs & unit tests (T016, T017, etc.), repository and DB migration work (T005, T022)
- Independent test criteria per story: included at the top of each user story phase (see sections above)
- Suggested MVP scope: US2 (Buy Tokens) with Foundational tasks
- Format validation: All tasks follow the checklist format (- [ ] T### [P?] [US?] Description with exact file path)


