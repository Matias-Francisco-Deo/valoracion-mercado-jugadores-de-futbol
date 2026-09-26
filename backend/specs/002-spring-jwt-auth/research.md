# Research: Spring JWT authentication

## Decision: Use the existing User model and a JWT access token separate from User.tokens

- The application already defines `com.overcode.model.User` with `id`, `username`, `email`, `password`, `creditBalance`, and `tokens`.
- `User.tokens` is an application-specific balance value and must not be confused with a JWT token. The JWT is a transient bearer token generated for authentication, not persisted in the `User` entity.
- The auth feature will therefore reuse the same `User` record for identity and credential storage, while the JWT is generated and validated outside the model.

## Decision: Add JWT dependencies only once in the project POM

- The project currently includes Spring Security and validation support in `backend/pom.xml`.
- The auth feature needs `io.jsonwebtoken:jjwt-api`, `io.jsonwebtoken:jjwt-impl`, and `io.jsonwebtoken:jjwt-jackson`.
- We will add only this set and avoid duplicate library declarations or overlapping JWT starter dependencies.
- The resulting configuration should remain minimal and aligned with the current Spring Boot version already used by the application.

## Decision: Keep controller logic thin and delegate to auth service

- `AuthController` will only receive HTTP requests and map them to DTOs and response objects.
- Business rules for validation, credential checks, password hashing, JWT creation, and duplicate-email detection will live in `AuthService` and `AuthServiceImpl`.
- This preserves the project pattern already used by `UserController` and `UserService`.

## Decision: Use one-day JWT expiration

- The token expiration will be set to a value equivalent to 24 hours (`Duration.ofDays(1)` or its milliseconds equivalent).
- This matches the feature requirement and keeps the token usable without becoming indefinitely valid.

## Decision: Secure the app by default and open only auth endpoints

- The existing `SecurityConfig` currently permits all requests.
- The implementation will replace that permissive rule with a `SecurityFilterChain` that allows anonymous access only to `/auth/register` and `/auth/login` while requiring authentication for the rest of the application.
- Existing endpoints that are not auth-related will remain protected by the same filter chain.

## Decision: Reuse existing API error patterns

- The project already exposes `ApiError` and a `GlobalExceptionHandler` that handles `ValidationException`, `ConflictException`, and `NotFoundException`.
- Invalid credentials and bad auth requests should be mapped to the same validation/error convention (`VALIDATION_ERROR`, `CONFLICT`, or a dedicated authentication error code if necessary), instead of inventing a parallel error mechanism.

## Alternatives considered

- Creating a separate auth-specific `User` class: rejected because the requirement explicitly says to use the existing `User` model and avoid a separate entity.
- Persisting the JWT inside the `User` model: rejected because it conflates platform token balances with authentication state and would overlap with `User.tokens`.
- Using API-key authentication: rejected because the feature explicitly excludes API-key-based authentication.
- Putting credential logic directly in the controller: rejected because it violates the project’s layered architecture and controller/service separation.

## Open implementation notes

- `AuthController` should sit under `com.overcode.controller`.
- `AuthService` and `AuthServiceImpl` should stay under `com.overcode.service.interfaces` and `com.overcode.service.impl`.
- Repositories should remain under `com.overcode.persistence.repository.*` and may be extended only if the current `UserRepository` does not already provide the required lookup methods.
- Registration should validate required inputs, detect duplicate emails, hash the password, and create the user through the existing repository/service flow.
- Login should validate the email/password pair, load the matching `User`, validate the password, and issue a signed JWT with a 1-day expiration.
