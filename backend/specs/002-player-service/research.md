# Research: Player Service

## Decision

The player feature will be implemented as a Spring service layer that sits on top of the existing `PlayerRepository` persistence abstraction rather than directly using `PlayerDAOJPA`.

## Rationale

- The current persistence architecture already separates the repository interface from its JPA implementation (`PlayerRepository` + `PlayerRepositoryImpl`), which matches the project constitution.
- The service layer is the correct boundary for create/getById/getAll orchestration and validation.
- The repository contract is already the persistence abstraction used by other services such as `UserServiceImpl`, so the player service should follow the same pattern.
- This keeps controllers free of persistence logic and preserves the layered architecture required by the constitution.

## Alternatives considered

1. Accessing `PlayerDAOJPA` directly from the controller or service.
   - Rejected because it bypasses the repository abstraction and breaks the project architecture.

2. Adding the entire feature as controller logic.
   - Rejected because the requirement explicitly excludes endpoints and calls for a service-only solution.

3. Creating duplicate persistence code instead of extending the repository.
   - Rejected because it would duplicate the data-access boundary and increase maintenance burden.

## Resolved clarifications

- Persistence access must reuse the existing repository, not a DAO directly.
- The database is PostgreSQL running at `jdbc:postgresql://localhost:5432/overcode`.
- The feature is limited to service behavior only; endpoint implementation is out of scope.
- A repository method for listing all players is required because the current interface only supports save and find-by-id.
