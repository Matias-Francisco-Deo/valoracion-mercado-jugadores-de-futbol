# Contract: Backend Login API (`POST /auth/login`)

**Feature**: `002-user-login`
**Spec**: `specs/002-user-login/spec.md`
**Status**: Draft

## Endpoint

### `POST /auth/login`

Authenticates an existing account and returns a session.

- **URL**: `${VITE_API_BASE_URL}/auth/login`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Authentication**: None

## Request

```json
{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

The client trims the email before submission and does not trim the password.

## Successful Response

The client accepts the agreed response shape and passes it to the session layer:

```json
{
  "token": "jwt-token",
  "expiresAt": "2026-12-31T23:59:59.000Z",
  "user": {
    "id": "usr_123",
    "email": "user@example.com",
    "username": "futbol_fan",
    "creditBalance": 0,
    "tokens": []
  }
}
```

The HTTP success status may be the backend's normal successful login status. The frontend does not hardcode a status-specific success requirement beyond `response.ok`.

## Error Handling

- Non-500 responses: preserve the backend-provided client message through `ApiError` and display it in the login form's alert area.
- HTTP 500 or network failure: show the existing generic user-facing message and omit stack traces, raw server internals, and transport details.
- A backend rejection of an authenticated request clears the session and restores the public navbar state.

## Client Boundary

`src/services/authService.ts` owns this request. `LoginForm` calls `AuthContext.login`; it must not call `fetch`, build URLs, or access `localStorage` directly.
