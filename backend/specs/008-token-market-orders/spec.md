# Feature Specification: token-market-orders

**Feature Branch**: `[008-token-market-orders]`

**Created**: 2026-10-08

**Status**: Draft

**Input**: User description: "Necesito implementar el flujo inicial de asignación de tokens y las operaciones de compra/venta de jugadores para el 'Mercado de tokens' de nuestra aplicación (basada en el documento de visión)..."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Initial Token Emission (Priority: P1)

As the system, I need to automatically issue tokens when a new player is discovered, so that there is an initial supply of tokens available for trading in the market.

**Why this priority**: Without tokens in existence, the market cannot operate. This is the foundation of the feature.

**Independent Test**: Can be fully tested by verifying that saving a new player via the scraper pipeline results in 100 tokens being assigned to the super admin and an initial price of 1 credit.

**Acceptance Scenarios**:

1. **Given** a new player is successfully scraped and persisted in the system, **When** the transaction completes, **Then** 100 tokens for that player are allocated to the super admin's portfolio.
2. **Given** a new player is saved, **When** checking the market, **Then** the initial price of the player's token is set to 1 credit.
3. **Given** a new player is saved, **When** the emission occurs, **Then** an immutable audit record is created detailing the emission.

---

### User Story 2 - User Buys Tokens (Priority: P1)

As a user, I want to buy player tokens from the market using my credits, so that I can build my portfolio and invest in players.

**Why this priority**: Buying tokens is the primary interaction for users in the market.

**Independent Test**: Can be tested by executing a buy order as a normal user and verifying portfolio balances and audit logs.

**Acceptance Scenarios**:

1. **Given** the super admin has available tokens for a player and the user has sufficient credits, **When** the user submits a buy order for N tokens, **Then** the user's credits are deducted by N * current price, their token balance increases by N, the super admin's token balance decreases by N, the super admin's credits increase, and an audit log is created.
2. **Given** the user does not have enough credits, **When** they attempt to buy tokens, **Then** the transaction is rejected with an insufficient funds error and no balances are changed.
3. **Given** the super admin does not have enough tokens, **When** the user attempts to buy more than available, **Then** the transaction is rejected with an insufficient availability error.

---

### User Story 3 - User Sells Tokens (Priority: P2)

As a user, I want to sell my player tokens back to the market to realize profits or cut losses, receiving credits in return.

**Why this priority**: Selling allows users to exit positions, completing the market lifecycle.

**Independent Test**: Can be tested by executing a sell order for a user who holds tokens, verifying they receive credits and lose tokens.

**Acceptance Scenarios**:

1. **Given** a user owns N tokens of a player, **When** they submit a sell order for N tokens, **Then** their token balance decreases by N, their credits increase by N * current price, the super admin's token balance increases by N, and an audit log is created.
2. **Given** a user tries to sell more tokens than they own, **When** they submit the sell order, **Then** the transaction is rejected with an insufficient tokens error.

### Edge Cases

- What happens when a transaction fails midway (e.g., database connection drops)? The system must ensure atomicity; the entire buy/sell process rolls back, leaving balances unchanged.
- What happens if the super admin's credit balance goes negative when buying back tokens from users? The super admin acts as the market maker and can have a negative credit balance (or is assumed to have infinite liquidity).
- What happens if multiple users try to buy the last remaining tokens concurrently? Database locks or optimistic locking must prevent overselling the super admin's token balance.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST automatically issue exactly 100 tokens of a newly persisted player.
- **FR-002**: System MUST assign the 100 emitted tokens to the designated "super admin" user.
- **FR-003**: System MUST set the initial trading price of newly emitted player tokens to exactly 1 credit.
- **FR-004**: System MUST allow users to submit buy orders specifying the player and the amount of tokens.
- **FR-005**: System MUST allow users to submit sell orders specifying the player and the amount of tokens.
- **FR-006**: System MUST validate that the buyer has sufficient credits before executing a buy order.
- **FR-007**: System MUST validate that the seller has sufficient tokens before executing a sell order.
- **FR-008**: System MUST validate that the super admin has sufficient token inventory before executing a user's buy order.
- **FR-009**: System MUST update both the user's and the super admin's portfolios atomically during a trade.
- **FR-010**: System MUST record an immutable audit entry for every emission, buy, and sell operation, capturing the actors, token amount, player, price, and timestamp.
- **FR-011**: System MUST process all buy and sell operations using the player's current market price.

### Key Entities

- **Portfolio / Position**: Represents a user's holdings, including their available credits and the quantities of various player tokens they own.
- **Order / Trade**: Represents the intent and execution of buying or selling tokens.
- **Audit Log**: An immutable record of any transaction that changes token or credit balances.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of newly persisted players automatically generate exactly 100 tokens assigned to the super admin.
- **SC-002**: 100% of successful buy and sell operations atomically update both the buyer's and seller's (super admin's) balances without discrepancies.
- **SC-003**: 100% of transactions generate a corresponding immutable audit log entry.
- **SC-004**: System successfully rejects 100% of orders where the user lacks sufficient funds or tokens, preventing negative balances for regular users.

## Assumptions

- The "super admin" user already exists in the system and can be identified reliably (e.g., via a specific role or well-known ID).
- The super admin acts as the automatic counterparty for all sell orders and has no limit on the amount of credits they can owe/spend to buy back tokens.
- There are no transaction fees applied to buy or sell orders in this initial version.
- User credit funding (deposits) is handled outside the scope of this feature.
- Authentication and authorization are already in place; endpoints will receive the identity of the authenticated user making the request.
