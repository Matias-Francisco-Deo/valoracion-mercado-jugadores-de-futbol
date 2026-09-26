# Security Documentation

This document describes the security architecture of the application, focusing on Authentication and Authorization.

## Authentication (JWT)

The application uses JSON Web Tokens (JWT) for stateless authentication.

### Token Generation
Upon successful login or registration, the backend generates a JWT signed with the HS256 algorithm. The token contains the user's `email` as the subject, and additional claims like `uid` and `username`.

### Token Validation
A custom `JwtAuthenticationFilter` intercepts all incoming requests. If a request has an `Authorization` header starting with `Bearer `, the filter extracts and validates the token using the secret key. If valid, the user's details are stored in the Spring `SecurityContextHolder`.

### Environment Variables
The JWT implementation relies on two main properties that must be configured securely:

- `jwt.secret`: A base64-encoded secret string. MUST be at least 32 bytes long (256 bits). **Do not commit the production secret to version control.**
- `jwt.expiration`: The time-to-live for the token in milliseconds. Default is usually 24 hours (`86400000`).

## Passwords
Passwords are never stored in plain text. They are hashed using `BCryptPasswordEncoder` with a strong work factor before being persisted to the database.

## Protected Routes
By default, all endpoints require authentication. The only exceptions are explicitly whitelisted in `SecurityConfig.java`:
- `/auth/**` (Registration, Login)
- Swagger UI / OpenAPI docs (`/v3/api-docs/**`, `/swagger-ui/**`)
- H2 Console (`/h2-console/**`)
