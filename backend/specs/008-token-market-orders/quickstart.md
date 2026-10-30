# Quickstart: Token Market Orders

## Prerequisites
- PostgreSQL running via Testcontainers (or locally).
- The application built and running via `./mvnw spring-boot:run`.
- A valid JWT token for a regular user (`USER_TOKEN`) and the Super Admin configured.

## 1. Verify Emission
1. Trigger a player save (e.g. via the Scraper endpoint or DB script).
2. Query the database to verify the Admin Portfolio:
   ```sql
   SELECT * FROM portfolio p JOIN token_holding t ON p.id = t.portfolio_id WHERE p.user_id = 'SUPER_ADMIN_ID';
   ```
   *Expected Outcome*: A row for the new player with `quantity = 100`.

## 2. Execute a Buy Order
```bash
curl -X POST http://localhost:8080/orders/buy \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"playerId": 1, "quantity": 10}'
```
*Expected Outcome*: 200 OK. The user's credits decrease by 10, their token holding for Player 1 becomes 10.

## 3. Execute a Sell Order
```bash
curl -X POST http://localhost:8080/orders/sell \
  -H "Authorization: Bearer $USER_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"playerId": 1, "quantity": 5}'
```
*Expected Outcome*: 200 OK. The user's credits increase by 5, their token holding for Player 1 decreases to 5.

## 4. Verify Audit Log
Query the database:
```sql
SELECT * FROM audit_log ORDER BY timestamp DESC LIMIT 3;
```
*Expected Outcome*: Records for `EMISSION`, `BUY`, and `SELL` operations exist with correct token amounts and prices.
