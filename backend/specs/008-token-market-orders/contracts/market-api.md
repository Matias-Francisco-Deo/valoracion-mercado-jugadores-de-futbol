# Market API Contracts

These contracts define the REST API exposed by the `TokenMarketController`.

## 1. Buy Tokens

**Endpoint**: `POST /orders/buy`
**Authorization**: Bearer Token (Authenticated User)

**Request Body**:
```json
{
  "playerId": 12345,
  "quantity": 10
}
```
*(Note: Price is determined securely on the backend based on current market value, preventing users from spoofing a lower price).*

**Success Response (200 OK)**:
```json
{
  "message": "Tokens successfully purchased",
  "playerId": 12345,
  "quantity": 10,
  "totalPricePaid": 10.0,
  "timestamp": "2026-10-08T10:00:00Z"
}
```

**Error Responses**:
- `400 Bad Request`: Validation errors (e.g., negative quantity).
- `402 Payment Required` (or `422 Unprocessable Entity`): Insufficient funds.
- `404 Not Found`: Player or market not found.
- `409 Conflict`: Insufficient token availability from the counterparty.

---

## 2. Sell Tokens

**Endpoint**: `POST /orders/sell`
**Authorization**: Bearer Token (Authenticated User)

**Request Body**:
```json
{
  "playerId": 12345,
  "quantity": 5
}
```

**Success Response (200 OK)**:
```json
{
  "message": "Tokens successfully sold",
  "playerId": 12345,
  "quantity": 5,
  "totalCreditsReceived": 5.0,
  "timestamp": "2026-10-08T10:05:00Z"
}
```

**Error Responses**:
- `400 Bad Request`: Validation errors.
- `422 Unprocessable Entity`: Insufficient tokens in portfolio.
- `404 Not Found`: Player not found.
