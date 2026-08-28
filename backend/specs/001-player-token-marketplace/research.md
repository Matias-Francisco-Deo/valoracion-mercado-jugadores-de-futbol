# Research: Player Token Marketplace

## Decision

- Use a Spring Boot REST backend with a single in-memory market state managed by domain services.
- Keep market data in memory using `ConcurrentHashMap`-style collections keyed by player and user IDs rather than a database or repository abstraction.
- Model token accounting as immutable transaction validation rules using domain objects and service invariants.
- Set every player's quotation to a fixed value of `1` for this version, simplifying value calculations to `quantity * 1`, while preserving the domain structure for future fluctuational quotations.
- Keep Spring Security in the project dependency set for future use, but do not enable or configure it in this v1 no-security phase.
- Expose endpoints for market initialization, buy, sell, and player list queries.
- Validate with unit tests for business rules and Spring Boot integration tests for HTTP behavior.

## Rationale

The feature specification explicitly demands a market with a super-user operator, token inventory, and balance validation, but it does not describe a persistence requirement. The repository constitution already standardizes on Spring Boot + Java + Maven, and the no-persistence constraint is a clean design decision for this MVP. A memory-backed state keeps the implementation deterministic, easy to test, and consistent with the initial launch scope.

## Alternatives considered

1. Relational database with JPA/Hibernate
   - Pros: persistence, auditability, multi-process continuity.
   - Cons: contradicts explicit no-database requirement and adds complexity not required by the MVP.

2. Static file-based storage
   - Pros: persists data without a database.
   - Cons: more operational complexity, harder concurrency control, less aligned with Spring Boot REST patterns for this project.

3. Pure DTO-only validation without domain service invariants
   - Pros: simpler bootstrap.
   - Cons: fails the requirement to maintain consistent token accounting and auditability; weakens validation and testability.

## Key implementation decisions

- `MarketOperator` is the privileged account that owns 100 tokens for every player at startup.
- Every player is represented by a `Player` record with `id`, `name`, `quotation`, `totalSupply`, and current token inventory data.
- Every user has an account balance and token holdings tracked by player.
- On each buy or sell request, the system reads the latest quotation at processing time and validates available inventory or ownership before mutating state.
- The invariant is enforced as: sum(operatorInventory + userHoldings) = 100 for each player.
- The API layer applies validation responses with clear failure reasons instead of silent rejection.

## Open questions and resolved assumptions

- Market quotation changes are treated as a property on the `Player` record that can be updated by a later administrative operation or a test fixture; the transaction uses the current value at execution time.
- A user purchase may include multiple token quantities in a single request, represented as an order payload with quantity and target player.
- A user may have multiple players and token holdings tracked independently.
- The account balance is treated as a numeric monetary value represented in a simple `BigDecimal`-compatible model for deterministic calculations.
