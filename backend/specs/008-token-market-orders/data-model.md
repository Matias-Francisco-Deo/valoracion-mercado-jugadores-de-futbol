# Data Model: token-market-orders

## Domain Models

### Portfolio (Position)
Represents a user's total holdings. It holds the user's available credits and a collection of token holdings per player.

**Fields**:
- `id` (UUID): Unique portfolio identifier.
- `userId` (UUID): ID of the user (or super admin) who owns this portfolio.
- `credits` (BigDecimal): Available monetary balance.
- `version` (Long): For optimistic locking.

**Behaviors / Invariants**:
- `hasSufficientCredits(amount)`: Checks if `credits >= amount`.
- `addCredits(amount)`: Increases credit balance.
- `deductCredits(amount)`: Decreases balance. Throws `InsufficientFundsException` if resulting balance < 0 (unless it's the super admin, who is allowed negative/infinite credits if configured).

### TokenHolding
Represents the amount of tokens a portfolio has for a specific player.

**Fields**:
- `portfolioId` (UUID)
- `playerId` (Long)
- `quantity` (Integer)

**Behaviors / Invariants**:
- `hasSufficientTokens(amount)`: Checks if `quantity >= amount`.
- `addTokens(amount)`: Increases quantity.
- `deductTokens(amount)`: Decreases quantity. Throws `InsufficientTokensException` if resulting quantity < 0.

### AuditLog (Trade Event)
Immutable record of an operation.

**Fields**:
- `id` (UUID)
- `operationType` (Enum: `EMISSION`, `BUY`, `SELL`)
- `userId` (UUID): The user performing the action (buyer/seller).
- `counterpartyId` (UUID): The super admin ID.
- `playerId` (Long): The player token involved.
- `tokenAmount` (Integer): Amount of tokens traded.
- `pricePerToken` (BigDecimal): The price at the moment of the transaction.
- `timestamp` (Instant)

**Behaviors**:
- Fully immutable. Once instantiated, no setters provided.

## Persistence Layer (JPA Entities)

### `PortfolioJpaEntity`
- Maps to `portfolio` table.
- `@Version` on the `version` field for optimistic locking.
- One-to-Many relationship with `TokenHoldingJpaEntity`.

### `TokenHoldingJpaEntity`
- Maps to `token_holding` table.
- Composite unique key on `(portfolio_id, player_id)`.

### `AuditLogJpaEntity`
- Maps to `audit_log` table.
- Updatable = false on all columns to enforce immutability at the DB schema level.

## State Transitions
1. **Emission**: New player saved -> `TokenMarketService.emitTokens(playerId)` -> Super Admin Portfolio gains 100 `TokenHolding`, AuditLog(EMISSION) created.
2. **Buy**: `TokenMarketService.buyTokens(userId, playerId, amount, price)` -> User portfolio credits deducted, user holdings increased. Admin holdings decreased, admin credits increased. AuditLog(BUY) created. All in one transaction.
3. **Sell**: `TokenMarketService.sellTokens(userId, playerId, amount, price)` -> User portfolio credits increased, user holdings decreased. Admin holdings increased, admin credits decreased. AuditLog(SELL) created. All in one transaction.
