# Research: token-market-orders

## Context

The feature requires atomic buy and sell token operations to avoid race conditions and negative balances, along with immutable audit logging. We need to decide on the best approaches for data persistence and locking in Spring Boot / Data JPA to guarantee atomicity and correct auditing.

## 1. Concurrency and Atomicity (Locking)

**Decision**: Use Optimistic Locking (with `@Version` on JPA entities) for `Portfolio` updates, paired with `@Transactional` on the service methods.

**Rationale**: 
- The market might experience concurrent buy/sell requests for the same token or by the same user.
- Optimistic locking (`@Version`) provides a lock-free approach that fails fast via `ObjectOptimisticLockingFailureException` if two transactions modify the same portfolio concurrently.
- This is highly scalable and ensures that no user can double-spend credits or tokens due to a race condition. The service layer can optionally implement a retry mechanism if necessary, though failing fast and letting the client retry is standard for financial-like endpoints.

**Alternatives considered**: 
- **Pessimistic Locking** (`@Lock(LockModeType.PESSIMISTIC_WRITE)`): Requires row-level database locks. While robust, it can lead to lock contention and deadlocks if not carefully managed. Optimistic locking is simpler and performs better under normal loads.

## 2. Immutable Audit Logging

**Decision**: Create an Append-Only `AuditLog` domain model and JPA entity.

**Rationale**:
- Requirements explicitly ask for an immutable audit record for emission, buys, and sells.
- Using a dedicated `AuditLog` entity (which maps to a table with no `UPDATE` or `DELETE` endpoints/repository methods) guarantees basic immutability at the application layer.
- The transaction that updates the portfolio will also insert the `AuditLog` in the same `@Transactional` boundary, ensuring consistency.

**Alternatives considered**: 
- **Hibernate Envers**: Overkill for just tracking specific buy/sell/emission events. Envers is better for entire entity versioning rather than domain-specific event logging.
- **Event Sourcing**: Too complex for the current architecture.

## 3. Super Admin Identification

**Decision**: Identify the Super Admin through a configured well-known UUID or a specific `Role` loaded via a property/service, ensuring that the emission logic and counterparty lookup dynamically find the correct admin portfolio.

**Rationale**: 
- Hardcoding user IDs is an anti-pattern. We'll use a `superadmin.email` or `superadmin.id` property, or a query like `findUserByRole("SUPER_ADMIN")` to locate the counterparty for emissions and trades.
