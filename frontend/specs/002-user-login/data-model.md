# Data Model: User Login and Session Menu

**Feature**: `002-user-login`
**Spec**: `specs/002-user-login/spec.md`
**Status**: Draft

## 1. Domain Entities and Schemas

### 1.1 `LoginCredentials`

Client payload submitted to `POST /auth/login`.

| Field      | Type     | Required | Constraints                               | Description                                 |
| ---------- | -------- | -------: | ----------------------------------------- | ------------------------------------------- |
| `email`    | `string` |      Yes | Trimmed, non-empty, standard email syntax | Existing account email and login identifier |
| `password` | `string` |      Yes | Non-empty                                 | Existing account secret                     |

Validation is defined by the Yup schema in `src/schemas/loginSchema.ts`. Whitespace-only email is invalid after trimming; password content is not trimmed before length validation.

### 1.2 `AuthResponse`

Successful response from `POST /auth/login`.

| Field       | Type          | Required | Description                               |
| ----------- | ------------- | -------: | ----------------------------------------- |
| `token`     | `string`      |      Yes | Authentication bearer token               |
| `expiresAt` | `string`      |      Yes | Server-provided expiration timestamp      |
| `user`      | `UserProfile` |      Yes | Authenticated user's identity and profile |

### 1.3 `AuthSession`

Client session persisted by the existing session helper and exposed through `AuthContext`.

| Field       | Type          | Required | Lifecycle                                                              |
| ----------- | ------------- | -------: | ---------------------------------------------------------------------- |
| `token`     | `string`      |      Yes | Set on successful login; cleared on logout or backend rejection        |
| `expiresAt` | `string`      |      Yes | Stored from the login response; not a client-only invalidation trigger |
| `user`      | `UserProfile` |      Yes | Used by the navbar and account menu                                    |

### 1.4 `AccountMenuState`

Ephemeral UI state for the authenticated navbar menu.

| Field    | Type          | Description                                  |
| -------- | ------------- | -------------------------------------------- |
| `isOpen` | `boolean`     | Whether the account dropdown is visible      |
| `user`   | `UserProfile` | Current authenticated user shown in the menu |

The menu exists only while `AuthContext.isAuthenticated` is true. Logout resets `isOpen` and clears the session.

## 2. State Transitions

```text
[Unauthenticated]
      |
      | valid login form -> POST /auth/login success
      v
[Authenticated Session]
      |                         \
      | username control click   \ logout
      v                           v
[Account Menu Open]         [Unauthenticated]
      | logout                     ^
      +----------------------------+

[Authenticated Session] -- backend rejects request --> [Unauthenticated]
```

Local validation failures remain on the login page and do not enter the request state. While a request is active, duplicate submissions are ignored and the submit control is disabled.

## 3. Form Feedback State

- Field errors: `email?: string`, `password?: string`, rendered beneath the corresponding field using the existing `Field` styling.
- Server error: a single user-facing alert message at the top of the login card.
- Loading: submit control disabled with a login-progress label while authentication is pending.
