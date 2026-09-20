# Data Model: User Registration Page & Layout

**Feature Branch**: `001-user-registration`  
**Feature Spec**: `specs/001-user-registration/spec.md`  
**Status**: Draft  

---

## 1. Domain Entities & Schemas

### 1.1 `RegisterCredentials`
Represents the client-side user submission payload sent to the registration endpoint.

| Field | Type | Required | Constraints / Validation | Description |
|---|---|---|---|---|
| `email` | `string` | Yes | Non-empty, trimmed, valid email syntax (`^[^\s@]+@[^\s@]+\.[^\s@]+$`) | User's electronic mail address used for communication and primary identity. |
| `username` | `string` | Yes | Non-empty, trimmed, min 3 chars, max 30 chars, alphanumeric and underscores (`^[a-zA-Z0-9_]+$`) | Unique public display and account handle. |
| `password` | `string` | Yes | Non-empty, min 6 chars, max 100 chars | Secret credential for account access. |

### 1.2 `UserProfile`
Represents the authenticated user profile data returned by the backend and preserved in the client session.

| Field | Type | Required | Description |
|---|---|---|---|
| `id` | `string \| number` | Yes | Unique identifier assigned by the backend. |
| `username` | `string` | Yes | User's registered username. |
| `email` | `string` | Yes | User's registered email address. |
| `creditBalance` | `number` | Yes | Timestamp of user creation (ISO 8601). |
| `tokens` | `number[]` | Yes | List of token identifiers associated with the user. |

### 1.3 `AuthResponse`
Represents the successful HTTP response payload received from `POST /auth/register` (or `POST /auth/login`).
The backend returns an HTTP 201 Created response with an AuthResponse containing the authentication token, its expiration timestamp, and the authenticated user's profile.

| Field | Type | Required | Description |
|---|---|---|---|
| `token` | `string` | Yes | JWT (JSON Web Token) bearer token for authenticating subsequent requests. |
| `expiresAt` | `string` | Yes | Token expiration timestamp returned by the backend as an ISO-8601 timestamp. |
| `user` | `UserProfile` | Yes | Authenticated user profile data. |

### 1.4 `AuthSession`
Represents the client-side session state stored in `localStorage` and managed by `AuthContext`.

| Field | Type | Storage Key | Description |
|---|---|---|---|
| `token` | `string \| null` | `auth_token` | Persisted JWT authentication token. |
| `expiresAt` | `string \| null` | auth_expires_at | Persisted token expiration timestamp returned by the backend. |
| `user` | `UserProfile \| null` | `auth_user` | Persisted user profile JSON object. |
| `isAuthenticated` | `boolean` | Derived | Computed flag (`Boolean(token && user)`). |
| `isLoading` | `boolean` | In-memory | Indicates whether storage hydration or an auth action is in flight. |

---

## 2. UI & Form Validation State

### 2.1 `RegisterFormErrors`
Represents inline validation error messages displayed beneath individual form inputs.

| Field | Type | Description |
|---|---|---|
| `email` | `string \| undefined` | Inline error message for email field validation failure. |
| `username` | `string \| undefined` | Inline error message for username field validation failure. |
| `password` | `string \| undefined` | Inline error message for password field validation failure. |

### 2.2 `AlertState`
Represents banner-level notifications displayed at the top of the form card for network or backend errors.

| Field | Type | Description |
|---|---|---|
| `type` | `'error' \| 'success' \| 'info'` | Alert severity and visual style. |
| `message` | `string` | User-friendly, sanitized notification text (zero technical leaks). |

---

## 3. State Transitions & Lifecycle

### 3.1 Registration Lifecycle State Machine

```
[ Idle / Empty Form ]
        │
        ▼ (User types into inputs)
[ Form Populated ]
        │
        ▼ (Submit button clicked)
[ Validating Client Inputs ]
   ├── (Validation fails) ────► [ Display Inline Field Errors ] ───► (User edits field to fix)
   │
   └── (Validation passes) ───► [ Submitting: isLoading = true ]
                                       │
            ┌──────────────────────────┴──────────────────────────┐
            ▼ (Network / Server Error or 409)                     ▼ (200 / 201 Success)
  [ Display Top Alert Banner ]                       [ Persist Token & User in localStorage ]
  [ Re-enable Form Inputs ]                                       │
                                                                  ▼
                                                     [ Update AuthContext State ]
                                                                  │
                                                                  ▼
                                                     [ Navigate to Home Page ('/') ]
```

### 3.2 Session Hydration Lifecycle

```
[ App Mounts ]
      │
      ▼
[ AuthProvider Initialized: isLoading = true ]
      │
      ▼ (Read localStorage: auth_token, auth_user)
      ├── (Token & User present and valid) ──► [ Set token, user, isAuthenticated = true ]
      └── (Missing or corrupted) ─────────────► [ Clear localStorage, token = null, user = null ]
      │
      ▼
[ Hydration Complete: isLoading = false ]
```

---

## 4. Storage Model (`localStorage`)

All storage interactions are strictly isolated within `AuthContext.tsx`. Individual components never interact with `localStorage`.

| Key | Storage Type | Serialization | Expiry / Cleanup |
|---|---|---|---|
| `auth_token` | `string` | Raw string (JWT) | Cleared on logout or invalid session |
| `auth_expires_at` | `string` | ISO-8601 timestamp | Cleared on logout or expired/invalid session. |
| `auth_user` | `string` | `JSON.stringify(UserProfile)` | Cleared on logout or invalid session |
