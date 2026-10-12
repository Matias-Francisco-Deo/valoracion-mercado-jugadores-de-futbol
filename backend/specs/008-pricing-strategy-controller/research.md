# Research: Pricing Strategy Controller and End-to-End Test Suite

## Decisions and Findings

### Decision 1: Security and Authentication via API Key

- **Decision**: Expose all pricing strategy endpoints under `/api/admin/estrategias-cotizacion/**` protected by `ApiKeyAuthFilter` requiring the `X-API-KEY` header matching the configured `scraper.api.key`.
- **Rationale**: Follows the established administrative pattern used in `ExternalPlayerController` (`/api/admin/players/actualizar-jugadores`). Consolidates administrative operations behind API key verification.
- **Alternatives considered**:
  - JWT Bearer authentication: Rejected per user instruction to align with scraper admin endpoint API key security.
  - Public read access with protected writes: Rejected because pricing strategy visibility and management are administrative concerns.

### Decision 2: Strategy Creation DTO & Test Mocking Pattern

- **Decision**: The request DTO `CrearEstrategiaCotizacionRequestDTO` captures `tipo` (String), `valorBase` (Double), and `factorEscala` (Double). In tests, the strategy type-to-DTO resolution is mocked using `MockedStatic<TipoEstrategiaCotizacion>` dynamically without modifying the production enum `TipoEstrategiaCotizacion`.
- **Rationale**: Strictly preserves the production codebase by avoiding test-specific enum entries, exactly mirroring the proven testing approach from `EstrategiaCotizacionServiceTest`.
- **Alternatives considered**:
  - Adding test types (e.g. `SIEMPRE_2`) to production `TipoEstrategiaCotizacion`: Explicitly rejected by user instructions and production code governance.
  - Hardcoding a single strategy in the service/controller: Rejected because strategies must remain polymorphic and extensible.

### Decision 3: Player Seeding in E2E Tests via PlayerService

- **Decision**: E2E test scenarios validating `/cotizar` will use `PlayerService.crear(Player)` to persist player records prior to invoking the recalculation endpoint.
- **Rationale**: Aligns with user request ("Use PlayerService if you want to create players") and follows layered architecture by delegating entity creation to the service layer.
- **Alternatives considered**:
  - Direct persistence DAO injection: Rejected in favor of using `PlayerService`.
  - Relying on pre-existing database records: Rejected to maintain test isolation and determinism.

### Decision 4: Test Suite Naming and Structure in Latin American Spanish

- **Decision**: Write all test classes, method names, comments, and assertions in Latin American Spanish (e.g., `debeListarTodasLasEstrategiasCuandoExistenEstrategias()`, `debeRechazarPeticionSinApiKey()`). Structure test cases starting with normal happy paths, followed by border conditions and error scenarios.
- **Rationale**: Explicit user requirement ("tests in latin american spanish") and adheres to Constitution Principle IV ("Testing as a Delivery Gate").
- **Alternatives considered**:
  - English test names: Rejected per user instruction.

### Decision 5: Exception Handling for Domain Validation

- **Decision**: Add `@ExceptionHandler(EstrategiaInvalidaException.class)` to `GlobalExceptionHandler` returning `HttpStatus.BAD_REQUEST` (`400`) with the domain error message.
- **Rationale**: Upholds Constitution Principle III ("Validation at Every Layer"), ensuring that model-level validation violations (e.g. `factorEscala <= 0`, `valorBase < 0`) translate into clear client error responses rather than unhandled 500 server errors.
- **Alternatives considered**:
  - Catching domain exceptions inside the controller: Rejected because global exception handlers provide uniform error responses across the application.
