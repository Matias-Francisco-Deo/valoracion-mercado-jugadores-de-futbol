# Implementation Plan: Player Token Market

**Branch**: 001-player-token-market | **Date**: 2026-09-02 | **Spec**: /specs/001-player-token-market/spec.md

**Input**: Feature specification from `/specs/001-player-token-market/spec.md`

## Summary

Implement a Spring Boot web-service backend for a football player token market in which a single superuser owns the initial token supply, users register with credits and token positions, and every buy/sell operation is validated, executed atomically, and recorded to an immutable ledger. The design follows the repository’s layered architecture, keeps business rules in the domain layer, and enforces transaction safety via PostgreSQL-backed JPA repositories and Testcontainers integration tests.

This plan explicitly includes: a `GlobalExceptionHandler` for API-level error mapping; custom service-layer and model-layer exceptions for business failures; a shared `executeTransaction` flow in the service layer that abstracts buy/sell execution behind the same transaction contract; domain model classes that remain free of persistence annotations and are initialized via constructor-based creation; persistence DTOs implemented as Java `record`s for ORM mapping; and Lombok-based accessors plus no-arg constructors only where required by the framework while avoiding setter-based mutation for newly created domain objects.

## Technical Context

**Language/Version**: Java 21 with Spring Boot 4.1.1

**Primary Dependencies**: Spring WebMVC, Spring Data JPA, PostgreSQL JDBC driver, Spring Security, Springdoc OpenAPI, Lombok, JUnit 5, MockMvc, Testcontainers

**Storage**: PostgreSQL (`jdbc:postgresql://localhost:5432/overcode`) for the local profile; tests use Testcontainers with isolated PostgreSQL containers

**Testing**: JUnit 5 for unit tests, Testcontainers + Spring integration tests for repository/service flows, MockMvc for end-to-end controller validation

**Target Platform**: Web backend running on a local Java server and Docker-enabled developer machines

**Project Type**: Web service / REST API

**Performance Goals**: Support interactive trading for a small market (single-player catalog, up to a few hundred users, 100 tokens per player) with atomic operations completing within acceptable interactive response times and without oversell or negative balances

**Constraints**: No external money integration; integer-only token and credit arithmetic; strict validation before and during trade execution; no JWT/auth layer in v1; all relationships fetched eagerly; domain invariants enforced at model/service boundaries; model classes remain persistence-agnostic; persistence DTOs are database-oriented records; constructor-based object creation is required for all newly created domain objects; error handling uses a global exception handler plus custom service/model exceptions

**Scale/Scope**: Monolithic backend with one market, one superuser, player catalog, user portfolio, and transaction ledger; no separate frontend in this phase

### Architecture decisions

- Global exception handling: controller-facing failures are normalized through `@ControllerAdvice` / `GlobalExceptionHandler`, mapping domain and service exceptions to consistent HTTP responses.
- Custom error taxonomy: `ServiceException` / `ModelException` hierarchy with specific subclasses for validation, not-found, and invariant violations.
- Shared transaction flow: both buy and sell operations delegate to a common `executeTransaction` method in the service layer so pricing, ledger creation, and atomic state updates follow one path.
- Persistence separation: JPA annotations live in persistence DTO records only; domain model classes remain plain Java objects without ORM metadata.
- Lombok + constructor-based creation: domain and aggregate objects use Lombok for getters/setters/no-arg constructors where necessary, but object creation occurs via constructor arguments instead of empty instantiation followed by mutation.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

Pass. The design remains compliant with the constitution:

- Layered architecture: controller -> service -> repository -> JPA/persistence; controllers only expose DTOs and call service methods; repositories are isolated behind interfaces.
- Rich model: trade validation, token conservation, and business invariants remain in model/service domain logic rather than controller code.
- Validation boundaries: request DTOs validate shape and basic sanitization, services validate entity existence and action feasibility, domain objects enforce invariants and throw domain-specific exceptions.
- Testing gate: repository + service + end-to-end API tests will be required; local app runs against PostgreSQL, while tests run under Testcontainers.
- Definition of done: build health and test coverage are required before this feature is considered complete.

## Project Structure

### Documentation (this feature)

```text
specs/001-player-token-market/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
├── contracts/           # Phase 1 output
├── checklist/           # Supporting acceptance checklist
└── spec.md              # Source feature specification
```

### Source Code (repository root)

```text
backend/
├── src/main/java/com/overcode/
│   ├── configuration/
│   ├── controller/
│   │   ├── dto/
│   │   └── exception/
│   ├── model/
│   ├── persistency/
│   │   ├── dao/
│   │   ├── repository/
│   │   └── repository/impl/
│   ├── service/
│   │   ├── exception/
│   │   ├── impl/
│   │   └── interfaces/
│   └── ...
├── src/main/resources/
│   └── application.properties
├── src/test/java/com/overcode/
│   ├── controller/
│   ├── integration/
│   ├── model/
│   ├── persistency/
│   └── service/
├── pom.xml
└── mvnw
```

**Structure Decision**: Use a single backend module with layered Java packages under `com.overcode`, aligned with the constitution’s controller/service/model/persistency separation and the repository’s current Spring Boot structure. Persistence DTOs will live under a dedicated `persistency/dto` package as Java records, while the domain model stays in `model` with no JPA annotations and constructor-based creation patterns.

## Complexity Tracking

No constitution violations require justification; the feature fits within the existing project architecture and testing gate.
