# Quickstart: JWT auth validation

## Prerequisites

- Java 21
- Maven
- PostgreSQL or the project’s configured local test database
- Application running locally via `mvn spring-boot:run`

## Dependency check

Confirm the following dependencies are present in `backend/pom.xml` before implementation begins:

- `io.jsonwebtoken:jjwt-api`
- `io.jsonwebtoken:jjwt-impl`
- `io.jsonwebtoken:jjwt-jackson`

No duplicate JWT libraries should be added.

## Environment Variables

Ensure the following properties are set in your environment or `application.properties`:

- `jwt.secret`: A base64-encoded secret key used to sign the JWT tokens. It must be at least 256-bit (32 bytes). Keep this secure!
- `jwt.expiration`: The token validity duration in milliseconds (e.g., `86400000` for 24 hours).

## Validation scenarios

### 1. Registration success

- POST to `/auth/register`
- Body: `{ "username": "demo", "email": "demo@example.com", "password": "StrongPass123!" }`
- Expected: `201 Created` and a JSON body containing a JWT token and expiration metadata.

### 2. Duplicate email rejection

- Repeat the same registration call with the same email.
- Expected: `409 Conflict` or the project’s conflict response in the same error format as `ConflictException`.

### 3. Login success

- POST to `/auth/login`
- Body: `{ "email": "demo@example.com", "password": "StrongPass123!" }`
- Expected: `200 OK` with a JWT issued for the user.

### 4. Invalid credentials rejection

- Submit a bad password or an unregistered email.
- Expected: `401 Unauthorized` or a validation/error payload in the existing `ApiError` format.

### 5. Protected route validation

- Call any non-auth endpoint without a token.
- Expected: request denied before business logic executes.
- Then include the token in the `Authorization` header as `Bearer <token>`.
- Expected: protected flow proceeds normally when allowed.

## Test command

Run the project tests after implementation:

```bash
cd backend
mvn test
```

The test suite should cover successful registration/login, invalid credentials, duplicate email handling, and protected endpoint access.

## CI Verification

For continuous integration (e.g., GitHub Actions), ensure that the build runs `mvn clean test` to verify all security scenarios automatically before merging PRs.
