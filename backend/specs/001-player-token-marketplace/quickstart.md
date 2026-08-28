# Quickstart: Market validation and test execution

## Prerequisites

- Java 21
- Maven 3.9+
- Project root: `backend/`

## Run the test suite

```bash
cd backend
mvn test
```

## Validate core scenarios

1. Initialize market
   - Start the Spring Boot application.
   - Call the market initialization endpoint.
   - Verify the market operator owns exactly 100 tokens for each player.

2. Buy tokens
   - Create or identify a user with sufficient balance.
   - Submit a buy request for a positive quantity.
   - Because the quotation is fixed at `1`, the total cost equals the quantity requested.
   - Confirm the user's holdings increase and the operator inventory decreases.

3. Sell tokens
   - Submit a sell request for tokens the user already owns.
   - Confirm the user's balance increases and holdings decrease.

4. Rejection path
   - Try to buy or sell beyond limit.
   - Confirm a clear error response and no state mutation.

## Recommended verification commands

```bash
cd backend
mvn test -Dtest='*Unit*'
cd backend
mvn test -Dtest='*Integration*'
```

## Expected outcomes

- All token totals remain within the 100-token cap.
- User balances never become negative.
- Operator inventory matches the initial state minus successful sales.
- HTTP responses are deterministic and descriptive for invalid operations.
