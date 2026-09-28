# Data Model: Player Token Market

## Domain model vs persistence DTOs

The project will keep two parallel representations for each aggregate:

- Domain model objects live under `model/` and contain business logic, validation, and invariants. They are not annotated with JPA or persistence metadata.
- Persistence DTOs live under `persistency/dto/` and are implemented as Java `record`s. Those records carry the database mapping, column metadata, and repository conversion logic.
- Domain object creation is constructor-driven: any new `User`, `Player`, or `Transaction` is instantiated with its required values in the constructor instead of creating empty objects and mutating fields afterward.
- Lombok is used for accessors and framework-friendly constructors, but object mutation is intentionally minimized; domain state changes occur through explicit business methods and transaction execution rather than ad hoc setters.

## Core entities

### Player

| Field | Type | Constraints | Notes |
|---|---|---|---|
| id | Long | PK, generated | Unique player identifier |
| name | String | not blank | Football player display name |
| currentPrice | Integer | > 0, default 100 | Price used for trade calculations |
| totalIssued | Integer | fixed at 100 | Initial token supply per player |
| positions | Set<Position> | EAGER | Positions held across users |
| transactions | Set<Transaction> | EAGER | Ledger entries referencing the player |

Rules:
- Each player is initialized with `currentPrice = 100` and `totalIssued = 100`.
- Price is mutable by later feature work but remains positive in v1.
- Token conservation must hold at all times across users for each player (`sum(user positions) = totalIssued`).

### User

| Field | Type | Constraints | Notes |
|---|---|---|---|
| id | Long | PK, generated | Unique user identifier |
| username | String | not blank, unique | Login handle in v1 |
| email | String | not blank, unique | Stored as plain text in v1 |
| password | String | not blank | Plain-text storage by explicit feature requirement |
| creditBalance | Integer | >= 0, default 0 | In-system credits|
| positions | Set<Position> | EAGER | Player-position inventory |
| buyerTransactions | Set<Transaction> | EAGER | Transactions where user is buyer |
| sellerTransactions | Set<Transaction> | EAGER | Transactions where user is seller |

Rules:
- New users start with zero credits.
- Credit balance may never be negative.
- A user can have zero or many positions for various players.

### Position

| Field | Type | Constraints | Notes |
|---|---|---|---|
| id | Long | PK, generated | Position row identity |
| user | User | not null | Parent user |
| player | Player | not null | Parent player |
| quantity | Integer | >= 0 | Tokens held by the user |

Rules:
- One user may hold at most one position row per player.
- `quantity` must remain non-negative.
- A trade updates the two related position rows atomically.

### Transaction

| Field | Type | Constraints | Notes |
|---|---|---|---|
| id | Long | PK, generated | Immutable ledger entry |
| timestamp | Instant | not null | Execution timestamp |
| type | String | BUY or SELL | Trade direction |
| buyer | User | nullable for system-driven issuance | Buyer account |
| seller | User | nullable for system-driven issuance | Seller account |
| player | Player | not null | Player affected |
| quantity | Integer | > 0 | Token quantity bought/sold |
| unitPrice | Integer | > 0 | Price at transaction time |
| totalAmount | Integer | > 0 | `quantity * unitPrice` |
| metadata | String | optional | Audit details if needed |

Rules:
- Transaction rows are immutable once created.
- All trades create one ledger entry even if the buyer or seller is the initial superuser.
- `totalAmount` and `unitPrice` reflect the price at the instant of execution.

## Relationships

- `User` 1:N `Position` and `User` 1:N `Transaction` (buyer/seller sides)
- `Player` 1:N `Position` and `Player` 1:N `Transaction`
- All association fetches must be `EAGER` per project requirement

## Validation rules

- `quantity > 0`
- `unitPrice > 0`
- `buyer.creditBalance >= quantity * unitPrice` during buy
- `seller position quantity >= quantity` for a sell
- `buyer != seller` or allow same-user self-trade only if explicitly disallowed in future requirement
- `sum(Position.quantity by player) == Player.totalIssued`
- No negative balances or negative positions
- Domain and service validation errors are represented via custom `ModelException` and `ServiceException` subclasses, not generic runtime exceptions.
- Controller responses are normalized through a `GlobalExceptionHandler` so all invalid requests and invariant failures return stable API payloads.

## State transitions

### User registration
- New `User` created with `creditBalance = 0` and no positions.

### Trade execution
1. Validate buyer/seller exist and player exists.
2. Check buyer funds and seller inventory.
3. Compute `totalAmount = quantity * currentPrice`.
4. Route both buy and sell flows through a shared service method: `executeTransaction(...)`.
5. Update both user positions and balances in one transaction.
6. Persist ledger row.
7. Commit only if all checks pass; otherwise roll back.

### Failure handling
- Any validation or persistence exception must trigger rollback and leave all balances/positions unchanged.

## Persistence notes

- Use repository interfaces for all database access.
- Model classes use Lombok for constructors/getters/setters as required by the constitution.
- Repository layer may expose JPA entities but must not contain business rules.
