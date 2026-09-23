# Research: User Login and Session Menu

**Feature**: `002-user-login`
**Date**: 2026-09-22

## Decision 1: Reuse the existing authentication boundary

- **Decision**: Extend `src/services/authService.ts` with `login(credentials)` and extend `AuthContext` with `login`, while keeping components unaware of API URLs and storage details.
- **Rationale**: The repository already centralizes API calls in `apiClient`, authentication state in `AuthContext`, and session persistence in `src/lib/session.ts`. Reusing these boundaries satisfies the constitution and prevents a second session model.
- **Alternatives considered**: Calling `fetch` directly from `LoginForm` was rejected because it duplicates API behavior and violates the centralized integration boundary.

## Decision 2: Use Yup for declarative login validation

- **Decision**: Add the `yup` runtime dependency and place the login schema in `src/schemas/loginSchema.ts`. Keep the schema focused on email and password rules, and use the same messages and limits as registration.
- **Rationale**: Yup provides a typed, declarative schema that is easy to test independently and avoids duplicating imperative validation logic in the form component. The existing registration rules establish the product behavior: trimmed standard email syntax, password required, minimum 6 characters, and maximum 30 characters.
- **Alternatives considered**: Keeping inline validation was rejected because it would leave registration and login validation divergent. A custom validator was rejected because the requested feature explicitly requires Yup.

## Decision 3: Persist the complete session, including expiration metadata

- **Decision**: Extend `AuthSession` to include `expiresAt` and persist the token, expiration timestamp, and user profile through the existing session helper. Do not proactively log out solely because the timestamp has passed; clear the session when the backend rejects an authenticated request.
- **Rationale**: This matches the clarification and preserves server authority for token validity while making expiration metadata available to future authenticated requests.
- **Alternatives considered**: A client-side timer was rejected because it can drift from backend validity and was explicitly not selected during clarification.

## Decision 4: Keep the existing visual language and shared shell

- **Decision**: Implement `LoginPage` with the same background asset, layout dimensions, gray card, orange button, field primitives, and navbar shell as registration. Extend `Navbar` conditionally from `useAuth` to render the username control and account menu.
- **Rationale**: Registration already establishes the required visual conventions and the constitution requires reuse of suitable components and centralized navigation.
- **Alternatives considered**: A separate login-only layout was rejected because it would create visual and maintenance divergence.

## Decision 5: Handle backend failures through the existing API error contract

- **Decision**: Preserve `ApiError` propagation for non-500 client messages, show those messages in the form alert, and use the existing generic message for network/server failures. The login form must not expose stack traces or raw technical details.
- **Rationale**: This follows the project constitution and the behavior already defined by `apiClient`.
- **Alternatives considered**: Reformatting all backend messages into a generic login error was rejected because the constitution requires displaying backend client messages for non-500 responses.
