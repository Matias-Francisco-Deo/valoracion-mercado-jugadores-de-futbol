Feature: Player Token Market
Short name: player-token-market

Overview
Build a player-token trading market where users trade in-system credits for player tokens. A single superuser initially owns all issued tokens (100 per player). Each football player has a per-token price (initially 100). Users may buy and sell tokens between accounts using internal credits. Transactions must validate availability and ownership, update positions and balances atomically, and record immutable ledger entries.

Actors
- Superuser: single privileged account holding initial supply (100 tokens per football player).
- User: registered participant with an in-system credit balance and token positions.
- System (market engine): enforces rules, maintains positions and ledger.

Goals
- Enable users to invest by buying/selling football player tokens using in-system credits.
- Preserve token supply and make all trades auditable.
- Ensure correctness under concurrent operations.

Key business rules
- Each player: total_issued = 100 tokens; current_price default 100 (mutable later).
- Tokens are integer units.
- All trades use the football player current price at transaction time.
- Users may transact with any user (peer-to-peer allowed). However, the superuser starts owning all tokens so until others acquire tokens the superuser is the only seller with inventory.
- Transactions are internal credit transfers (no external money integration in v1).

User scenarios & acceptance tests
1) Register user
- User registers; system creates account with unique id and initial in-system credit balance (default 0).
- Acceptance: GET /users/{id} returns profile with zero positions.

2) Buy tokens (typical flow)
- Precondition: seller_account.tokens[player_id] >= quantity.
- Action: buyer requests purchase of N tokens; system computes cost = N * player.current_price; verifies buyer has sufficient credits; transfers tokens and credits; records transaction.
- Acceptance: buyer position increased by N, seller decreased by N, balances adjusted, ledger record created, sum of tokens for player == 100.

3) Sell tokens (typical flow)
- Precondition: seller.position[player_id] >= quantity.
- Action: seller initiates sale to a chosen buyer (peer or superuser); system computes proceeds = N * player.current_price; transfers tokens and credits; records transaction.
- Acceptance: seller position decreased, buyer increased, balances adjusted, ledger record created, token conservation holds.

4) View portfolio & history
- Users can view positions and transaction history; responses match ledger and positions.

Functional Requirements (testable)
FR-01: Superuser creation
- System initializes a single superuser owning 100 tokens per player. Test: superuser exists and holds 100 tokens for each player.

FR-02: Player model
- Player has id, name, current_price (default 100), total_issued (100). Test: creating players yields those defaults.

FR-03: User registration
- Create users with unique id and initial credit balance (0). Test: GET returns profile.

FR-04: Purchase flow
- Verify seller inventory; verify buyer credits; compute cost using current_price; atomically transfer tokens and credits; create immutable transaction record with fields: id, timestamp, type, buyer_id, seller_id, player_id, quantity, unit_price, total_amount. Test: positions, balances, and ledger match.

FR-05: Sale flow
- Verify seller owns tokens; compute proceeds using current_price; atomically transfer tokens and credits to designated buyer; record transaction. Test: positions, balances, ledger match.

FR-06: Atomicity and consistency
- Trades must be atomic. On any failure, rollback state. Test: simulate partial failure and assert no state change.

FR-07: Audit ledger
- All transactions stored and queryable by user and player.

FR-08: Token conservation
- For each player, sum of tokens across accounts must equal total_issued (100). Test: invariants hold after operations.

FR-09: Validation and errors
- Reject buys if seller lacks tokens or buyer lacks credits; reject sells if seller lacks tokens; invalid quantities rejected. Test: invalid requests return appropriate errors.

Success Criteria
- SC-1: Users can register and complete buy/sell flows end-to-end in manual tests (>=90% pass rate).
- SC-2: After 100 sequential buys/sells, token conservation holds for each player.
- SC-3: 100% of executed trades in tests are recorded and retrievable.
- SC-4: No account ends with negative tokens or negative total supply after tests.
- SC-5: Primary operations complete within interactive user-perceived time.

Key Entities
- Player: {id, name, current_price, total_issued}
- Account (User): {id, username/email, credit_balance, created_at}
- Position: {account_id, player_id, quantity}
- Transaction (ledger): {id, timestamp, type, buyer_id, seller_id, player_id, quantity, unit_price, total_amount, metadata}

Assumptions
- Tokens are integer (no fractions).
- Currency is in-system credits only.
- Initial player price is 100; price updates are supported later but out of initial scope.
- Superuser is single initial owner of all tokens; peer-to-peer transfers are allowed once users hold tokens.
- Initial user credit balance is 0; credits must be provisioned by system/admin for users to buy.
- No authentication or encryption: The system will not implement any authentication mechanisms (no JWT, sessions, OAuth, or similar) and will not use encryption for stored data or in-transit communications in this initial phase. User records will include email and password fields stored plainly (e.g., unencrypted/plaintext) and the system will not enforce credential-based authentication — users operate without authentication controls.
- Risk: This intentionally omits security controls and encryption; treat this as an internal/test-phase system only. Document and accept associated risks before any production deployment.

Edge cases & concurrency
- Concurrent buys must not allow oversell: enforce DB constraints or serialization.
- Attempts to trade more tokens than available must be rejected.
- Attempts to sell more than owned rejected.
- Zero or negative quantities rejected.

Acceptance criteria summary
- All functional requirements have concrete acceptance tests above.
- Two clarifications resolved: peer-to-peer allowed (but superuser initially holds all tokens), tokens are integer-only.

Audit & Testing guidance
- Unit tests for validation and business rules.
- Integration tests for end-to-end buy/sell and ledger integrity.
- Concurrency tests simulating competing buys to ensure no oversell.

Status
- Spec finalized with clarifications applied.
