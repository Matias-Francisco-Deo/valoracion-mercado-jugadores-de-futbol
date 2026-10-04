# Feature Specification: WhoScored Scraper and HTTP Client Test Suite

**Feature Branch**: `006-whoscored-scraper-tests`

**Created**: 2026-10-01

**Status**: Draft

**Input**: User description: "We are going to develop tests for this particular class ExternalPlayerWhoScoredScrapper, mocking every real life connection with other websites. Also we have to test its httpClient ScraperHttpClient, also mocking its real life connections."

## Clarifications

### Session 2026-10-01

- Q: What mocking approach should be used to test ScraperHttpClient without connecting to external websites? → A: Option A (Use a local MockWebServer on localhost so Playwright's headless Chromium navigates exclusively to local simulated responses with zero outbound internet traffic).
- Q: Should the test class for ExternalPlayerWhoScoredScrapper be structured as a pure JUnit 5 and Mockito unit test named ExternalPlayerWhoScoredScrapperTest, or should it run within a @SpringBootTest context? → A: Option B (Spring-managed test with `@SpringBootTest` using `@MockBean` for the HTTP client).
- Q: May we rename and replace the empty stub file `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/ExternalPlayerWhoScoredScrapper.java` with the standard test class `ExternalPlayerWhoScoredScrapperTest` in package `com.overcode.persistence.repository.dao.external.scrapper.whoscored`? → A: Option A (Affirmative permission granted by project owner to replace and rename the empty stub file into `ExternalPlayerWhoScoredScrapperTest` under the matching package `com.overcode.persistence.repository.dao.external.scrapper.whoscored`).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Verify WhoScored Scraper Data Extraction and Metric Aggregation (Priority: P1)

The external data extraction engine (`ExternalPlayerWhoScoredScrapper`) is responsible for retrieving and transforming raw WhoScored player stats into domain Player entities. The test suite must verify that player statistics across top-5 European leagues are accurately extracted, filtered, accumulated, and normalized without performing any real network requests.

**Why this priority**: Accurate extraction and calculation of metrics (goals, assists, shots on target, passes, interceptions, tackles, rating) are the foundation of player valuation. Erroneous calculations directly compromise the entire platform's market valuation model.

**Independent Test**: Can be fully tested by providing simulated HTML payloads containing realistic WhoScored JSON state for a player, invoking the scraper with a mocked HTTP client, and asserting that the returned Player entity contains all expected aggregated metrics, rounded ratings, and draft metadata.

**Acceptance Scenarios**:

1. **Given** a player participating in one or more Top-5 leagues (Premier League, LaLiga, Serie A, Bundesliga, Ligue 1) and a simulated HTML response with valid embedded statistics, **When** `getDatosDeJugador` is invoked, **Then** the system returns a populated `Player` object containing accumulated goals, assists, shots on target, pass accuracy percentage, defensive actions, and weighted average rating matching the mock data.
2. **Given** a player who has appearances across both Top-5 leagues and non-Top-5 competitions (such as domestic cups or non-top-5 leagues), **When** `getDatosDeJugador` is executed, **Then** only the statistics from the Top-5 leagues are accumulated into totals, ignoring non-Top-5 tournament records.
3. **Given** a player draft DTO with specific name, club, and league attributes, **When** `getDatosDeJugador` produces a player, **Then** the resulting Player entity retains the draft metadata alongside the extracted external identifier.

---

### User Story 2 - Verify Scraper Boundary Handling and Extraction Exceptions (Priority: P2)

The scraper must safely handle abnormal, incomplete, or corrupted responses from the external source, ensuring predictable failures and diagnostic errors rather than unhandled crashes or inconsistent data states.

**Why this priority**: Web pages and external responses frequently change, fail, or deliver unexpected structures. The application must gracefully reject malformed data and invalid player states to maintain database integrity.

**Independent Test**: Can be fully tested by feeding corrupted JSON, missing required nodes, or payloads representing players without top-5 league appearances, asserting that explicit scraper extraction exceptions are thrown with informative messages.

**Acceptance Scenarios**:

1. **Given** a player payload where the player registers 0 appearances across all Top-5 leagues, **When** `getDatosDeJugador` is called, **Then** the system throws an extraction exception indicating that the player has no recorded activity in the top 5 leagues.
2. **Given** a response where the embedded JSON lacks the required tournament statistics structure or contains invalid syntax, **When** `getDatosDeJugador` is called, **Then** the system catches the parser failure and throws a scraper extraction exception.
3. **Given** an HTML response that does not contain the expected initial state script block or has an empty body, **When** `getDatosDeJugador` is called, **Then** the extraction fails fast with a descriptive scraper extraction exception.
4. **Given** a player with zero recorded pass attempts or zero rated appearances, **When** `getDatosDeJugador` is called, **Then** the calculation handles boundary divisions safely (0% pass accuracy, 0.0 rating) without arithmetic errors.

---

### User Story 3 - Verify Scraper HTTP Client Resilience, Anti-Bot Handling, and Resource Management (Priority: P1)

The scraper HTTP client (`ScraperHttpClient`) is responsible for executing browser-based page navigation, avoiding bot challenge blocks, ignoring unnecessary network assets (images, stylesheets, trackers), and handling retries upon temporary network or challenge failures without hitting live external endpoints.

**Why this priority**: The HTTP client is the front-line gateway to external data. Testing its retry logic, asset filtering, and anti-bot challenge response handling without relying on live external websites ensures deterministic, reliable, and fast continuous integration.

**Independent Test**: Can be fully tested by exercising `ScraperHttpClient` against a local `MockWebServer` serving simulated responses on `localhost`, asserting proper retry behavior upon encountering anti-bot challenges or errors, correct asset blocking, and resource lifecycle management.

**Acceptance Scenarios**:

1. **Given** a simulated page served by local `MockWebServer` that completes successfully, **When** `getHtml` is executed with the local URL, **Then** the client returns the full HTML string and terminates the page context cleanly.
2. **Given** an initial request that encounters an anti-bot challenge (such as a Cloudflare verification page on `MockWebServer`), **When** the client evaluates the response, **Then** it triggers an automatic retry; if the subsequent attempt succeeds, the HTML is returned.
3. **Given** repeated requests that continuously fail or exceed the maximum retry count with anti-bot challenges or navigation errors on `MockWebServer`, **When** `getHtml` exhausts its retry limit, **Then** the system throws an extraction exception indicating the failure reason.
4. **Given** incoming resource requests during page navigation for media, stylesheets, fonts, ads, or tracking pixels, **When** network routes are evaluated, **Then** superfluous resource requests are aborted while primary document and script assets are allowed.
5. **Given** the initialization and shutdown phases of the client component, **When** lifecycle hooks run, **Then** the underlying browser engine instances are initialized and disposed of without resource leaks.

---

### Edge Cases

- What happens when a player participates in multiple clubs or multiple tournaments within the same season across Top-5 leagues?
- What happens when a player has 0 attempted passes (potential division by zero in pass accuracy calculation)?
- What happens when a player has matches played but none have an assigned rating (or rating is 0.0)?
- What happens when the HTTP client receives an empty HTML string or an immediate timeout during page loading?
- What happens when the external website serves unexpected HTML structure without the target script tags?
- What happens when the browser encounters an unrecoverable crash during navigation?
- What happens when the maximum retry attempts are reached without overcoming a challenge page?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The test suite for `ExternalPlayerWhoScoredScrapper` MUST be executed within a `@SpringBootTest` context as `ExternalPlayerWhoScoredScrapperTest` in package `com.overcode.persistence.repository.dao.external.scrapper.whoscored`, using `@MockBean` to substitute `ScraperHttpClient` with mock responses to verify player statistic parsing without making live external HTTP requests.
- **FR-002**: The test suite MUST verify that `ExternalPlayerWhoScoredScrapper` filters tournament data to exclusively accumulate statistics from Top-5 European leagues (Premier League: 2, LaLiga: 4, Serie A: 5, Bundesliga: 3, Ligue 1: 22).
- **FR-003**: The test suite MUST verify that `ExternalPlayerWhoScoredScrapper` correctly computes weighted ratings based on games played per tournament.
- **FR-004**: The test suite MUST verify that `ExternalPlayerWhoScoredScrapper` calculates pass completion percentage accurately and handles zero pass attempts without error.
- **FR-005**: The test suite MUST verify that `ExternalPlayerWhoScoredScrapper` throws a `ScraperExtractionException` when a player has 0 appearances in Top-5 leagues.
- **FR-006**: The test suite MUST verify that `ExternalPlayerWhoScoredScrapper` throws a `ScraperExtractionException` when the tournament JSON node is missing, malformed, or not an array.
- **FR-007**: The test suite MUST verify that `ExternalPlayerWhoScoredScrapper` propagates or wraps extraction exceptions when HTML or JSON parsing fails.
- **FR-008**: The test suite MUST verify that `ScraperHttpClient` successfully returns page HTML when navigation succeeds, using a local `MockWebServer` on `localhost` with zero external network calls.
- **FR-009**: The test suite MUST verify that `ScraperHttpClient` intercepts network requests to abort images, stylesheets, fonts, media, and third-party trackers.
- **FR-010**: The test suite MUST verify that `ScraperHttpClient` detects anti-bot challenge indicators (e.g., Cloudflare verification pages) and initiates a retry up to the configured limit against `MockWebServer`.
- **FR-011**: The test suite MUST verify that `ScraperHttpClient` throws a `ScraperExtractionException` when the retry limit is exceeded after persistent challenge or navigation failures.
- **FR-012**: The test suite MUST verify that `ScraperHttpClient` properly initializes and cleans up browser resources during lifecycle management.
- **FR-013**: All tests MUST run in complete isolation from external internet dependencies, ensuring deterministic execution with zero reliance on live third-party servers.
- **FR-014**: With affirmative permission granted by the project owner, the empty stub file `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/ExternalPlayerWhoScoredScrapper.java` MUST be renamed and replaced by `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`. All other pre-existing tests in the application MUST be strictly preserved without modification or deletion.

### Key Entities *(include if feature involves data)*

- **Simulated Player Statistics Payload**: Mocked HTML/JSON fixture containing representative WhoScored state with tournament arrays, player metric counts, and ratings.
- **Player Extraction Engine (`ExternalPlayerWhoScoredScrapper`)**: Component under test responsible for orchestrating the retrieval of player HTML, isolating state JSON, and converting it to domain player models.
- **Scraper HTTP Client (`ScraperHttpClient`)**: Component under test responsible for executing headless browser navigation, resource filtering, anti-bot detection, and retry orchestration.
- **Mock Web Server (`MockWebServer`)**: Local HTTP server (`okhttp3.mockwebserver`) delivering predetermined mock HTML responses and challenge simulations on `localhost` without external network activity.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of tests in the suite execute and pass in a fully offline/isolated environment without making live network connections to external websites.
- **SC-002**: Test execution is deterministic, achieving 0% flakiness across repeated executions.
- **SC-003**: The test suite achieves comprehensive coverage of all branching paths in `ExternalPlayerWhoScoredScrapper` (happy path, multi-league aggregation, zero games played, malformed JSON/HTML, zero pass attempts).
- **SC-004**: The test suite achieves comprehensive coverage of all operational flows in `ScraperHttpClient` (successful fetch, retry on challenge, failure on exhausted retries, resource abort filtering, lifecycle setup/cleanup).
- **SC-005**: All tests complete execution within standard unit/component test timeframes (total test suite execution under 15 seconds).

## Assumptions

- Test fixtures for WhoScored HTML and JSON accurately reflect the structure expected by `JsonExtractorUtil` and Jackson object mapping.
- `MockWebServer` operates on a dynamic loopback port on `localhost` without firewall or port conflict issues.
- `ExternalPlayerWhoScoredScrapper` is tested via Spring Boot test context with `@MockBean` for `ScraperHttpClient`.
- `ScraperHttpClient` is tested via component tests with local `MockWebServer` and Playwright Chromium.
- Pre-existing test `ExternalPlayerDAOWhoScoredImplTest` and other unrelated tests remain untouched.
- Top-5 league IDs (2, 4, 5, 3, 22) are stable domain identifiers used for filtering tournament stats.
