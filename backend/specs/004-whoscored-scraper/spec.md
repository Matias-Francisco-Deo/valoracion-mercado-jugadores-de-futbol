# Feature Specification: WhoScored Scraper

**Feature Branch**: `004-whoscored-scraper`

**Created**: 2026-09-14

**Status**: Draft

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Flow 1: Initial Mapping (Priority: P1)

As a backend system, I want to receive a team name and a player name to search WhoScored and obtain the unique player ID, saving it in the database for future queries.

**Why this priority**: This is the blocking prerequisite. Without the ID, metrics cannot be queried.

**Independent Test**: Provide a team name (e.g., "Brest") and a player (e.g., "Ludovic Ajorque"). The system navigates the corresponding URLs using a native HTTP client, extracts the JSONs, and returns the ID `234364`.

**Acceptance Scenarios**:

1. **Given** a valid team and player, **When** Flow 1 is executed, **Then** the system extracts the team ID from the league's JSON, uses that URL to extract the player ID from the team's JSON, and returns it.

---

### User Story 2 - Flow 2: Weekly Update (Priority: P1)

As a backend system, I want to receive a WhoScored ID and extract the mandatory player metrics by directly reading the initial JSON state, to update the weekly market value without relying on the DOM.

**Why this priority**: This is the core value of the system (Points 1 and 3.2 of the Vision Document).

**Independent Test**: Provide the WhoScored ID. The system downloads the HTML with an HTTP Client, uses Regex to isolate the `<script>` tag, and returns the parsed metrics.

**Acceptance Scenarios**:

1. **Given** a player ID, **When** Flow 2 is executed, **Then** the system returns goals, assists, shots on target, passes, interceptions, and rating.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001 (CRITICAL ARCHITECTURE)**: It is STRICTLY FORBIDDEN to use DOM parsing (JSoup/CSS selectors) or headless browsers for table extraction.
- **FR-002**: All data extraction MUST be done using Regular Expressions (Regex) isolating the `<script>` blocks that contain the initial state JSONs, parsing them afterwards.
- **FR-003 (Flow 1 - Mapping)**: The system MUST receive "Team Name" and "Player Name".
- **FR-004 (Flow 1 - Mapping)**: The system MUST navigate the league URL to find and extract the team ID.
- **FR-005 (Flow 1 - Mapping)**: The system MUST use the team URL to find and extract the WhoScored player ID.
- **FR-006 (Flow 2 - Extraction)**: The system MUST receive only the "WhoScored ID".
- **FR-007 (Flow 2 - Extraction)**: The system MUST extract the mandatory metrics from the player's JSON: goals, assists, shots on target, passes, interceptions, and rating.

### Key Entities *(include if feature involves data)*

- **WhoScoredIdentity**: Transient entity linking Name -> TeamID -> PlayerID.
- **WeeklyMetrics**: Structured DTO with the required statistics extracted from the JSON.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Flow 1 successfully resolves the correct player ID at runtime.
- **SC-002**: Flow 2 extracts the metrics without excessive memory usage or timeouts (characteristic of avoiding headless browsers).
- **SC-003**: The Regex successfully isolates the JSON in 100% of valid profiles.

## Assumptions

- The injected JSONs in WhoScored's `<script>` blocks maintain a predictable structure that can be parsed by standard libraries like Jackson.
- League and Team URLs follow a deterministic pattern or can be resolved via internal search.
