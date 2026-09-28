# Implementation Plan: Spring JWT Authentication and Authorization

**Branch**: `002-spring-jwt-auth` | **Date**: 2026-09-06 | **Spec**: `specs/002-spring-jwt-auth/spec.md`

**Input**: Feature specification from `/specs/002-spring-jwt-auth/spec.md`

## Summary

Implement JWT-based registration and login using the existing `User` model and the current layered project structure. The feature will add `AuthController`, `AuthService`/`AuthServiceImpl`, validation DTOs, JWT generation with a one-day expiration, and a hardened `SecurityFilterChain` in the existing `SecurityConfig`. The controller will remain thin, business logic will stay in the service layer, and the current `User.tokens` field will not be confused with the JWT token.

## Technical Context

**Language/Version**: Java 21 with Spring Boot 4.1.1

**Primary Dependencies**: Spring WebMVC, Spring Security, Spring Data JPA, Bean Validation, PostgreSQL/H2, JJWT (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`)

**Storage**: PostgreSQL in the main runtime, H2 for local/embedded support, persistence via JPA repositories

**Testing**: JUnit 5, Spring Boot Test, Spring Security Test, Testcontainers for integration validation

**Target Platform**: Spring Boot backend running on a standard Java server environment

**Project Type**: Web service / backend API

**Performance Goals**: Standard API latency for registration, login, and protected endpoint checks; no need for a custom caching layer for this feature

**Constraints**: Keep the current `User` model; do not create a separate auth entity; avoid API-key auth; JWT expiration fixed to one day; respect the current controller/service/repository package structure

**Scale/Scope**: Small to medium backend service with authentication required for non-public endpoints

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Layered architecture: PASS. This feature will keep controller DTO mappings in `com.overcode.controller`, business logic in `com.overcode.service`, and persistence access in `com.overcode.persistence.repository`.
- Rich model: PASS with constraint. No new domain model is created; the existing `User` model is reused, and the JWT is not persisted as an attribute on the entity.
- Validation at every layer: PASS. Request validation belongs in DTO and controller validation, with service-level validation for duplicate emails, invalid credentials, and user lookup rules.
- Testing as a delivery gate: PASS. The feature requires registration/login success and failure coverage, plus authorization checks for protected routes.
- Definition of done: PASS, provided the implementation is backed by tests and the app remains buildable when the feature is introduced.

## Project Structure

### Documentation (this feature)

```text
specs/002-spring-jwt-auth/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   └── auth-api.yaml
├── spec.md
└── checklists/
    └── requirements.md
```

### Source Code (repository root)

```text
backend/
├── src/
│   ├── main/
│   │   └── java/com/overcode/
│   │       ├── config/
│   │       │   └── SecurityConfig.java
│   │       ├── controller/
│   │       │   ├── dto/
│   │       │   ├── exception/
│   │       │   └── AuthController.java
│   │       ├── model/
│   │       │   └── User.java
│   │       ├── persistence/
│   │       │   ├── repository/
│   │       │   │   ├── interfaces/
│   │       │   │   └── impl/
│   │       │   └── dto/
│   │       └── service/
│   │           ├── exception/
│   │           ├── interfaces/
│   │           │   ├── AuthService.java
│   │           │   └── UserService.java
│   │           ├── impl/
│   │           │   ├── AuthServiceImpl.java
│   │           │   └── UserServiceImpl.java
│   │           └── ...
│   └── test/
│       └── java/com/overcode/
├── pom.xml
└── ...
```

**Structure Decision**: The feature follows the existing structure under `controller`, `service/interfaces`, `service/impl`, `persistence/repository/interfaces`, and `persistence/repository/impl`. The project already uses `UserController`, `UserRepository`, and `UserService`; auth will align with those conventions instead of introducing a new package layout.

## Phase 0: Research & Design Decisions

1. Reuse the existing `User` model as the source of identity and credentials.
2. Preserve the distinction between the application’s `User.tokens` field and the JWT bearer token.
3. Add JJWT dependencies once in `pom.xml` (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) without duplicating libraries.
4. Use `SecurityConfig` to allow only `/auth/register` and `/auth/login` without authentication while protecting all other endpoints.
5. Define a 24-hour JWT TTL (`Duration.ofDays(1)`).
6. Keep `AuthController` focused on HTTP concerns; put validation, credential checks, password hashing, and token generation in `AuthServiceImpl`.
7. Reuse existing error mappings and validation responses rather than introducing a custom auth-only error layer unless absolutely required.

## Phase 1: Implementation Plan

### 1. Dependency review and setup

- Inspect `backend/pom.xml` and add the JWT modules required by the authentication workflow.
- Ensure no dependency duplicates are introduced.
- Confirm the project already includes `spring-boot-starter-security`, validation, and test support needed for authentication and authorization.

### 2. Existing model review and validation constraints

- Review `com.overcode.model.User` before auth implementation to confirm that `username`, `email`, `password`, and the existing token balance fields are used correctly.
- Explicitly preserve the difference between `User.tokens` and JWT bearer tokens.
- Ensure hash-based password storage is used and that plain-text password exposure is avoided in responses.

### 3. DTO and controller design

- Add DTOs under `com.overcode.controller.dto` for registration and login requests/responses.
- Define `AuthController` in `com.overcode.controller` as the HTTP entry point for `/auth/register` and `/auth/login`.
- Keep controller methods thin: validate request payloads, delegate to `AuthService`, and return `ResponseEntity` with correct status codes.
- Map invalid credentials and validation errors to the existing `ApiError`/`GlobalExceptionHandler` pattern.

### 4. Service layer design

- Define `AuthService` in `com.overcode.service.interfaces` with methods such as `register(...)` and `login(...)`.
- Implement `AuthServiceImpl` in `com.overcode.service.impl` to handle:
  - email/password validation,
  - duplicate-email detection,
  - password hashing,
  - repository lookup and user creation,
  - JWT creation and expiration metadata,
  - invalid credential handling.
- Keep all business logic outside the controller and in the service contract implementation.

### 5. Security configuration

- Update the existing `SecurityConfig` class to define a `SecurityFilterChain`.
- Permit anonymous access for only the auth endpoints (`/auth/register`, `/auth/login`).
- Require authentication for all remaining endpoints that are not part of registration or login.
- Keep the current project pattern of using `HttpSecurity` and the existing security configuration class rather than introducing a new security bootstrap class.

### 6. JWT token lifecycle

- Define a JWT secret and signing key configuration supported by the app’s current security setup.
- Set JWT expiration to one day (24 hours) using a `Duration.ofDays(1)`-based value or equivalent millisecond configuration.
- Generate the token after successful authentication and return it to the client in the response body or header conventions already used by the project.

### 7. Error handling

- Reuse the existing `ValidationException`, `ConflictException`, `NotFoundException`, and `ApiError` mechanism where appropriate.
- For invalid credentials, return a clear authentication/validation error response and do not leak user existence details beyond the project’s standard error contract.
- Ensure missing or malformed token requests are rejected before any action reaches protected business logic.

### 8. Validation and verification

- Add unit and integration tests for:
  - successful registration,
  - duplicate email rejection,
  - valid login,
  - invalid credentials,
  - protected route denial with no token,
  - protected route success with a valid JWT.
- Run the backend test suite with Maven and ensure the application still compiles cleanly after the feature is added.

## Complexity Tracking

No constitution violations or additional architectural exceptions are required for this feature. The implementation remains within the existing layering and package constraints.
