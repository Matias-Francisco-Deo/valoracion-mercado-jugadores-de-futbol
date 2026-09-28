# Data model: JWT authentication

## Core entity

### User

The feature reuses the existing `com.overcode.model.User` entity and does not create an auth-specific user model.

| Field | Type | Notes |
| --- | --- | --- |
| id | Long | Database identifier |
| username | String | Required for registration; used as a user-facing identity |
| email | String | Required and unique for login/duplicate detection |
| password | String | Stored as a hashed password; never exposed in plain text |
| creditBalance | Integer | Existing financial balance; not part of JWT auth |
| tokens | Integer | Existing marketplace token balance; not the JWT bearer token |

### Important clarification

The `User.tokens` attribute is a business field for the app’s token economy and is different from the JWT bearer token used to authorize HTTP requests. The auth feature must never treat `User.tokens` as the JWT or store JWTs in the user model.

## DTOs

### Registration DTOs

- `RegisterRequest` / `CreateUserRequest` equivalent: fields `username`, `email`, `password`.
- Validation requirements: required, non-empty, proper email format, password length according to project conventions.
- Response DTO: `AuthResponse` or `LoginResponse` with `token`, `expiresAt`, and the authenticated user summary.

### Login DTOs

- `LoginRequest`: `email`, `password`.
- Response DTO: same auth payload as registration, or a dedicated `TokenResponse` if the project prefers a narrower contract.

## Validation rules

- Email must be present and valid.
- Password must be present and meet the app’s minimum requirements.
- Username must be present for registration.
- Duplicate email addresses must be rejected with the project’s conflict/validation flow.
- Passwords must be hashed before persistence.

## Relationships

- `User` has a one-to-one relationship with the auth identity used during login; no separate auth entity is introduced.
- JWT is treated as an ephemeral token generated on successful authentication and validated by the security filter chain; it is not persisted in the `User` table.

## State transitions

- Guest -> registered user: request valid registration data and create a user record.
- Registered user -> authenticated session: valid email/password leads to JWT creation.
- Authenticated user -> protected resource access: valid JWT accepted by `SecurityFilterChain` and authorization rules.
- Invalid/expired/missing token -> denied before business logic executes.
