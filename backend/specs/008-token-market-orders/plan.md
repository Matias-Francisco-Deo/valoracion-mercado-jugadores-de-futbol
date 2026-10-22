# Implementation Plan: token-market-orders

**Branch**: `[008-token-market-orders]` | **Date**: 2026-10-08 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `specs/008-token-market-orders/spec.md`

## Summary

Implement initial token allocation (emission of 100 tokens per new player) and the buy/sell token marketplace operations between users and the super admin, enforcing strict atomic operations, layered architecture, and immutable auditing.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: Spring Boot (Web, Data JPA, Security)

**Storage**: PostgreSQL

**Testing**: JUnit 5, Testcontainers for integration tests, @SpringBootTest with RestClient for E2E.

**Target Platform**: Web API

**Project Type**: Web Service

**Performance Goals**: Fast atomic transactions for buys/sells to avoid race conditions.

**Constraints**: Strict compliance with Constitution (Model-Service-Controller-Persistence boundaries). Transactional integrity must be guaranteed.

**Scale/Scope**: Initial implementation for a single token market.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **I. Layered Architecture**: PASS. The design strictly respects the Controller -> Service -> Model & Persistence boundary. Controllers will use DTOs in `dto` subfolders. Persistence will have repository interfaces.
- **II. Rich Model**: PASS. Domain logic (e.g. calculating total price, checking sufficient balance, validating state) will reside in Model objects (Portfolio/User, MarketOrder).
- **III. Validation at Every Layer**: PASS. HTTP shape in Controller DTOs. Business validation (e.g. sufficient funds) in Service and domain invariants in Model.
- **IV. Testing as a Delivery Gate**: PASS. Unit tests for Model. Integration for Repository/Service using Testcontainers. E2E via RestClient.
- **V. Definition of Done**: PASS. Standard compliance.

## Project Structure

### Documentation (this feature)

```text
specs/008-token-market-orders/
├── plan.md              
├── research.md          
├── data-model.md        
├── quickstart.md        
└── contracts/           
```

### Source Code (repository root)

```text
src/main/java/com/example/valoracion/ (or relevant root package)
├── controller/
│   ├── TokenMarketController.java
│   └── dto/
│       ├── BuyTokenRequest.java
│       └── SellTokenRequest.java
├── service/
│   ├── TokenMarketService.java
│   ├── TokenEmissionService.java (Listen/Handle player creation)
│   └── impl/
├── persistence/
│   ├── repository/
│   │   ├── PortfolioRepository.java
│   │   └── AuditLogRepository.java
│   └── dto/
│       └── jpa/
│           ├── PortfolioJpaEntity.java
│           └── AuditLogJpaEntity.java
└── model/
    ├── Portfolio.java
    ├── AuditLog.java
    └── exceptions/

src/test/java/
├── model/ (Unit tests)
├── service/ (Integration tests with Testcontainers)
├── persistence/ (Integration tests with Testcontainers)
└── controller/ (E2E API tests with @SpringBootTest and RestClient)
```

## Complexity Tracking

No violations.
