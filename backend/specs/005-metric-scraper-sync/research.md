# Phase 0: Research & Clarifications

## Known Technical Context
- **Language**: Java / Spring Boot
- **Database**: PostgreSQL (via Spring Data JPA)
- **Architecture**: Strict Layered Architecture (Controller, Service, Model, Persistence)

## Research Tasks & Findings

*No pending clarifications were identified in the Technical Context.*

### 1. External Scraper Integration Pattern
- **Decision**: The scraper HTTP client and parsing logic will be relocated to the `persistence` layer, acting as a "Repository" of external data.
- **Rationale**: The constitution mandates that the persistence layer exposes a repository abstraction representing the boundary between domain objects and storage/external logic. The external scraper is effectively a read-only external data source.
- **Alternatives considered**: Keeping it in the `service` layer (rejected: violates the constitution's boundary definitions).

### 2. Player Metrics Modeling
- **Decision**: `WeeklyMetrics` will be mapped as `@Embedded` inside the `Player` entity.
- **Rationale**: The user explicitly requested this structural design. It adheres to the Rich Model principle by keeping the metrics tightly coupled to the Player's lifecycle and internal state, allowing `Player.actualizarMetricas(...)` to encapsulate the data mutation safely.
- **Alternatives considered**: Creating a separate `@Entity` for WeeklyMetrics (rejected: adds unnecessary `JOIN` overhead and bidirectional relationship management when metrics are strictly owned by the player).
