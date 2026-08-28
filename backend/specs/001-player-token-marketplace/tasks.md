# Tasks: Player Token Marketplace

**Input**: Design documents from `/specs/001-player-token-marketplace/`

**Prerequisites**: plan.md (required), spec.md (required for user stories), research.md, data-model.md, contracts/

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Initialize the Spring Boot backend structure and shared testing configuration for the in-memory marketplace.

- [X] T001 Create backend package structure and source/test directories in `backend/src/main/java/com/overcode` and `backend/src/test/java/com/overcode`
- [X] T002 Configure the Spring Boot Maven project and application properties in `backend/pom.xml` and `backend/src/main/resources/application.properties`
- [X] T003 [P] Configure test dependencies and base test support in `backend/pom.xml` and `backend/src/test/java/com/overcode/support`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Implement the domain model and in-memory market engine that all stories depend on.

- [X] T004 Define the core domain model for players, users, the internal wallet ledger, and the market operator in `backend/src/main/java/com/overcode/domain/model`
- [X] T005 [P] Create the in-memory state holder for market inventory and holdings in `backend/src/main/java/com/overcode/market/state/MarketState.java`
- [X] T006 [P] Implement the transaction validation and invariant rules in `backend/src/main/java/com/overcode/market/service/MarketService.java`
- [X] T007 Create request/response DTOs and API error model in `backend/src/main/java/com/overcode/market/dto` and `backend/src/main/java/com/overcode/controller/exception`
- [X] T008 Implement the REST controller skeleton for market endpoints in `backend/src/main/java/com/overcode/market/controller/MarketController.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel.

---

## Phase 3: User Story 1 - Buy tokens from the market operator (Priority: P1) 🎯 MVP

**Goal**: Allow a user to buy one or more tokens for a player with validation against operator inventory and buyer balance.

**Independent Test**: A user can request a valid token purchase and immediately see both their holdings and the operator inventory updated without violating the 100-token cap.

### Tests for User Story 1

- [X] T009 [P] [US1] Write unit tests for buy validation and balance checks in `backend/src/test/java/com/overcode/unit/MarketServiceBuyTest.java`
- [ ] T010 [P] [US1] Write integration tests for `POST /api/market/users/{userId}/buy` in `backend/src/test/java/com/overcode/integration/MarketBuyIntegrationTest.java`

### Implementation for User Story 1

- [X] T011 [P] [US1] Create the purchase request DTO in `backend/src/main/java/com/overcode/market/dto/BuyTokenRequest.java`
- [X] T012 [P] [US1] Create the purchase response DTO in `backend/src/main/java/com/overcode/market/dto/BuyTokenResponse.java`
- [X] T013 [US1] Implement buy logic and stock/balance updates in `backend/src/main/java/com/overcode/market/service/MarketService.java`
- [X] T014 [US1] Implement the buy endpoint and validation mapping in `backend/src/main/java/com/overcode/market/controller/MarketController.java`
- [ ] T015 [US1] Add buy rejection handling for insufficient balance or unavailable inventory in `backend/src/main/java/com/overcode/controller/exception` and `backend/src/main/java/com/overcode/market/service/MarketService.java`

**Checkpoint**: At this point, User Story 1 should be fully functional and independently testable.

---

## Phase 4: User Story 2 - Sell player tokens back to the market (Priority: P1)

**Goal**: Allow a user to sell previously owned tokens back to the market operator and receive the current quotation value.

**Independent Test**: A user with valid holdings can sell tokens and observe both their holding reduction and balance increase without creating negative ownership.

### Tests for User Story 2

- [ ] T016 [P] [US2] Write unit tests for sell validation and negative ownership prevention in `backend/src/test/java/com/overcode/unit/MarketServiceSellTest.java`
- [ ] T017 [P] [US2] Write integration tests for `POST /api/market/users/{userId}/sell` in `backend/src/test/java/com/overcode/integration/MarketSellIntegrationTest.java`

### Implementation for User Story 2

- [ ] T018 [P] [US2] Create the sell request DTO in `backend/src/main/java/com/overcode/market/dto/SellTokenRequest.java`
- [ ] T019 [P] [US2] Create the sell response DTO in `backend/src/main/java/com/overcode/market/dto/SellTokenResponse.java`
- [ ] T020 [US2] Implement sell logic and operator inventory reconciliation in `backend/src/main/java/com/overcode/market/service/MarketService.java`
- [ ] T021 [US2] Implement the sell endpoint and success/error responses in `backend/src/main/java/com/overcode/market/controller/MarketController.java`
- [ ] T022 [US2] Add rejection handling for insufficient ownership and invalid quantities in `backend/src/main/java/com/overcode/controller/exception` and `backend/src/main/java/com/overcode/market/service/MarketService.java`

**Checkpoint**: At this point, User Stories 1 and 2 should both work independently.

---

## Phase 5: User Story 3 - Market initialization and operator inventory (Priority: P2)

**Goal**: Initialize the market with the market operator holding every player's full 100-token supply and provide a consistent market snapshot.

**Independent Test**: Starting a fresh market results in exactly 100 tokens assigned to the operator for each player with the total inventory remaining consistent.

### Tests for User Story 3

- [ ] T023 [P] [US3] Write unit tests for initialization invariants in `backend/src/test/java/com/overcode/unit/MarketInitializationTest.java`
- [ ] T024 [P] [US3] Write integration tests for `POST /api/market/init` and `GET /api/market/players` in `backend/src/test/java/com/overcode/integration/MarketInitializationIntegrationTest.java`

### Implementation for User Story 3

- [ ] T025 [P] [US3] Create the initialization response DTO in `backend/src/main/java/com/overcode/market/dto/MarketInitializationResponse.java`
- [ ] T026 [P] [US3] Create the player market snapshot DTO in `backend/src/main/java/com/overcode/market/dto/PlayerMarketSnapshot.java`
- [ ] T027 [US3] Implement market bootstrap and player registry initialization in `backend/src/main/java/com/overcode/market/service/MarketService.java`
- [ ] T028 [US3] Implement `GET /api/market/players` and `POST /api/market/init` in `backend/src/main/java/com/overcode/market/controller/MarketController.java`
- [ ] T029 [US3] Ensure invariant checks for total supply and operator inventory remain valid after each operation in `backend/src/main/java/com/overcode/market/service/MarketService.java`

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Final validation across all stories and cleanup of the in-memory market API.

- [ ] T030 [P] Run the full Maven test suite and fix any regressions in `backend/pom.xml` and relevant test classes under `backend/src/test/java`
- [ ] T031 [P] Verify OpenAPI documentation and REST contract consistency in `backend/src/main/java/com/overcode/market/controller/MarketController.java` and `backend/specs/001-player-token-marketplace/contracts/market-api.md`
- [ ] T032 [P] Validate the no-persistence design and in-memory state assumptions against the quickstart guide in `backend/specs/001-player-token-marketplace/quickstart.md`
- [ ] T033 Final review of app configuration and API exposure settings in `backend/src/main/java/com/overcode/config` and `backend/src/main/resources/application.properties`, keeping Spring Security dependency available for future use but without enabling it in this v1 no-security phase

---

## Dependencies & Execution Order

### Phase Dependencies

- Phase 1 Setup: no dependencies
- Phase 2 Foundational: depends on Phase 1 completion
- Phase 3+ User Stories: all depend on Phase 2 completion
- Phase 6 Polish: depends on all desired user stories being complete

### User Story Dependencies

- User Story 1 (buy): depends on foundational market engine and controller setup
- User Story 2 (sell): depends on the same foundational layer and may be implemented in parallel with US1 once the base service is ready
- User Story 3 (initialization): depends on foundational setup and is independently testable

### Parallel Opportunities

- T003 can run in parallel with T001 and T002
- T005, T006, and T007 can be implemented in parallel after Phase 1
- US1 and US2 tests can be written in parallel once foundational tasks are complete
- US3 initialization tasks can proceed in parallel with the buy/sell story tasks if needed

---

## Implementation Strategy

### MVP First

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1 (Buy)
4. Validate the end-to-end buy flow independently
5. Add User Story 2 and User Story 3 as follow-on increments

### Incremental Delivery

1. Setup + Foundational -> market engine ready
2. Buy flow -> verify token purchases
3. Sell flow -> verify liquidity back into user balance
4. Market initialization -> verify startup inventory correctness
5. Polish -> run test suite and API validation

### Suggested MVP Scope

The minimal viable feature for delivery is User Story 1 with the initial market bootstrap and core validation. User Story 2 and User Story 3 are valuable follow-on increments that complete the full marketplace behavior and invariants.
