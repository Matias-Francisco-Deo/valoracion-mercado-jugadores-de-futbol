# Tasks: Spring JWT Authentication and Authorization

Feature: Spring JWT Authentication and Authorization (specs/002-spring-jwt-auth)

---

## Phase 1: Setup (Project initialization)

- [X] T001 Ensure JJWT dependencies (jjwt-api, jjwt-impl, jjwt-jackson) and spring-boot-starter-security in backend/pom.xml
- [X] T002 Add JWT config placeholders (jwt.secret, jwt.expiration) to backend/src/main/resources/application.properties
- [X] T003 Add test profile entries for security and H2 in backend/src/test/resources/application-test.properties
- [X] T004 Create specs/002-spring-jwt-auth/quickstart.md with run and smoke-check commands
- [X] T005 [P] Add dependency and build verification CI note in .github/workflows or docs (backend/.github/workflows or specs/002-spring-jwt-auth/quickstart.md)

---

## Phase 2: Foundational (Blocking prerequisites)

- [X] T006 [P] Review and document existing User model in backend/src/main/java/com/overcode/model/User.java (confirm fields: username, email, password)
- [X] T007 [P] Add password hashing utility in backend/src/main/java/com/overcode/security/PasswordHasher.java (bcrypt wrapper)
- [X] T008 [P] Add JWT utility for token creation/validation in backend/src/main/java/com/overcode/security/JwtUtil.java
- [X] T009 Create DTOs for auth requests/responses in backend/src/main/java/com/overcode/controller/dto/AuthDtos.java (RegisterRequest, LoginRequest, AuthResponse)
- [X] T010 [P] Add AuthService interface in backend/src/main/java/com/overcode/service/interfaces/AuthService.java (register, login signatures)
- [X] T011 Create integration test base for security tests in backend/src/test/java/com/overcode/integration/SecurityTestBase.java (H2/Testcontainers setup)
- [X] T012 [P] Update GlobalExceptionHandler to map authentication validation and conflict exceptions in backend/src/main/java/com/overcode/controller/exception/GlobalExceptionHandler.java

---

## Phase 3: User Story 1 - Register a new account (Priority: P1)

Goal: Allow creating an account with username/email/password; reject duplicates.

Independent Test: POST /auth/register with unique email creates account and returns success; duplicate email returns conflict.

- [X] T013 [US1] Implement POST /auth/register in backend/src/main/java/com/overcode/controller/AuthController.java
- [X] T014 [US1] Implement register DTO validation annotations in backend/src/main/java/com/overcode/controller/dto/RegisterRequest.java
- [X] T015 [US1] Implement AuthService.register(...) contract wiring in backend/src/main/java/com/overcode/service/interfaces/AuthService.java
- [X] T016 [US1] Implement AuthServiceImpl.register in backend/src/main/java/com/overcode/service/impl/AuthServiceImpl.java (duplicate email check, password hashing, persist user)
- [X] T017 [US1] Add unit tests for registration validation and duplicate-email in backend/src/test/java/com/overcode/service/AuthServiceRegisterTest.java
- [X] T018 [US1] Add integration test for /auth/register in backend/src/test/java/com/overcode/integration/AuthRegisterIT.java (verify persistence and response)

---

## Phase 4: User Story 2 - Sign in and receive trusted access (Priority: P1)

Goal: Authenticate user and return JWT with 24h TTL.

Independent Test: POST /auth/login returns valid JWT accepted by protected endpoints.

- [X] T019 [US2] Implement POST /auth/login in backend/src/main/java/com/overcode/controller/AuthController.java
- [X] T020 [US2] Implement login DTO validation in backend/src/main/java/com/overcode/controller/dto/LoginRequest.java
- [X] T021 [US2] Implement AuthService.login(...) declaration in backend/src/main/java/com/overcode/service/interfaces/AuthService.java
- [X] T022 [US2] Implement AuthServiceImpl.login in backend/src/main/java/com/overcode/service/impl/AuthServiceImpl.java (credential verification, JWT creation via JwtUtil)
- [X] T023 [US2] Add unit tests for login success and invalid credentials in backend/src/test/java/com/overcode/service/AuthServiceLoginTest.java
- [X] T024 [US2] Add integration test for /auth/login issuing a JWT and validating its TTL in backend/src/test/java/com/overcode/integration/AuthLoginIT.java

---

## Phase 5: User Story 3 - Access protected actions only when authorized (Priority: P2)

Goal: Protect non-auth endpoints; deny requests without valid/expired tokens.

Independent Test: Protected endpoint fails without token and succeeds with valid JWT.

- [X] T025 [US3] Implement JwtAuthenticationFilter in backend/src/main/java/com/overcode/security/JwtAuthenticationFilter.java (validate token, set SecurityContext)
- [X] T026 [US3] Update SecurityConfig SecurityFilterChain in backend/src/main/java/com/overcode/config/SecurityConfig.java to permit /auth/** and require auth elsewhere
- [X] T027 [US3] Wire JwtUtil and PasswordHasher beans into SecurityConfig or appropriate config class in backend/src/main/java/com/overcode/config/
- [X] T028 [US3] Add integration tests for protected endpoints: denied without token, allowed with valid token in backend/src/test/java/com/overcode/integration/ProtectedEndpointIT.java
- [X] T029 [US3] Add tests for expired/tampered token behavior in backend/src/test/java/com/overcode/integration/JwtFailureIT.java

---

## Phase 6: Polish & Cross-Cutting Concerns

- [X] T030 [P] Add OpenAPI annotations for auth endpoints and update springdoc config in backend/src/main/java/com/overcode/config/OpenApiConfig.java
- [X] T031 [P] Document JWT environment variables and secret handling in specs/002-spring-jwt-auth/quickstart.md and docs/security.md
- [X] T032 [P] Add logging and structured error messages around auth flows in backend/src/main/java/com/overcode/service/impl/AuthServiceImpl.java
- [X] T033 Run mvnw test and fix any failing tests; add notes to specs/002-spring-jwt-auth/quickstart.md about local test execution
- [X] T034 [P] Add contract test or API spec mapping in specs/002-spring-jwt-auth/contracts/auth-api.yaml and link to backend/tests/contract if present

---

## Dependencies & Execution Order

- Setup (Phase 1) → Foundational (Phase 2) → User Stories (Phases 3-5) → Polish (Phase 6)
- Recommended story order: [US1] Register → [US2] Login → [US3] Protected endpoints

---

## Parallel opportunities

- Tasks marked [P] can be worked in parallel (DTOs, utils, docs, logging, CI notes).

---

## Implementation strategy

- MVP: Phase 1 + Phase 2 + Phase 3 ([US1] Register) and validate with unit/integration tests. Then implement [US2] Login and [US3] Protected endpoints.

---

Generated from specs/002-spring-jwt-auth/spec.md and plan.md
