# Feature Specification: Pricing Strategy Management and End-to-End Verification

**Feature Branch**: `feature/cotizar-jugadores`

**Created**: 2026-10-09

**Status**: Draft

**Input**: User description: "We are going to develop the controller for all the methods in the service of EstrategiaCotizacionService, and all the e2e tests related to it"

## Clarifications

### Session 2026-10-09

- Q: How should clients authenticate when calling the pricing strategy endpoints? → A: Option B (Administrative API Key header `X-API-KEY` under the admin route prefix `/api/admin/estrategias-cotizacion/**`).
- Q: How should the strategy creation endpoint identify which concrete pricing strategy to instantiate? → A: The creation request specifies the strategy parameters (`tipo`, `valorBase`, `factorEscala`), and tests MUST NOT add new enum entries to `TipoEstrategiaCotizacion` in production code; instead, the strategy type mapping is mocked within the test context (mocking the type-to-DTO resolution when the test strategy class arrives, following the existing pattern in service tests).
- Q: What HTTP status and response payload should the bulk player valuation endpoint return upon successful completion? → A: Option A (HTTP 200 OK with a descriptive confirmation message, e.g., `"Cotización de jugadores finalizada exitosamente"`).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Consult Available Pricing Strategies (Priority: P1)

An authorized platform administrator needs to inspect all registered pricing strategies in the system to review available valuation algorithms, their base values, and their scaling factors.

**Why this priority**: Visibility into available pricing strategies is a foundational prerequisite for managing quotations and selecting which algorithm dictates player valuations.

**Independent Test**: Can be verified by creating sample pricing strategies and executing a query request with the administrative API key, confirming that all records are returned with their complete configuration, and verifying that an empty collection is returned when no strategies exist.

**Acceptance Scenarios**:

1. **Given** one or more pricing strategies exist in the system, **When** a valid administrative request with `X-API-KEY` to list pricing strategies is received, **Then** the system returns a successful response containing all registered strategies with their identifiers, base values, and scale factors.
2. **Given** no pricing strategies are registered in the system, **When** a valid administrative request with `X-API-KEY` to list pricing strategies is received, **Then** the system returns a successful response containing an empty list.

---

### User Story 2 - Select and Activate a Pricing Strategy (Priority: P1)

An authorized administrator needs to designate a specific pricing strategy as the active strategy for the entire platform, so that subsequent player valuation operations use that chosen algorithm.

**Why this priority**: Selecting the active strategy determines how player market values are calculated across the platform. Without this capability, the valuation engine cannot be configured.

**Independent Test**: Can be verified by selecting an existing strategy identifier with the administrative API key and validating that the quotation configuration is updated to reference it, and that attempting to select a non-existent strategy returns a resource-not-found error.

**Acceptance Scenarios**:

1. **Given** a registered pricing strategy exists with a valid identifier, **When** an authorized administrative request with `X-API-KEY` to select that strategy is received, **Then** the system sets it as the active strategy in the system configuration and returns a successful response.
2. **Given** a pricing strategy is already designated as the active strategy, **When** an authorized administrative request with `X-API-KEY` selects that same strategy again, **Then** the system keeps it as active and returns a successful response without error.
3. **Given** an administrative request is made with a strategy identifier that does not exist in the system, **When** the request is processed, **Then** the system returns a resource-not-found error with a descriptive message.
4. **Given** an administrative request is made with a malformed or non-numeric strategy identifier, **When** the request is processed, **Then** the system rejects the request with a client error indicating invalid input format.

---

### User Story 3 - Register a New Pricing Strategy (Priority: P1)

An authorized administrator needs to register a new pricing strategy into the catalog by providing its initial valuation parameters (such as base value and scale factor) and strategy type identifier.

**Why this priority**: Allows introducing new valuation algorithms and tuning parameters without modifying existing records, enabling progressive evolution of the pricing model.

**Independent Test**: Can be verified by submitting a valid strategy creation payload with the administrative API key, within an execution context where the strategy type resolution is configured/mocked, verifying that the new strategy is persisted and returned with an assigned identifier, and validating that invalid or missing attributes are rejected.

**Acceptance Scenarios**:

1. **Given** a valid strategy creation request containing a valid strategy type, a non-negative base value (≥ 0), and a positive scale factor (> 0) accompanied by `X-API-KEY`, **When** the request is processed in an environment where the strategy type is mapped, **Then** the system persists the strategy, assigns a unique identifier, and returns the created strategy with a successful creation response.
2. **Given** a strategy creation request with a scale factor less than or equal to 0, **When** the request is processed, **Then** the system rejects the request with a validation error indicating that the scale factor must be strictly greater than 0.
3. **Given** a strategy creation request with a negative base value (< 0), **When** the request is processed, **Then** the system rejects the request with a validation error indicating that the base value must be 0 or greater.
4. **Given** a strategy creation request missing required attributes or containing malformed payload structure, **When** the request is processed, **Then** the system rejects the request with a client error indicating invalid request body.
5. **Given** a strategy creation request with an unrecognized or unmapped strategy type, **When** the request is processed, **Then** the system rejects the creation with a descriptive client or validation error.

---

### User Story 4 - Update Strategy Scale Factor (Priority: P2)

An authorized administrator needs to adjust the scale factor of an existing pricing strategy to recalibrate the sensitivity of performance metrics in market value computations.

**Why this priority**: Operational tuning often requires adjusting strategy parameters without having to recreate the strategy or alter other attributes.

**Independent Test**: Can be verified by updating the scale factor of an existing strategy with a valid positive number accompanied by `X-API-KEY` and asserting the updated value, and verifying that non-positive values or non-existent identifiers are rejected.

**Acceptance Scenarios**:

1. **Given** an existing pricing strategy and a valid new scale factor strictly greater than 0 accompanied by `X-API-KEY`, **When** an authorized update request is received for that strategy identifier, **Then** the system updates the scale factor and returns a successful response.
2. **Given** an update request with a scale factor of 0 or a negative number, **When** the request is processed, **Then** the system rejects the update with a validation error indicating that the scale factor must be strictly greater than 0.
3. **Given** an update request for a strategy identifier that does not exist in the system, **When** the request is processed, **Then** the system returns a resource-not-found error.
4. **Given** an update request with a malformed or non-numeric strategy identifier, **When** the request is processed, **Then** the system rejects the request with a client error indicating invalid input format.

---

### User Story 5 - Trigger Bulk Player Valuation Recalculation (Priority: P2)

An authorized administrator needs to trigger the market valuation recalculation process across all players in the catalog using the currently active pricing strategy.

**Why this priority**: Recalculating player valuations applies the active pricing rules to the entire player dataset, keeping market prices synchronized with real-world or simulated performance metrics.

**Independent Test**: Can be verified by setting an active strategy, having players with known metric scores, triggering the valuation process with `X-API-KEY`, and asserting that all player market prices are updated according to the active strategy's calculation formula and a descriptive confirmation message is returned.

**Acceptance Scenarios**:

1. **Given** an active pricing strategy is configured and one or more players exist in the system, **When** an authorized administrative request with `X-API-KEY` to recalculate player valuations is received, **Then** the system computes the new market valuation for all players using the active strategy, saves the updated values, and returns a successful HTTP 200 OK response with the confirmation message `"Cotización de jugadores finalizada exitosamente"`.
2. **Given** no active pricing strategy has been selected in the configuration, **When** an administrative request to recalculate player valuations is received, **Then** the system rejects the operation with an error indicating that no active strategy is configured.
3. **Given** an active pricing strategy is configured but no players exist in the database, **When** an administrative request to recalculate player valuations is received, **Then** the system completes the operation without error and returns a successful HTTP 200 OK confirmation response.

---

### User Story 6 - Access Control and Security Enforcement (Priority: P3)

The platform must enforce API key authentication boundaries on all pricing strategy endpoints under the `/api/admin/estrategias-cotizacion` prefix, ensuring unauthenticated or unauthorized callers cannot view or alter valuation strategies or execute bulk recalculations.

**Why this priority**: Pricing strategies directly govern player asset valuations. Unrestricted access would allow unauthorized actors to manipulate player prices or disrupt system operations.

**Independent Test**: Can be verified by issuing requests to strategy management and recalculation operations without the `X-API-KEY` header or with an invalid key value and confirming that access is denied with an unauthorized response.

**Acceptance Scenarios**:

1. **Given** a request to any pricing strategy endpoint without an `X-API-KEY` header, **When** the request reaches the platform, **Then** access is denied with an unauthorized error response before any controller logic executes.
2. **Given** a request to any pricing strategy endpoint with an invalid or incorrect `X-API-KEY` value, **When** the request reaches the platform, **Then** access is denied with an unauthorized error response.
3. **Given** a request to any pricing strategy endpoint with a valid `X-API-KEY` matching configuration, **When** the request reaches the platform, **Then** the request proceeds to the appropriate controller handler.

---

### Edge Cases

- **Missing or Invalid API Key**: Any request to `/api/admin/estrategias-cotizacion/**` without `X-API-KEY` or with an invalid key is rejected with an unauthorized status.
- **Unmapped Strategy Type**: Registering a strategy with a type that cannot be resolved throws an invalid data usage or validation error.
- **Non-existent Strategy Identifier**: Requests to select or update a strategy with an identifier that does not exist return a resource-not-found error with an informative message.
- **Zero or Negative Scale Factor**: Any attempt to set a scale factor less than or equal to 0 (at creation or during update) is rejected with an invalid input validation error.
- **Negative Base Value**: Any attempt to provide a base value less than 0 at creation is rejected with an invalid input validation error.
- **Missing Active Strategy on Recalculation**: Triggering player quotation when the configuration has no active strategy returns an error indicating the active strategy was not found.
- **Empty Player Catalog**: Triggering player quotation when zero players are registered succeeds gracefully with no error.
- **Empty Strategy Catalog**: Listing strategies when none have been created returns an empty collection.
- **Malformed Identifiers**: Passing non-numeric characters in identifiers returns an invalid input client error.
- **Re-selecting Current Strategy**: Selecting the strategy that is already active succeeds without side effects.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow authorized administrators to retrieve all registered pricing strategies via the `/api/admin/estrategias-cotizacion` path.
- **FR-002**: The system MUST return an empty collection when no pricing strategies exist in the system.
- **FR-003**: The system MUST allow administrators to register a new pricing strategy by supplying strategy configuration parameters (`tipo`, `valorBase`, `factorEscala`).
- **FR-004**: The system MUST validate that base values are greater than or equal to 0 upon strategy registration.
- **FR-005**: The system MUST validate that scale factors are strictly greater than 0 upon strategy registration and update.
- **FR-006**: The system MUST allow administrators to designate an existing pricing strategy as the active strategy for market quotations.
- **FR-007**: The system MUST return a resource-not-found error when selecting a strategy identifier that does not exist.
- **FR-008**: The system MUST allow administrators to update the scale factor of an existing pricing strategy.
- **FR-009**: The system MUST reject scale factor updates where the value is missing, zero, or negative with an invalid input validation error.
- **FR-010**: The system MUST return a resource-not-found error when attempting to update a strategy identifier that does not exist.
- **FR-011**: The system MUST allow administrators to trigger bulk player market price recalculation using the active pricing strategy, returning an HTTP 200 OK status and a confirmation message upon completion.
- **FR-012**: The system MUST return a resource-not-found error when player price recalculation is requested without an active strategy configured.
- **FR-013**: The system MUST complete player price recalculation without error when the player catalog contains zero records.
- **FR-014**: The system MUST protect all pricing strategy endpoints under the `/api/admin/estrategias-cotizacion` route prefix, requiring a valid `X-API-KEY` header for all requests.
- **FR-015**: The system MUST reject malformed or non-numeric identifiers with an invalid input client error.
- **FR-016**: The system MUST provide comprehensive end-to-end verification covering all operations, exercising happy paths, boundary validations, error handling, and security enforcement.
- **FR-017**: Test suites MUST mock the strategy type-to-DTO resolution dynamically within test contexts and MUST NOT add new enum entries to production code for testing purposes.

### Key Entities *(include if feature involves data)*

- **Pricing Strategy**: Represents an algorithm and configuration for determining player market value. Attributes include a unique identifier, a strategy type descriptor, a non-negative base valuation (`valorBase`), and a strictly positive scaling factor (`factorEscala`).
- **Quotation Configuration**: System-level configuration holding the reference to the currently active pricing strategy designated for calculating player market prices.
- **Player**: Entity representing a football player whose current market price is recalculated based on metrics and the active pricing strategy.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of defined pricing strategy operations (creation, listing, selection, parameter updating, and bulk valuation recalculation) are fully accessible under `/api/admin/estrategias-cotizacion`.
- **SC-002**: 100% of end-to-end verification scenarios covering standard flows, boundary constraints, invalid inputs, and API key validation pass consistently in automated validation suites.
- **SC-003**: 100% of invalid parameter inputs (non-positive scale factors, negative base values, missing entities, malformed identifiers) produce consistent, deterministic client error responses.
- **SC-004**: Bulk player valuation updates all eligible player records accurately according to the active strategy formula.
- **SC-005**: 100% of requests missing a valid `X-API-KEY` header are rejected before reaching controller business logic.
- **SC-006**: Production code contains zero test-specific enum modifications or test artifacts.

## Assumptions

- Strategy management and bulk quotation execution are exposed under the administrative route prefix `/api/admin/estrategias-cotizacion` and protected via the `X-API-KEY` header.
- Recalculated player market values are persisted to storage upon completion of the valuation process.
- All monetary valuations calculated for players are constrained to be at least 1 unit, consistent with core business rules.
- Test suites mock static strategy type resolution within scoped test contexts, preventing test-only contamination of production enums.
