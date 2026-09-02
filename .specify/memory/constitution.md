<!--
Sync Impact Report
- Version change: 0.0.0 -> 1.0.0
- Modified principles: Initial constitution adopted from project governance requirements
- Added sections: Core Principles, Additional Constraints, Development Workflow, Governance
- Removed sections: none
- Follow-up TODOs: none
-->

# Valoracion Mercado Jugadores de Futbol Constitution

## Core Principles

### I. Layered Architecture
The project MUST follow a strict layered architecture: model, service, controller, and persistency. Controller components MUST communicate only with the service layer. Services MUST mediate between model and persistency. Model objects MUST NOT know about other layers. Persistency MUST expose a repository abstraction representing the boundary between domain objects and storage logic. Persistency components MAY know the model, but they MUST NOT execute domain behavior. Controller MUST define all DTOs under a dedicated dto subfolder, and persistency MUST also include a repository package structure.

### II. Rich Model
All domain logic MUST live inside model objects. The model layer MUST use SOLID principles and design patterns when needed to keep logic cohesive and testable. Any new model object not explicitly specified by the project MUST NOT be created without explicit permission from the project owner. Model-layer work MUST remain confined to the model package and its responsibilities.

### III. Validation at Every Layer
Each validation MUST occur at the correct boundary. DTO request validation MUST enforce shape, type, trimming, and sanitization requirements before the request reaches the service. Service methods MUST validate that requested entities, IDs, and actions are valid and realizable, including existence checks and state feasibility. Domain invariants MUST be enforced in model objects, and invalid states MUST throw domain-specific exceptions. Validation MUST be explicit and tied to the level where it belongs.

### IV. Testing as a Delivery Gate
All requirements MUST be backed by tests in the proper scope and order. Model tests MUST be unit tests without any database or Spring runtime. Service and repository tests MUST be integration tests using real PostgreSQL instances managed through Testcontainers. End-to-end API tests MUST use MockMvc and MUST live in their own package, never mixed with service tests. Test execution MUST proceed from the normal happy path to border cases, ensuring the most common behavior is proved before exceptional flows. Test names MUST clearly represent the behavior under test; if a test needs extra context, the author MUST add a clarifying comment.

### V. Definition of Done
A requirement is considered done only when the implementation has unit tests and integration tests that pass, and the application compiles and runs without errors using the local configuration. The system MUST satisfy the behavior expected by the project and remain aligned with the architecture rules above. No requirement is complete if its validation coverage or build health is missing.

## Additional Constraints

The project MUST keep a clean separation between responsibility boundaries:

- Controller DTOs MUST be placed under controller/dto.
- Persistency repositories MUST be isolated in persistency/repository and implemented behind repository interfaces.
- Domain logic MUST remain in model objects, not in controllers or repositories.
- New domain objects MUST be approved before introduction, and only within the model layer.
- Existing tests MUST NOT be deleted or modified without explicit permission and an affirmative response from the project owner.
- The project MUST prefer explicit, deterministic validation and domain exceptions over silent fallback behavior.

## Development Workflow

Feature work MUST follow the project architecture and quality gates. Implementation MUST be started by defining the service contract and the relevant domain model, followed by repository and controller support as required. Validation MUST be performed at each layer with the appropriate checks, and all changes MUST be supported by tests that cover both standard and edge conditions. The application MUST be buildable and runnable in the local environment before a requirement is considered complete.

## Governance

This Constitution supersedes informal conventions and governs all technical decisions in this project. Any amendment MUST be documented in this file, must include a version bump according to semantic versioning, and must be reviewed before becoming effective. Major governance or structural changes require explicit approval before implementation. Compliance review MUST verify that architecture boundaries, validation placement, and testing requirements remain satisfied.

**Version**: 1.0.0 | **Ratified**: 2026-09-02 | **Last Amended**: 2026-09-02
