# Contract: Backend Authentication API (`POST /auth/register`)

**Feature Branch**: `001-user-registration`  
**Spec**: `specs/001-user-registration/spec.md`  
**Status**: Draft  

---

## 1. Overview
The registration view interfaces with the backend service to create user accounts and obtain authentication tokens. All client-side calls to this contract are mediated through `src/services/authService.ts`.

---

## 2. Endpoint Specification

### `POST /auth/register`

Creates a new user account and returns an authentication session.

- **URL**: `${VITE_API_BASE_URL || '/api'}/auth/register`
- **Method**: `POST`
- **Content-Type**: `application/json`
- **Authentication**: None (Public endpoint)

### Request Payload

```json
{
  "email": "user@example.com",
  "username": "futbol_fan",
  "password": "securePassword123"
}
```

#### Request Field Specifications

| Field | Type | Required | Validation Rules | Description |
|---|---|---|---|---|
| `email` | `string` | Yes | Valid email format | User's unique contact and identity email |
| `username` | `string` | Yes | 3-30 chars, `^[a-zA-Z0-9_]+$` | User's unique system handle |
| `password` | `string` | Yes | Min 6 chars | User's secret password |

---

## 3. Responses

### 3.1 Success: 201 Created (or 200 OK)

Returned when registration is successful and the account is created.

**Response Headers**:
- `Content-Type: application/json`

**Response Body**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": "usr_9812481023",
    "email": "user@example.com",
    "username": "futbol_fan",
    "createdAt": "2026-09-19T21:00:00.000Z"
  }
}
```

### 3.2 Error: 409 Conflict

Returned when the email or username is already taken by another registered user.

**Response Body**:
```json
{
  "statusCode": 409,
  "error": "Conflict",
  "message": "Email or username already in use"
}
```

**Client Translation (Sanitized)**:
> `"El correo electrónico o nombre de usuario ya se encuentra registrado."`

### 3.3 Error: 400 Bad Request

Returned when the server detects malformed or unprocessable input data.

**Response Body**:
```json
{
  "statusCode": 400,
  "error": "Bad Request",
  "message": ["email must be a valid email", "password must be longer than 6 characters"]
}
```

**Client Translation (Sanitized)**:
> `"Los datos ingresados son inválidos. Por favor revisa los campos e intenta nuevamente."`

### 3.4 Error: 500 / Network Failure / Server Unreachable

Returned when network connection is severed or backend server encounters an unhandled internal exception.

**Client Translation (Sanitized)**:
> `"No se pudo conectar con el servidor. Por favor verifica tu conexión a internet o intenta nuevamente más tarde."`
> *(Strictly no stack traces, SQL errors, or runtime exceptions exposed in the UI)*
