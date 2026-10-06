# Data Model: Controller, Repository, Auth, and JSON Extraction Test Coverage

## Entities and value objects

### `PlayerGameData`

Represents the persisted player statistical snapshot used by the repository boundary.

| Field | Type | Constraints | Notes |
|---|---|---|---|
| `id` | `Long` | optional | database identifier |
| `player` | `Player` | optional | associated player reference |
| `goals` | `Integer` | required | number of goals |
| `assists` | `Integer` | required | helper statistics |
| `shotsOnTarget` | `Integer` | required | shooting output |
| `tackles` | `Integer` | required | defensive actions |
| `keyPasses` | `Integer` | required | playmaking contribution |
| `successfulDribbles` | `Integer` | required | ball progression |
| `rating` | `Double` | required | last rating value |

Relationships:
- A `PlayerGameData` is associated with a `Player` instance and is persisted through `PlayerGameDataJPADTO`.

### `PlayerGameDataJPADTO`

Persistence representation for the JPA repository layer.

| Field | Type | Constraints | Notes |
|---|---|---|---|
| `id` | `Long` | PK, generated | database row id |
| `player` | `PlayerJPADTO` | optional | persisted player relation |
| `goals` | `Integer` | `nullable = false` | mapped from model |
| `assists` | `Integer` | `nullable = false` | mapped from model |
| `shotsOnTarget` | `Integer` | `nullable = false` | mapped from model |
| `tackles` | `Integer` | `nullable = false` | mapped from model |
| `keyPasses` | `Integer` | `nullable = false` | mapped from model |
| `rating` | `Double` | `nullable = false` | mapped from model |
| `successfulDribbles` | `Integer` | `nullable = false` | mapped from model |

Validation rules:
- The repository must preserve values when mapping model → JPA DTO and back.
- Null or malformed data on the DTO boundary is handled by the repository contract and the test boundary, not by the controller.

### `ExternalPlayerController`

Admin controller that exposes the manual refresh trigger.

| Contract | Value |
|---|---|
| Route | `POST /api/admin/players/actualizar-jugadores` |
| Authorization | requires valid `X-API-KEY` header |
| Response on success | `202 Accepted` |
| Response body | `Scraper manual iniciado en background. Este proceso puede tardar varias horas.` |
| Side effect | calls `actualizarJugadoresAsync()` exactly once |

### `ApiKeyAuthFilter`

Filter that guards the scraper admin route.

| Input | Validation |
|---|---|
| path | only checks `/api/admin/scraper` and related admin scraper routes |
| header | `X-API-KEY` required |
| allowed request | equals configured `scraper.api.key` |
| invalid request | unauthorized `401` and no chain continuation |

### `JsonExtractorUtil`

Stateless utility that extracts the embedded JSON payload and position text from HTML.

| Method | Input | Output |
|---|---|---|
| `extractPlayerStatsJson(String html)` | raw HTML | clean JSON snippet without script noise |
| `extractPlayerPosition(String html)` | raw HTML | normalized position label or `null` |

Edge cases defined by the tests:
- null or empty HTML triggers extraction exception.
- malformed HTML without `require.config.params['args']` triggers extraction exception.
- absent position markers return `null`.
