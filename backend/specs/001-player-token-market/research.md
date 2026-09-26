# Research: Player Token Market

## Decision

- Use a single Spring Boot backend module with PostgreSQL as the durable store and JPA repositories for persistence.
- Keep the market engine in the service layer with transaction boundaries and atomic updates for credit and token movement.
- Model the market as four primary entities: `Player`, `User`, `Position`, and `Transaction`.
- Keep the initial v1 scope intentionally security-light: no JWT, no OAuth, no session auth, and plain-text user credential storage as explicitly required by the product spec.
- Use eager fetching for all entity relationships to keep portfolio and ledger reads simple and deterministic under the repository’s architecture rules.
- Add a `GlobalExceptionHandler` with a consistent API error contract and custom `ServiceException` / `ModelException` hierarchies for validation and invariant failures.
- Implement a shared `executeTransaction` service method that abstracts the buy/sell flow while preserving each operation’s specific validation rules.
- Separate domain models from persistence concerns: model classes remain ORM-free; JPA annotations and persistence-specific DTOs live in dedicated record-based DTOs under the persistency package.
- Require constructor-based creation for domain objects, with Lombok used for accessors and no-arg constructors only when the framework demands them.

## Rationale

The feature spec already defines the business domain and operational constraints. The repository constitution mandates a strict layered design, deterministic validation, and test-first evidence for both model and integration scenarios. A monolithic Spring Boot service is the most direct fit because the product is a single market domain with a small set of APIs and a PostgreSQL-backed ledger. Atomic trade processing is better enforced in a transactional service than in controller logic or repository layer side effects.

## Alternatives considered

1. In-memory persistence or local file store
   - Rejected because the project requires PostgreSQL, auditing, and integration-test realism under Testcontainers.

2. Domain logic in controllers or repositories
   - Rejected because the constitution forbids domain behavior outside the model/service boundary and requires repository abstraction and validation at the right layer.

3. Separate authentication subsystem and encrypted credentials
   - Rejected because the feature spec explicitly states that v1 does not implement secure authentication or encryption and that plain-text credentials are accepted as a temporary tradeoff.

4. Lazy-loading relationships
   - Rejected because the project requirement states that fetch type must be `EAGER` for all relationships in this feature.

## Outcome

The design resolves the open product decisions by aligning with the constitution, the current stack, and the acceptance tests in the spec. No additional clarification is required before implementation begins.
