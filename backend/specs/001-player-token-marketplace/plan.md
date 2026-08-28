# Implementation Plan: Player Token Marketplace

**Branch**: `001-player-token-marketplace` | **Date**: 2026-08-28 | **Spec**: `/specs/001-player-token-marketplace/spec.md`

**Input**: Feature specification from `/specs/001-player-token-marketplace/spec.md`

## Summary

This feature implements a Spring Boot REST backend for a simplified football player token marketplace. The market operator starts with the full 100-token supply for each player, and users can buy/sell token quantities while the system preserves invariants: per-player total supply is always 100, no user can overspend or hold negative balances, and every transaction uses an internal quotation fixed at 1 for this iteration while keeping the design compatible with future fluctuational pricing. The design intentionally uses in-memory state and no persistence layer or database, satisfying the explicit requirement to avoid durable storage for this iteration.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot 4.1.1, Spring WebMVC, Spring Security, springdoc-openapi-starter-webmvc-ui 3.1.0, Lombok, Maven

**Storage**: In-memory application state only; no database, no JPA, no persistence layer for this phase

**Testing**: JUnit 5, Spring Boot test stack, MockMvc for controller-level integration tests, unit tests for domain validation and transaction rules

**Target Platform**: Linux-compatible server runtime (Spring Boot API)

**Project Type**: Web service / REST API backend

**Performance Goals**: Support a small in-memory market (<1000 users, <100 players) with transaction processing in milliseconds and no external services

**Constraints**: No persistent datastore, no user session database, deterministic in-memory state reset between tests, strict invariant checks for token accounting and balances, fixed quotation value of 1 for every player in this version, no authentication or security layer in scope for v1 though Spring Security remains available for future use

**Scale/Scope**: Initial MVP focused on one market operator + multiple users, token trading logic, internal wallet ledger accounting, and REST endpoints for marketplace actions

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Passes under the project constitution:

- Clean Code and Minimal Dependencies: one backend service, domain-driven design, no unnecessary libraries.
- SOLID and Object-Oriented Discipline: clear model boundaries for Player, User, Wallet, MarketOperator, and MarketService.
- Testing Is Non-Negotiable: unit tests for rules and integration tests for HTTP flows are required.
- Java, Spring Boot, and Maven Standards: Spring Boot 4.1.1 + Java 21 + Maven are already aligned with the repository.
- Simplicity Over Conditionals and Over-Engineering: keep state in a service layer and domain-specific validation methods rather than deep abstraction.

No security implementation is required for this initial backend-only simulation; the specification explicitly excludes authentication and authorization from scope.

No constitution violation is introduced by the no-persistence decision because this is an explicit product requirement, not a shortcut; the implementation still preserves deterministic testing and clear accounting rules.

## Project Structure

### Documentation (this feature)

```text
specs/001-player-token-marketplace/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── market-api.md
├── spec.md
└── checklists/requirements.md
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/overcode/
│   ├── config/
│   ├── domain/
│   │   ├── model/
│   │   ├── controller/
│   │   │   ├── dto/
│   │   │   └── exception/
│   │   ├── service/
│   │   └── state/
│   └── DemoApplication.java
├── src/test/java/com/overcode/
│   ├── config/
│   ├── integration/
│   ├── unit/
│   └── support/
├── pom.xml
└── src/main/resources/application.properties
```

**Structure Decision**: This remains a single Spring Boot backend service. The domain logic and HTTP contract are separated into controller, DTO, and service packages; the in-memory market state is contained in a dedicated state/service layer instead of a database abstraction.

## Complexity Tracking

No complexity exceptions required. The no-persistence requirement is intentional and consistent with the feature spec and project constitution.
