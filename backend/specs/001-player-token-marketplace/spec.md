# Feature Specification: Player Token Marketplace

**Feature Branch**: `001-player-token-marketplace`

**Created**: 2026-08-27

**Status**: Draft

**Input**: User description: "initial model setup - we are going to develop a backend with a API REST suit that integrates football players under a defined market price (quotation). The users will be able to buy and sell tokens of each player in relation to the quotations each have. Each player will have 100 tokens and the users can buy multiple of them at the same time. There will be a super-user that will have all the tokens at the beginning, and the users will buy from him at first."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Buy player tokens from the market operator (Priority: P1)

A registered user wants to acquire ownership in a football player by purchasing player tokens at the current quotation. The user can choose one or more tokens for a player and complete the purchase through the market system without manual coordination.

**Why this priority**: This is the core value of the product and the initial marketplace flow. Without this path, users cannot enter the market or create the liquidity needed for the rest of the system.

**Independent Test**: A user can open the market, select a player, choose a quantity of tokens, confirm the purchase, and see the new ownership reflected in their account while the market operator inventory decreases accordingly.

**Acceptance Scenarios**:

1. **Given** the market operator holds the full token supply for a player, **When** a user purchases a valid quantity of tokens at the current quotation, **Then** the user gains that ownership and the operator's remaining inventory is reduced appropriately.
2. **Given** the user does not have enough balance or the requested token quantity exceeds available inventory, **When** they attempt the purchase, **Then** the system rejects the action and explains the reason clearly.

---

### User Story 2 - Sell player tokens back to the market (Priority: P1)

A user who holds player tokens wants to liquidate part or all of their position by selling back to the market operator at the current quotation. The transaction must reduce the user's holdings and increase their available balance.

**Why this priority**: Selling is the second half of the market cycle and is necessary for capital movement, user trust, and sustainable liquidity.

**Independent Test**: A user with existing holdings can sell a valid quantity of tokens and confirm that their balance increases and their token count decreases.

**Acceptance Scenarios**:

1. **Given** a user owns at least the requested quantity of a player's tokens, **When** they sell those tokens, **Then** the system updates the user's account, removes the sold tokens from their holdings, and transfers the corresponding value back to the user.
2. **Given** a user attempts to sell more tokens than they own, **When** the sale is processed, **Then** the system blocks the transaction and prevents negative holdings.

---

### User Story 3 - Market operator opens the market with a controlled initial supply (Priority: P2)

A market operator must start the marketplace with all player tokens in circulation and maintain the initial inventory until users begin trading. This provides a trusted starting point and ensures the market has a stable supply from the beginning.

**Why this priority**: The initial launch model defines the market structure and protects the system from invalid initial token state.

**Independent Test**: When the market is initialized, every player has exactly 100 tokens assigned to the operator and no token is unaccounted for.

**Acceptance Scenarios**:

1. **Given** a new market is created, **When** the operator initializes player inventory, **Then** each player has exactly 100 tokens available in the system.
2. **Given** a user purchases tokens from the operator, **When** the transaction completes, **Then** the operator's available inventory reflects the remaining supply and the total supply remains consistent.

---

### Edge Cases

- What happens when a user attempts to buy more tokens than the market operator currently has available?
- How does the system handle a purchase where the user requests multiple token quantities across different players in one action?
- What happens when the market quotation changes between the time a user views a player and the time a transaction is submitted?
- What happens when a user attempts to sell tokens for a player that they do not own?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST define each football player as a tracked asset with a current market quotation and a total token supply of 100.
- **FR-002**: The system MUST initialize the market with a dedicated super-user, hereafter called the market operator, holding the entire initial token supply for every player.
- **FR-003**: Users MUST be able to purchase multiple tokens for a player in a single transaction, provided the requested quantity is available and the buyer can cover the total value.
- **FR-004**: Users MUST be able to sell one or more tokens they already own back to the market operator at the current quotation.
- **FR-005**: The system MUST prevent negative token holdings and reject any transaction that exceeds the available inventory or a user's ownership.
- **FR-006**: The system MUST keep token ownership records accurate for each user and each player so that balances can be audited at any time.
- **FR-007**: The system MUST maintain a consistent total token count for each player, ensuring the sum of operator inventory and all user holdings remains equal to 100.
- **FR-008**: The system MUST calculate purchase and sale values using the player's current quotation at the moment the transaction is processed. For this initial version, every player's quotation value is fixed at `1` for all transactions, though the system is designed so this value can later become fluctuational without changing the transaction model.
- **FR-009**: The system MUST support an internal model wallet object associated with each user, used as the value ledger for purchases and sales. This wallet is a backend-only accounting object and is not connected to any external real wallet, payment provider, or bank transaction.
- **FR-010**: The system MUST provide clear transaction outcomes for successful operations and blocked attempts, including reasons for rejection when applicable.
- **FR-011**: The initial backend version MUST NOT implement authentication, authorization, or any external security layer; all marketplace flows are intended to operate as a backend-only in-memory simulation without live user identity enforcement. This design decision is temporary and does not preclude adding Spring Security later as the product evolves.

### Key Entities

- **Player**: Represents a football player available in the market, including their identity and current quotation.
- **User**: Represents a registered participant who can buy and sell tokens and maintain a value balance.
- **Market Operator**: Represents the privileged super-user that holds the initial token inventory and acts as the primary counterparty for early trades.
- **Player Token**: Represents one unit of ownership in a player, with each player capped at 100 total tokens.
- **Token Holding**: Represents the quantity of a player's tokens owned by a user or the operator.
- **Market Quotation**: Represents the market value assigned to a player at a given point in time and used to determine trade totals.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can complete a valid purchase or sale of player tokens in under 2 minutes from the moment they initiate the action.
- **SC-002**: 100% of token transactions preserve valid ownership totals and never create negative holdings or token counts above the 100-token cap per player.
- **SC-003**: At least 95% of transaction attempts result in correct balance and token updates on the first attempt without manual reconciliation.
- **SC-004**: The seller and buyer both see accurate ownership and balance changes immediately after each completed transaction.
- **SC-005**: The market operator starts with the full initial inventory for every player and remains the primary liquidity source until other users participate.

## Assumptions

- Users have an internal model wallet object that acts as the value ledger for settlements within the backend market simulation; it is not tied to a real external wallet or bank account.
- For the initial version, the quotation is fixed at `1` to keep the marketplace simple and reduce implementation complexity, but the design remains compatible with future fluctuational quotation logic.
- The market operator is a privileged internal account, not a regular user account.
- Each player is assigned a fixed total supply of 100 tokens at market creation and remains capped at that amount.
- The initial product scope focuses on token trading mechanics and market initialization rather than advanced analytics, social trading, or external market data feeds.
