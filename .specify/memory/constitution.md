<!--
Sync Impact Report
- Version change: 1.0.0 -> 1.1.0
- Modified principles: I. Layered Architecture, II. Rich Model, III. Validation at Every Layer, IV. Testing as a Delivery Gate, V. Definition of Done
- Added sections: none
- Removed sections: none
- Follow-up TODOs: none
-->

# Valoracion Mercado Jugadores de Futbol Constitution

## Core Principles

### I. Layered Architecture
The project MUST follow a strict layered architecture: model, service, controller, and persistency. Controller components MUST communicate only with the service layer. Services MUST orchestrate flows between model and persistency, but MUST NOT contain business rules or domain logic. Model logic, including operations such as a trade between users, MUST live in the model layer and MUST be implemented as part of the domain model. Persistency MUST be separated from the model: database mappings, persistence DTOs, and repository adapters MUST be distinct from model objects. Persistency components MAY know model types for mapping, but they MUST NOT execute domain behavior. No business rules may be embedded in controllers, services, or persistence code.

### II. Rich Model
All domain logic MUST live inside model objects. The model layer MUST use SOLID principles and design patterns when needed to keep logic cohesive and testable. Model objects MUST own invariants, state transitions, and rules for valid operations. A trade between users, eligibility checks, balance changes, and any other domain behavior MUST be expressed in the model, not in the service layer. The model layer MUST remain the authoritative home of business rules; services may delegate to model methods but MUST NOT re-implement the same logic.

### III. Validation at Every Layer
Each validation MUST occur at the correct boundary. DTO request validation MUST enforce shape, type, trimming, and sanitization before data reaches the service. Service methods MUST validate requested entities, IDs, and actions are valid and realizable, including existence and state checks. Domain invariants MUST be enforced in model objects, and invalid states MUST raise domain-specific exceptions. Validation MUST be explicit and tied to the layer where the rule belongs.

### IV. Testing as a Delivery Gate
All requirements MUST be backed by tests in the proper scope and order. Model tests MUST be unit tests without database or Spring runtime. Service and repository tests MUST be integration tests using real PostgreSQL instances managed through Testcontainers. End-to-end API tests MUST use MockMvc and MUST live in their own package, never mixed with service tests. Every new behavior, rule, or mapping added MUST be covered by tests at the same time it is introduced. No production code may be added without corresponding tests, and all tests MUST be run before completion. Test execution MUST proceed from the normal happy path to edge cases, proving the most common behavior before exceptional flows. Test names MUST clearly represent the behavior under test; if context is needed, the author MUST add a clarifying comment.

### V. Definition of Done
A requirement is considered done only when the implementation has passing unit and integration tests, the application compiles and runs without errors in the local configuration, and the system satisfies the expected behavior without violating the architecture rules above. The project MUST keep code and tests aligned with the domain model and persistency separation rules. Any newly introduced artifact MUST be used by the application or removed; unused code is not allowed. No requirement is complete if its validation coverage, build health, or architectural compliance is missing.

## Additional Constraints

The project MUST keep a clean separation between responsibility boundaries:

- Controller DTOs MUST be placed under controller/dto.
- Persistency repositories MUST be isolated in persistency/repository and implemented behind repository interfaces.
- Database DTOs that map model objects MUST live in the persistency layer, not in the model layer.
- The model MUST remain free of persistence concerns and database-specific annotations or structures.
- Domain logic MUST remain in model objects, not in controllers, services, or repositories.
- Any new domain object or behavior MUST be justified by the project requirements and implemented in the model layer.
- Existing tests MUST NOT be deleted or modified without explicit permission and an affirmative response from the project owner.
- The project MUST prefer explicit, deterministic validation and domain exceptions over silent fallback behavior.
- Any new class, helper, repository, DTO, or service introduced MUST be used by the application; otherwise it MUST be removed.
- Every new behavior MUST have tests added with the same change.

## Development Workflow

Feature work MUST follow the project architecture and quality gates. Implementation MUST start by defining the service contract and the relevant domain model, followed by repository and controller support as required. Validation MUST be performed at each layer with the appropriate checks, and all changes MUST be supported by tests that cover standard and edge conditions. Persistency mapping MUST be implemented as distinct DTOs that translate between the model and the database. No business logic may be moved into the service layer when it belongs in the model. The application MUST be buildable and runnable in the local environment before a requirement is considered complete.

## Governance

This Constitution supersedes informal conventions and governs all technical decisions in this project. Any amendment MUST be documented in this file, MUST include a version bump according to semantic versioning, and MUST be reviewed before becoming effective. Major governance or structural changes require explicit approval before implementation. Compliance review MUST verify that architecture boundaries, validation placement, domain ownership, persistence separation, and testing requirements remain satisfied.

**Version**: 1.1.0 | **Ratified**: 2026-09-02 | **Last Amended**: 2026-09-03
