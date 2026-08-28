# Market API Contract

## Base URL

`/api/market`

## Endpoints

### GET `/api/market/players`

Returns the current market snapshot for all players.

Response example:

```json
[
  {
    "id": "player-1",
    "name": "Lionel Messi",
    "quotation": 1,
    "totalSupply": 100,
    "operatorInventory": 100,
    "userHoldings": []
  }
]
```

### POST `/api/market/init`

Initializes the market operator and player inventories.

Request body: empty object or no body.

Success response:

```json
{
  "marketOperatorId": "operator",
  "playersInitialized": 1,
  "status": "INITIALIZED"
}
```

### POST `/api/market/users/{userId}/buy`

Purchases player tokens from the market operator.

Request body:

```json
{
  "playerId": "player-1",
  "quantity": 10
}
```

Success response:

```json
{
  "status": "SUCCESS",
  "userId": "user-1",
  "playerId": "player-1",
  "quantity": 10,
  "totalCost": 10
}
```

Failure response:

```json
{
  "status": "REJECTED",
  "reason": "INSUFFICIENT_BALANCE"
}
```

### POST `/api/market/users/{userId}/sell`

Sells player tokens back to the market operator.

Request body:

```json
{
  "playerId": "player-1",
  "quantity": 5
}
```

Success response:

```json
{
  "status": "SUCCESS",
  "userId": "user-1",
  "playerId": "player-1",
  "quantity": 5,
  "totalCredit": 5
}
```

Failure response:

```json
{
  "status": "REJECTED",
  "reason": "INSUFFICIENT_OWNERSHIP"
}
```

## Error handling

- Validation failures return `400 Bad Request` or a domain-specific error response.
- Business rule violations return explicit reasons for the rejection.
- No side effects occur for rejected transactions.

## Invariants enforced by the API

- All player totals remain at 100 tokens.
- Operator inventory and user holdings always reconcile.
- Balance cannot go negative.
- Token quantities cannot be negative or exceed available holdings.
