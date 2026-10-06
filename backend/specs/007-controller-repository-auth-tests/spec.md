# Feature Specification: Controller, Repository, Auth, and JSON Extraction Test Coverage

**Feature Branch**: `007-controller-repository-auth-tests`

**Created**: 2026-10-06

**Status**: Draft

**Input**: User description: "You have to do tests for ExternalPlayerController, for PlayerGameDataRepositoryImpl, ApiKeyAuthFilter, and JsonExtractorUtil, following the constitution rules for testing"

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Verify the external player admin endpoint behavior (Priority: P1)

The administrative endpoint for manually triggering player refresh must accept valid requests, delegate work to the service layer, return the correct HTTP status, and avoid creating inconsistent behavior when the service is invoked.

**Why this priority**: This endpoint is the entry point for manual data refresh operations, so the test suite must prove that the controller behaves consistently when the system initiates a background update.

**Independent Test**: Can be fully tested by invoking the controller action with a mocked service and asserting the immediate accepted response and the service invocation.

**Acceptance Scenarios**:

1. **Given** a valid admin request to trigger a player refresh, **When** the controller action is invoked, **Then** the service method responsible for the asynchronous refresh is called exactly once and the endpoint returns an accepted response.
2. **Given** the service layer is available and the controller is exercised under standard conditions, **When** the request is processed, **Then** the system returns a clear success message indicating the scraper was started in the background without blocking the caller.
3. **Given** the controller receives a normal request in the expected admin route, **When** the action completes, **Then** it does not throw an exception or return a misleading success state.

---

### User Story 2 - Verify persistence behavior for player game data (Priority: P1)

The repository layer must translate domain game data into the persistence DTO, save it through the JPA data access component, and return the expected data when a valid entity is requested.

**Why this priority**: The repository is the boundary between domain logic and storage, and test coverage must protect data integrity and expected retrieval semantics.

**Independent Test**: Can be fully tested by creating a player game data object, persisting it through the repository, and verifying the expected persistence call and retrieval behavior without relying on arbitrary system behavior.

**Acceptance Scenarios**:

1. **Given** a valid `PlayerGameData` instance and a repository dependency that accepts persistence operations, **When** `guardar` is called, **Then** the repository converts the model to the JPA DTO and persists it without losing the domain values.
2. **Given** a repository instance backed by a real or mocked JPA DAO, **When** a valid game data record is requested by identifier, **Then** the repository returns the equivalent domain data when it exists.
3. **Given** a missing identifier or absent persisted record, **When** `recuperar` is invoked, **Then** the repository returns an empty result instead of a null or invalid state.

---

### User Story 3 - Verify API key enforcement for scraper admin routes (Priority: P1)

The security filter must restrict access to admin scraper endpoints to callers that present a valid API key, while allowing unrelated routes to continue normally.

**Why this priority**: The scraper admin path exposes privileged functionality; unvalidated access would allow unauthorized use of background scraping and data extraction processes.

**Independent Test**: Can be fully tested by creating HTTP requests with and without the API key header and asserting whether the request continues to the chain or is rejected with an unauthorized status.

**Acceptance Scenarios**:

1. **Given** a request to the scraper admin path without an `X-API-KEY` header, **When** the filter runs, **Then** the request is rejected with an unauthorized response and the chain is not continued.
2. **Given** a request to the scraper admin path with a valid API key matching the configured value, **When** the filter runs, **Then** the request continues through the filter chain without interruption.
3. **Given** a request to a non-admin or non-scraper route, **When** the filter evaluates the URI, **Then** it allows the request to proceed without checking or rejecting the API key.
4. **Given** a request to the scraper admin path with an incorrect key value, **When** the filter runs, **Then** the request is rejected as unauthorized regardless of whether the header is present.

---

### User Story 4 - Verify JSON extraction utilities and position parsing (Priority: P2)

The JSON extraction utility must locate and isolate the player statistics payload from scraper HTML, while still handling malformed or partial input without returning corrupted data. The position extraction helper must resolve standard position metadata when available and safely return null for unsupported or absent values.

**Why this priority**: These utilities sit directly on the boundary between scraped HTML and domain processing. Their correctness is necessary to prevent silent parsing errors and invalid player data.

**Independent Test**: Can be fully tested with representative HTML samples containing expected script blocks, malformed payloads, and empty content, asserting that the utility either extracts the valid JSON or raises a controlled extraction exception.

**Acceptance Scenarios**:

1. **Given** a valid HTML document containing the expected `require.config.params['args']` script block, **When** `extractPlayerStatsJson` is invoked, **Then** the clean JSON payload is returned without additional script noise.
2. **Given** an empty or null HTML payload, **When** `extractPlayerStatsJson` is called, **Then** it throws a domain-specific extraction exception instead of returning invalid content.
3. **Given** HTML containing a valid `meta description` or fallback position marker, **When** `extractPlayerPosition` is invoked, **Then** it returns the normalized position label.
4. **Given** HTML without the expected structural markers, **When** `extractPlayerPosition` is executed, **Then** it returns null rather than crashing or producing a misleading value.

---

### Edge Cases

- What happens when the controller is invoked while the service layer throws an exception or is unavailable?
- What happens when the repository attempts to persist an empty or partial `PlayerGameData` payload?
- What happens when the API key header is present but empty, whitespace-only, or mismatched?
- What happens when the scraper admin path is accessed using uppercase, encoded, or trailing slash variations?
- What happens when the HTML payload contains the JSON block without the exact script quoting style used in the extractor?
- What happens when the response page is missing a meta description, position node, or fallback position label?
- What happens when the JSON extraction utility encounters nested braces or malformed script termination in the page source?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The test suite for `ExternalPlayerController` MUST verify that the endpoint returns an accepted HTTP response and invokes the asynchronous refresh service exactly once in the standard happy path.
- **FR-002**: The test suite for `ExternalPlayerController` MUST verify that the controller does not obscure service exceptions or produce a false success state when the asynchronous operation is triggered.
- **FR-003**: The repository test suite MUST verify that `PlayerGameDataRepositoryImpl` persists domain data through the JPA DTO conversion without dropping required fields.
- **FR-004**: The repository test suite MUST verify that `PlayerGameDataRepositoryImpl` returns the expected domain object when a record exists and an empty result when no record is present.
- **FR-005**: The security test suite MUST verify that `ApiKeyAuthFilter` blocks scraper admin paths when the `X-API-KEY` header is missing, empty, or invalid.
- **FR-006**: The security test suite MUST verify that `ApiKeyAuthFilter` permits non-scraper routes and valid scraper requests to continue through the filter chain.
- **FR-007**: The utility test suite MUST verify that `JsonExtractorUtil.extractPlayerStatsJson` extracts the expected JSON payload from valid HTML and throws a controlled extraction exception when input is empty, null, or malformed.
- **FR-008**: The utility test suite MUST verify that `JsonExtractorUtil.extractPlayerPosition` recognizes standard position markers and returns null when the structure is absent or unsupported.
- **FR-009**: All tests MUST be aligned with the project's testing constitution by proving the most common behavior first, covering boundary conditions next, and preventing flakiness or dependency on live external systems.
- **FR-010**: The test suite MUST keep the architecture boundaries intact by validating controller behavior at the controller boundary, repository behavior at the persistence boundary, security behavior at the filter boundary, and HTML parsing behavior at the utility boundary.

### Key Entities *(include if feature involves data)*

- **Player Game Data**: The domain object representing the persisted player statistics used by the repository boundary.
- **External Player Controller**: The admin endpoint responsible for triggering the asynchronous external player refresh workflow.
- **Player Game Data Repository**: The persistence abstraction that saves and retrieves player data using a JPA-backed implementation.
- **API Key Security Filter**: The request filter that guards access to privileged scraper routes.
- **JSON Extraction Utility**: The parser responsible for extracting embedded JSON and player position metadata from scraped HTML content.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: The targeted test suite for these four components executes successfully in the local project environment with all relevant assertions passing.
- **SC-002**: The suite covers the primary happy path plus the most important edge conditions for each boundary: controller invocation, persistence save/retrieval, API key enforcement, and HTML extraction failure handling.
- **SC-003**: The project maintains deterministic behavior by ensuring the tests do not rely on real external services, live network access, or non-deterministic runtime state.
- **SC-004**: The coverage includes both standard flows and boundary behavior where invalid or missing data is explicitly handled instead of silently passing through.
- **SC-005**: The suite remains aligned with the architecture and testing rules by validating the correct responsibility boundary for each class without mixing concerns across layers.

## Assumptions

- The test suite will be scoped to the four identified classes and will not alter unrelated existing behavior.
- Targeted validation will use the project's testing conventions and maintain strict separation between controller, repository, security, and utility responsibilities.
- For repository and controller checks, the behavior is validated through deterministic dependencies and boundary-level assertions rather than full end-to-end system execution.
- Missing or malformed HTML and API key scenarios are treated as explicit failure events, consistent with the project's error-handling expectations.
- Existing functionality outside this scope is preserved unless a failing test demonstrates a direct defect in the targeted components.
