# Data Model: Player and User Controller Test Suite

## Target Entities and Representations

The test suite exercises representations of the existing domain entities exposed via `PlayerController` and `UserController`. No new domain entities or database tables are created.

### 1. Player

Reuses existing `com.overcode.model.Player` and its serialized representation `PlayerResponseDTO`.

| Field | Type | Description | In Response DTO? |
|---|---|---|---|
| `id` | `Long` | Unique database identifier | Yes |
| `name` | `String` | Player full name (unique) | Yes |
| `currentPrice` | `Integer` | Current token market valuation (minimum 1) | Yes |
| `tokens` | `List<Long>` | List of token identifiers associated with the player | Yes (mapped to token IDs) |

### 2. User

Reuses existing `com.overcode.model.User` and its serialized representation `UserResponseDTO`.

| Field | Type | Description | In Response DTO? |
|---|---|---|---|
| `id` | `Long` | Unique database identifier | Yes |
| `username` | `String` | Public display name | Yes |
| `email` | `String` | User email address | Yes |
| `creditBalance` | `Integer` | Available wallet credits | Yes |
| `tokens` | `List<Long>` | List of owned token identifiers | Yes (mapped to token IDs) |
| `password` | `String` | BCrypt-hashed password | **NO (MUST NEVER BE EXPOSED)** |

### 3. Error Response

Reuses existing `com.overcode.controller.exception.ErrorResponseDTO`.

| Field | Type | Description |
|---|---|---|
| `message` | `String` | Human-readable error description |

---

## Test Fixture Constants

To ensure test maintainability and avoid magic literals across test cases, each test class defines reusable constant fixtures:

### Player Fixtures
- `PLAYER_NAME = "Lionel Messi"`
- `NON_EXISTENT_ID = 999999L`
- `MALFORMED_ID = "invalid-id"`

### User & Auth Fixtures
- `TEST_USERNAME = "testuser"`
- `TEST_EMAIL = "testuser@example.com"`
- `TEST_PASSWORD = "Password123!"`
- `INVALID_BEARER_TOKEN = "Bearer invalid.token.value"`

---

## State Lifecycle in Tests

1. **Clean Slate**: Database tables for users and players are truncated before or after each test via `TestService.eliminarUsuarios()` and `TestService.eliminarJugadores()`.
2. **Authenticated Context**: Tests needing authenticated access invoke `/auth/register` to produce a transient user and receive a valid JWT token.
3. **Data Seeding**: Tests requiring existing entities use `PlayerService.crear(...)` or register users through the test client.
4. **Teardown**: `@AfterEach` executes database cleanup to maintain test isolation.
