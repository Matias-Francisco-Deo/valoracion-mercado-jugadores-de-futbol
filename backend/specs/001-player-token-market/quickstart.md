# Quickstart Validation Guide

## Prerequisites

- Docker Desktop is running.
- PostgreSQL is available on `jdbc:postgresql://localhost:5432/`.
- Local database `overcode` exists.
- Local profile uses the provided credentials: `postgres` / `root`.
- Tests run against Testcontainers and do not use the local profile.

## Local setup

1. Create or confirm the PostgreSQL database:
   ```sql
   CREATE DATABASE overcode;
   ```
2. Ensure the app configuration matches:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/overcode
   spring.datasource.username=postgres
   spring.datasource.password=root
   ```
3. Start the backend:
   ```bash
   cd backend
   ./mvnw spring-boot:run
   ```

## API smoke checks

1. List players:
   ```bash
   curl http://localhost:8080/players
   ```
2. Get player details:
   ```bash
   curl http://localhost:8080/players/1
   ```
3. Buy tokens:
   ```bash
   curl -X POST http://localhost:8080/orders/buy \
     -H "Content-Type: application/json" \
     -d '{"buyerId": 2, "sellerId": 1, "playerId": 1, "quantity": 5}'
   ```
4. Sell tokens:
   ```bash
   curl -X POST http://localhost:8080/orders/sell \
     -H "Content-Type: application/json" \
     -d '{"sellerId": 2, "buyerId": 1, "playerId": 1, "quantity": 2}'
   ```
5. Check portfolio and ledger:
   ```bash
   curl http://localhost:8080/users/2/portfolio
   curl http://localhost:8080/users/2/transactions
   ```

## Test validation

Run the project test suite:
```bash
cd backend
./mvnw test
```

Expected outcomes:
- All unit tests pass.
- Repository and service tests using Testcontainers pass.
- API contract tests validate end-to-end trade operations.
- Token conservation and no-negative-balance invariants remain true.

## Relevant design docs

- `specs/001-player-token-market/spec.md`
- `specs/001-player-token-market/data-model.md`
- `specs/001-player-token-market/contracts/market-api.yaml`
