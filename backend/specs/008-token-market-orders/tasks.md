# Implementation Tasks: token-market-orders

**Feature Directory**: `specs/008-token-market-orders`

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization and basic structure. (The project and architecture are already established).

- [x] T001 Create database schema migration (e.g. Flyway/Liquibase) for `portfolio`, `token_holding`, and `audit_log` tables in `src/main/resources/db/migration/VX__market_orders.sql`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core domain models and persistence interfaces that MUST be complete before ANY user story can be implemented.

- [x] T002 [P] Create `Portfolio.java` and `TokenHolding.java` domain models in `src/main/java/com/overcode/model/`
- [x] T003 [P] Create `AuditLog.java` domain model with operation types (EMISSION, BUY, SELL) in `src/main/java/com/overcode/model/`
- [x] T004 Create Unit tests for domain logic (e.g. `hasSufficientCredits`) in `src/test/java/com/overcode/model/PortfolioTest.java`
- [x] T005 [P] Create JPA Entities (`PortfolioJpaEntity.java`, `TokenHoldingJpaEntity.java`, `AuditLogJpaEntity.java`) in `src/main/java/com/overcode/persistence/dto/jpa/`
- [x] T006 [P] Create Repositories (`PortfolioRepository.java`, `AuditLogRepository.java`) in `src/main/java/com/overcode/persistence/repository/`
- [x] T007 Create Integration tests for repositories (verifying optimistic locking) in `src/test/java/com/overcode/persistence/PortfolioRepositoryTest.java`

**Checkpoint**: Foundation ready - user story implementation can now begin in parallel.

---

## Phase 3: User Story 1 - Initial Token Emission (Priority: P1) 🎯 MVP

**Goal**: Automatically issue tokens when a new player is discovered.

**Independent Test**: Can be tested by executing the emission service and verifying the super admin's portfolio and audit logs.

### Tests for User Story 1

- [x] T008 [P] [US1] Create integration test for token emission in `src/test/java/com/overcode/service/TokenEmissionServiceTest.java`

### Implementation for User Story 1

- [x] T009 [US1] Create `TokenEmissionService.java` interface in `src/main/java/com/overcode/service/TokenEmissionService.java`
- [x] T010 [US1] Implement `TokenEmissionServiceImpl.java` in `src/main/java/com/overcode/service/impl/TokenEmissionServiceImpl.java` (listens/handles new player creation and emits tokens to Super Admin)

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently.

---

## Phase 4: User Story 2 - User Buys Tokens (Priority: P1)

**Goal**: Allow users to buy player tokens from the market using their credits.

**Independent Test**: Can be tested via POST `/orders/buy` endpoint and verifying portfolio balances (user and admin) and audit logs.

### Tests for User Story 2

- [x] T011 [P] [US2] Create integration test for buying tokens in `src/test/java/com/overcode/service/TokenMarketServiceBuyTest.java`
- [x] T012 [P] [US2] Create E2E test for POST /orders/buy in `src/test/java/com/overcode/controller/TokenMarketControllerBuyTest.java`

### Implementation for User Story 2

- [x] T013 [P] [US2] Create `BuyTokenRequest.java` DTO in `src/main/java/com/overcode/controller/dto/BuyTokenRequest.java`
- [x] T014 [US2] Create `TokenMarketService.java` interface and implement `buyTokens` in `src/main/java/com/overcode/service/impl/TokenMarketServiceImpl.java`
- [x] T015 [US2] Implement `POST /orders/buy` endpoint in `src/main/java/com/overcode/controller/TokenMarketController.java`

**Checkpoint**: User Story 1 and User Story 2 should both work independently.

---

## Phase 5: User Story 3 - User Sells Tokens (Priority: P2)

**Goal**: Allow users to sell their player tokens back to the market for credits.

**Independent Test**: Can be tested via POST `/orders/sell` endpoint and verifying portfolio balances (user and admin) and audit logs.

### Tests for User Story 3

- [x] T016 [P] [US3] Create integration test for selling tokens in `src/test/java/com/overcode/service/TokenMarketServiceSellTest.java`
- [x] T017 [P] [US3] Create E2E test for POST /orders/sell in `src/test/java/com/overcode/controller/TokenMarketControllerSellTest.java`

### Implementation for User Story 3

- [x] T018 [P] [US3] Create `SellTokenRequest.java` DTO in `src/main/java/com/overcode/controller/dto/SellTokenRequest.java`
- [x] T019 [US3] Implement `sellTokens` method in `src/main/java/com/overcode/service/impl/TokenMarketServiceImpl.java`
- [x] T020 [US3] Implement `POST /orders/sell` endpoint in `src/main/java/com/overcode/controller/TokenMarketController.java`

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Improvements and validations.

- [x] T021 Run `quickstart.md` validation to ensure end-to-end functionality of the market flow.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user stories
- **User Stories (Phase 3+)**: All depend on Foundational phase completion
  - US1 and US2 can proceed in parallel once Phase 2 is done.
  - US3 can proceed in parallel or after US2.
- **Polish (Final Phase)**: Depends on all desired user stories being complete

### Parallel Opportunities

- Foundation JPA Entities, Repositories and Domain models can be drafted in parallel.
- Contract/Integration tests for Buy and Sell operations can be written simultaneously by different developers.
- Buy and Sell DTOs and Controllers can be implemented independently once the `TokenMarketService` contract is agreed upon.

## Implementation Strategy

### MVP First (User Story 1 & 2)
1. Complete Setup and Foundational tasks (T001-T007).
2. Complete US1 (Emission) so that the market has supply (T008-T010).
3. Complete US2 (Buy) so that users can interact with the emitted tokens (T011-T015).
4. **STOP and VALIDATE**: Test US1 and US2.

### Incremental Delivery
1. After MVP, add US3 (Sell) to complete the market lifecycle (T016-T020).
