<!--
Sync Impact Report:
- Version change: 0.0.0 → 1.0.0
- Modified principles: none (initial constitution creation)
- Added sections: Core Principles, Technology Standards, Development Workflow, Governance
- Removed sections: none
- Deferred items: TODO(RATIFICATION_DATE): original adoption date was not recorded in the repository.
-->

# Overcode Backend Constitution

## Core Principles

### I. Clean Code and Minimal Dependencies
All Java code MUST be readable, intentional, and maintainable. We prefer small cohesive classes, clear naming, direct behavior, and minimal external dependencies to reduce operational and maintenance cost. Adding a library or framework dependency requires a clear business need, justification in design review, and no equivalent capability already available in the approved Spring Boot stack. Rationale: simplicity and low coupling lower failure risk and speed up onboarding.

### II. SOLID and Object-Oriented Discipline
The backend MUST follow SOLID principles and favor explicit domain modeling over procedural shortcuts. Classes MUST have a single responsibility, interfaces MUST express stable contracts, and extension points MUST not violate substitution or dependency inversion. Design patterns apply only when they solve a real domain problem and improve clarity; unnecessary abstraction is prohibited. Rationale: maintainable systems depend on stable boundaries and predictable extension.

### III. Testing Is Non-Negotiable
Every production change MUST include unit tests, integration tests, and regression tests when behavior changes. Unit tests validate isolated logic; integration tests validate Spring Boot application behavior; regression tests prevent previously fixed defects from returning. Tests MUST be deterministic, run with Maven, and remain green before merge. Rationale: change safety is not optional.

### IV. Java, Spring Boot, and Maven Standards
The project MUST remain a Java 21 Maven application built on Spring Boot 4.1.1 using the versions declared in pom.xml. Code MUST use Java conventions, Spring Boot idioms, and the approved dependencies in the project: spring-boot-starter-security, spring-boot-starter-webmvc, springdoc-openapi-starter-webmvc-ui 3.1.0, and Lombok for boilerplate reduction. Dependencies MUST be added only after evaluating compatibility with the existing stack. Rationale: consistency across the backend keeps builds reproducible and reviewable.

### V. Simplicity Over Conditionals and Over-Engineering
Developers MUST prefer straightforward abstractions, data-driven structures, and domain expressions over complex branching. Conditionals MUST be minimized and replaced with polymorphism, strategy selection, double dispatch, or encapsulation when the logic becomes clearer and more extensible. When a pattern or dispatch mechanism is a better fit, it MUST be used instead of adding nested conditionals. Rationale: simpler control flow is easier to test and reason about.

## Technology Standards
This project MUST use double quotes in Java string literals, CamelCase for class, method, and variable names, and explicit naming that reflects business meaning. We MUST avoid unused abstractions, duplicate logic, and speculative features. Spring Security and web MVC MUST be used to fit the approved framework; OpenAPI documentation MUST reflect the public API contract. Lombok MUST be used only to reduce boilerplate when it does not hide business logic. This repository MUST not introduce ad hoc technologies or dependency churn without a documented justification.

## Development Workflow
All changes MUST be implemented in small, reviewable increments with clear intent. New functionality MUST be added with tests first when the behavior is contractually clear, then production code, and finally refactoring. Pull requests MUST verify the Maven build, unit tests, integration tests, and regression coverage for impacted flows. Code review MUST confirm adherence to SOLID, the project stack, and the no-conditional-overengineering rule. Any exception to these rules requires explicit approval and documentation.

## Governance
This Constitution supersedes conflicting local conventions or shortcut practices. Amendments require a written proposal, a rationale tied to maintainability or delivery risk, and a review of impact on the Java, Spring Boot, and Maven stack. Versioning follows semantic versioning: MAJOR for incompatible policy changes, MINOR for new principles or materially expanded guidance, and PATCH for clarifying wording and non-semantic corrections. Compliance is checked during reviews and before merge; non-compliant work MUST be corrected or explicitly justified by a documented exception.

**Version**: 1.0.0 | **Ratified**: TODO(RATIFICATION_DATE): original adoption date was not recorded in the repository. | **Last Amended**: 2026-08-27
