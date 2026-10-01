# Phase 0 Research: WhoScored Scraper and HTTP Client Test Suite

**Feature**: WhoScored Scraper and HTTP Client Test Suite  
**Branch**: `006-whoscored-scraper-tests`  
**Date**: 2026-10-01  

## Technical Context & Decisions

### Decision 1: Scraper Testing Strategy (`ExternalPlayerWhoScoredScrapperTest`)
- **Decision**: Execute tests within `@SpringBootTest` context, using Spring's `@MockBean` on `ScraperHttpClient` to provide mocked HTML responses directly to `ExternalPlayerWhoScoredScrapper`.
- **Rationale**: 
  - Aligns with the project's Spring Boot conventions and the user's explicit clarification preference (Option B).
  - Guarantees 0 external HTTP calls because `httpClient.getHtml(anyString())` is fully intercepted by Mockito before any network code is executed.
  - Allows full verification of Jackson JSON parsing, league filtering logic, aggregation formulas, weighted ratings, and custom domain exceptions (`ScraperExtractionException`).
- **Alternatives considered**:
  - *Pure JUnit 5 / Mockito unit test without Spring*: Runs faster, but was deprioritized in favor of `@SpringBootTest` per user clarification.
  - *Integration test with live Playwright*: Rejected because it violates the critical requirement of 0 real-life network calls.

### Decision 2: HTTP Client Isolation Strategy (`ScraperHttpClientTest`)
- **Decision**: Use `okhttp3.mockwebserver.MockWebServer` running on `localhost` (loopback interface) to serve controlled simulated HTTP responses to Playwright's Chromium browser.
- **Rationale**:
  - `mockwebserver` is already declared in `pom.xml` (version 4.12.0) and used across the codebase (`ExternalPlayerDAOFootballDataAPIImplTest`).
  - Chromium runs in real headless mode, allowing Playwright's actual request routing (`page.route("**/*", ...)`), DOMContentLoaded wait states, and Cloudflare challenge detection logic to execute authentically.
  - Operating against `http://localhost:<dynamic-port>` guarantees 0 outbound traffic to external websites, perfectly satisfying the user's requirement.
- **Alternatives considered**:
  - *Mockito mock of Playwright `Page`, `BrowserContext`, `Browser`*: Rejected because Playwright uses a deep fluent builder API that is brittle to mock and would bypass testing the actual route abort filters and Playwright timeout handlers.
  - *WireMock*: Rejected because `MockWebServer` is already present in `pom.xml`.

### Decision 3: Test Method Naming & Language
- **Decision**: All test methods MUST be written in Latin American Spanish using camelCase, following existing project conventions (e.g., `encuentraJugadorConDatosMock` in `ExternalPlayerDAOWhoScoredImplTest`).
- **Rationale**:
  - Explicit user requirement: *"Remember to put the test names in latin american spanish."*
  - Follows Constitution Principle IV: *"Test names MUST clearly represent the behavior under test; if a test needs extra context, the author MUST add a clarifying comment."*
- **Spanish Method Catalog**:
  - `extraeMetricasDeJugadorConExitoYFiltraTorneosNoTop5()`
  - `calculaRatingPonderadoYPorcentajeDePasesExitososCorrectamente()`
  - `manejaSinErroresJugadorConCeroPasesIntentadosYCeroPartidosConRating()`
  - `lanzaExcepcionCuandoJugadorNoRegistraPartidosEnLasCincoLigasPrincipales()`
  - `lanzaExcepcionCuandoElJsonNoContieneElNodoTournamentsOEsInvalido()`
  - `lanzaExcepcionCuandoElHtmlNoContieneElBloqueJsonEsperado()`
  - `lanzaExcepcionCuandoElJsonTieneSintaxisCorrupta()`
  - `propagaExcepcionCuandoElHttpClientFalla()`
  - `obtieneHtmlExitosamenteDesdeMockWebServer()`
  - `reintentaYObtieneHtmlCuandoElPrimerIntentoEncuentraDesafioCloudflare()`
  - `lanzaExcepcionCuandoSeSuperanLosReintentosPorDesafioCloudflare()`
  - `lanzaExcepcionCuandoLaNavegacionFallaCompletamenteTrasReintentos()`
  - `abortaPeticionesDeImagenesEstilosYTrackersDuranteLaNavegacion()`
  - `inicializaYCierraRecursosDeNavegadorCorrectamente()`

### Decision 4: Playwright Lifecycle Management in Tests
- **Decision**: In `ScraperHttpClientTest`, initialize one shared headless browser instance via `@BeforeAll` and close it in `@AfterAll`, while resetting `MockWebServer` between tests.
- **Rationale**:
  - Launching Chromium takes ~1.5 to 2.5 seconds. Reusing the browser instance across test methods cuts the test suite runtime from ~15 seconds to ~3 seconds.
  - Each `getHtml(url)` invocation creates and closes its own fresh `BrowserContext` and `Page` inside a try-with-resources block, ensuring complete test isolation without browser leakage.
- **Alternatives considered**:
  - *Launching a new browser before each test*: Unnecessarily slow (~15-20s for 6 tests).

### Decision 5: Anti-Bot & Cloudflare Challenge Simulation
- **Decision**: Simulate Cloudflare challenge pages by enqueueing a mock HTTP response containing the exact substrings evaluated by `ScraperHttpClient`: `"Cloudflare"` and `"Checking your browser"`.
- **Rationale**:
  - `ScraperHttpClient.getHtmlPlaywright` specifically checks `content.contains("Cloudflare") && content.contains("Checking your browser")`.
  - Enqueueing this HTML body verifies the retry mechanism on attempt 1 and the terminal exception on attempt 2 deterministically.
