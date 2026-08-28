# Data Model: Player Token Marketplace

## Core entities

### Player

- `id`: string or long
- `name`: string
- `quotation`: BigDecimal, fixed at `1` in this initial version
- `totalSupply`: integer, fixed at 100
- `operatorInventory`: integer
- `holders`: map of userId -> token quantity

Validation rules:
- `totalSupply` MUST equal `100` for every player.
- `operatorInventory + sum(user holdings)` MUST always equal `100`.
- `quotation` MUST remain `1` in this initial version for trade execution.

### User

- `id`: string or long
- `name`: string
- `wallet`: `Wallet`
- `holdings`: map of playerId -> token quantity

Validation rules:
- `wallet.balance` MUST never become negative after a buy or sell operation.
- `holdings[playerId]` MUST never go below zero.

### Wallet

- `id`: string or long
- `ownerId`: string or long
- `balance`: BigDecimal
- `currency`: string, internal market currency label

Validation rules:
- `Wallet` is a backend-only model object and does not represent an external financial account.
- `balance` changes only as a result of buy/sell operations within the market simulation.

### MarketOperator

- `id`: string, fixed privileged account
- `balance`: BigDecimal, may be treated as internal treasury or zero-balance operator ledger
- `inventory`: map of playerId -> token quantity

Validation rules:
- The operator starts with all player tokens in circulation at market initialization.
- Operator inventory decreases on user purchases and increases on user sales.

### MarketTrade

- `id`: string or UUID
- `playerId`: string
- `buyerId`: string
- `sellerId`: string
- `quantity`: integer
- `unitQuotation`: BigDecimal
- `totalValue`: BigDecimal
- `status`: enum (`SUCCESS`, `REJECTED`)
- `reason`: optional string for blocked attempts

Validation rules:
- `quantity` MUST be positive.
- `totalValue` MUST equal `quantity * unitQuotation`.
- If `status = REJECTED`, no state mutation is applied.

## Relationships

- One `MarketOperator` owns the initial supply for each `Player`.
- Many `User` entities can hold token quantities for one or many `Player` records.
- Each `MarketTrade` references exactly one buyer, one seller, and one player.

## State transitions

1. Market initialization
   - Create all players with 100 total supply.
   - Assign all tokens to the operator inventory.
   - Create market operator account.

2. Buy operation
   - Validate buyer balance >= total value.
   - Validate requested quantity <= available inventory.
   - Update buyer holdings and balance.
   - Update operator inventory and treasury.

3. Sell operation
   - Validate seller holdings >= quantity.
   - Update seller holdings and balance.
   - Update operator inventory and treasury.

4. Rejected transaction
   - Leave all balances and holdings unchanged.
   - Return explicit rejection message.
