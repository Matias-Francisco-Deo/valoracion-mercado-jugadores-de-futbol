# Tasks: WhoScored Scraper and HTTP Client Test Suite

**Input**: Design documents from `/specs/006-whoscored-scraper-tests/`  
**Prerequisites**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), [contracts/test-suite-contract.md](contracts/test-suite-contract.md)  
**Tests**: Automated test suite implementation for [`ExternalPlayerWhoScoredScrapper`](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/src/main/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapper.java) and [`ScraperHttpClient`](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/src/main/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClient.java)  
**Organization**: Tasks are grouped by user story to enable independent implementation and testing of each story.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (e.g., US1, US2, US3)
- Include exact file paths in descriptions

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Verify dependencies, clean up deprecated stubs, and prepare package directory structure

- [X] T001 Verify test dependencies and Playwright/MockWebServer test configuration in `pom.xml`
- [X] T002 [P] Remove obsolete empty stub test file `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/ExternalPlayerWhoScoredScrapper.java` per FR-014 permission
- [X] T003 [P] Create package directories for new test classes in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/` and `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Shared mock payloads and test fixtures that MUST be complete before user stories are tested

**⚠️ CRITICAL**: Foundational fixtures must be available before user story tests can execute

- [X] T004 [P] Implement mock HTML and JSON payload fixtures in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/WhoScoredTestFixtures.java`
- [X] T005 [P] Initialize `@SpringBootTest` test class skeleton with `@MockBean ScraperHttpClient` in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`
- [X] T006 [P] Initialize Playwright test class skeleton with `MockWebServer` lifecycle setup and teardown in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java`

**Checkpoint**: Shared test fixtures and skeletons ready - user story testing can now begin

---

## Phase 3: User Story 1 - Verify WhoScored Scraper Data Extraction and Metric Aggregation (Priority: P1) 🎯 MVP

**Goal**: Verify that `ExternalPlayerWhoScoredScrapper` correctly queries player statistics, isolates Top-5 league tournaments (Premier League: 2, LaLiga: 4, Serie A: 5, Bundesliga: 3, Ligue 1: 22), aggregates performance metrics, calculates weighted ratings and pass accuracy, and constructs the domain `Player` model without making real network requests.

**Independent Test**: Execute `.\mvnw.cmd test -Dtest=ExternalPlayerWhoScoredScrapperTest#extraeMetricasDeJugadorConExitoYFiltraTorneosNoTop5,ExternalPlayerWhoScoredScrapperTest#calculaRatingPonderadoYPorcentajeDePasesExitososCorrectamente` to verify happy-path extraction and weighted aggregation against mocked HTML in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`.

### Implementation for User Story 1

- [X] T007 [US1] Implement test `extraeMetricasDeJugadorConExitoYFiltraTorneosNoTop5` verifying accumulation of goals, assists, shots, tackles, preservation of player draft metadata (name, club, league, externalId), and exclusion of non-Top-5 tournaments in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`
- [X] T008 [US1] Implement test `calculaRatingPonderadoYPorcentajeDePasesExitososCorrectamente` verifying weighted average rating and rounded pass accuracy in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`
- [X] T009 [US1] Validate User Story 1 test execution passes cleanly with 0 outbound network calls via `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`

**Checkpoint**: At this point, User Story 1 should be fully functional and testable independently (MVP complete)

---

## Phase 4: User Story 3 - Verify Scraper HTTP Client Resilience, Anti-Bot Handling, and Resource Management (Priority: P1)

**Goal**: Verify that `ScraperHttpClient` executes headless Chromium navigation via Playwright against local loopback `MockWebServer`, intercepts and aborts unneeded media/stylesheet/tracker assets, handles anti-bot challenge retries, handles fatal navigation failures, and manages browser lifecycle without external network connections.

**Independent Test**: Execute `.\mvnw.cmd test -Dtest=ScraperHttpClientTest` to verify that Playwright Chromium navigates exclusively against `MockWebServer` on localhost, properly retrying on Cloudflare challenges, aborting media assets, and cleaning up browser instances in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java`.

### Implementation for User Story 3

- [ ] T010 [US3] Implement test `obtieneHtmlExitosamenteDesdeMockWebServer` verifying successful page navigation and HTML extraction from local loopback in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java`
- [ ] T011 [US3] Implement test `reintentaYObtieneHtmlCuandoElPrimerIntentoEncuentraDesafioCloudflare` simulating Cloudflare response on attempt 1 followed by valid HTML on attempt 2 in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java`
- [ ] T012 [US3] Implement test `lanzaExcepcionCuandoSeSuperanLosReintentosPorDesafioCloudflare` verifying `ScraperExtractionException` when challenge persists across maxRetries in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java`
- [ ] T013 [US3] Implement test `lanzaExcepcionCuandoLaNavegacionFallaCompletamenteTrasReintentos` simulating unrecoverable server connection failure in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java`
- [ ] T014 [US3] Implement test `abortaPeticionesDeImagenesEstilosYTrackersDuranteLaNavegacion` verifying route filtering for images, stylesheets, fonts, and trackers in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java`
- [ ] T015 [US3] Implement test `inicializaYCierraRecursosDeNavegadorCorrectamente` verifying Playwright lifecycle methods `init()` and `cleanup()` in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java`
- [ ] T016 [US3] Validate User Story 3 test execution passes cleanly with 0 outbound network calls via `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java`

**Checkpoint**: At this point, User Stories 1 AND 3 both work and pass independently

---

## Phase 5: User Story 2 - Verify Scraper Boundary Handling and Extraction Exceptions (Priority: P2)

**Goal**: Verify that `ExternalPlayerWhoScoredScrapper` safely handles edge cases (zero pass attempts, zero rated games) without division-by-zero errors, and throws explicit `ScraperExtractionException` for players with zero Top-5 league games, missing tournaments node, corrupt JSON syntax, unparseable HTML, and upstream HTTP client failures.

**Independent Test**: Execute `.\mvnw.cmd test -Dtest=ExternalPlayerWhoScoredScrapperTest#manejaSinErrores*,ExternalPlayerWhoScoredScrapperTest#lanzaExcepcion*,ExternalPlayerWhoScoredScrapperTest#propagaExcepcion*` to verify boundary handling and exception propagation in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`.

### Implementation for User Story 2

- [ ] T017 [US2] Implement test `manejaSinErroresJugadorConCeroPasesIntentadosYCeroPartidosConRating` verifying safe zero handling in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`
- [ ] T018 [US2] Implement test `lanzaExcepcionCuandoJugadorNoRegistraPartidosEnLasCincoLigasPrincipales` verifying exception on zero Top-5 appearances in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`
- [ ] T019 [US2] Implement test `lanzaExcepcionCuandoElJsonNoContieneElNodoTournamentsOEsInvalido` verifying exception on missing tournaments array in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`
- [ ] T020 [US2] Implement test `lanzaExcepcionCuandoElHtmlNoContieneElBloqueJsonEsperado` verifying exception on missing script block in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`
- [ ] T021 [US2] Implement test `lanzaExcepcionCuandoElJsonTieneSintaxisCorrupta` verifying Jackson parsing exception wrapping in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`
- [ ] T022 [US2] Implement test `propagaExcepcionCuandoElHttpClientFalla` verifying exception propagation from mocked client in `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`
- [ ] T023 [US2] Validate User Story 2 test execution passes cleanly with 0 outbound network calls via `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`

**Checkpoint**: All user stories (US1, US2, US3) are now independently functional and fully verified

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Full regression suite validation, execution timing verification, and documentation updates across all user stories

- [ ] T024 [P] Run full test suite validation via `.\mvnw.cmd test -Dtest=*ScrapperTest,*ScraperHttpClientTest` in `backend/` to verify zero regressions and all 14 test methods pass under 15 seconds
- [ ] T025 [P] Verify pre-existing test suite integrity `.\mvnw.cmd test -Dtest=ExternalPlayerDAOWhoScoredImplTest` in `backend/` ensuring no existing test regressions per Constitution Principle Additional Constraints
- [ ] T026 Validate complete end-to-end execution flow against `specs/006-whoscored-scraper-tests/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion - BLOCKS all user story tests.
- **User Stories (Phase 3+)**: All depend on Foundational phase completion.
  - User Story 1 (P1) and User Story 3 (P1) can proceed in parallel once Foundation is ready (separate test files: `ExternalPlayerWhoScoredScrapperTest.java` vs `ScraperHttpClientTest.java`).
  - User Story 2 (P2) builds upon User Story 1's test class `ExternalPlayerWhoScoredScrapperTest.java` by adding edge cases and exception handling tests.
- **Polish (Final Phase)**: Depends on all user stories being complete.

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational (Phase 2) - No dependencies on other stories.
- **User Story 3 (P1)**: Can start after Foundational (Phase 2) - Operates on `ScraperHttpClientTest.java`, fully independent of US1/US2. Scheduled in Phase 4 ahead of User Story 2 because US3 is priority P1.
- **User Story 2 (P2)**: Extends `ExternalPlayerWhoScoredScrapperTest.java` created in US1 - Depends on US1 completion and scheduled in Phase 5 due to P2 priority.

### Within Each User Story

- Test setup & fixtures before test method implementation.
- Standard happy path tests before boundary and error tests.
- Story validation checkpoint before proceeding to next priority story.

### Parallel Opportunities

- Phase 1 setup tasks T002 and T003 can run in parallel.
- Phase 2 foundational tasks T004, T005, and T006 can run in parallel across separate files.
- Once Phase 2 is complete, User Story 1 (`ExternalPlayerWhoScoredScrapperTest.java`) and User Story 3 (`ScraperHttpClientTest.java`) can be executed in parallel.
- Phase 6 polish tasks T024 and T025 can run in parallel.

---

## Parallel Example: Foundational Phase

```bash
# Launch foundational tasks together:
Task: "Implement mock HTML and JSON payload fixtures in src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/WhoScoredTestFixtures.java"
Task: "Initialize @SpringBootTest test class skeleton with @MockBean ScraperHttpClient in src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java"
Task: "Initialize Playwright test class skeleton with MockWebServer lifecycle setup and teardown in src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java"
```

## Parallel Example: User Stories 1 & 3

```bash
# Developer A works on User Story 1 (Scraper Data Extraction):
Task: "Implement test extraeMetricasDeJugadorConExitoYFiltraTorneosNoTop5 in src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java"

# Developer B works on User Story 3 (HTTP Client Resilience):
Task: "Implement test obtieneHtmlExitosamenteDesdeMockWebServer in src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup (remove stub, verify dependencies).
2. Complete Phase 2: Foundational (fixtures and class skeletons).
3. Complete Phase 3: User Story 1 (happy path extraction & metric aggregation).
4. **STOP and VALIDATE**: Run `.\mvnw.cmd test -Dtest=ExternalPlayerWhoScoredScrapperTest#extraeMetricas*` independently.
5. Verify 0 network calls initiated.

### Incremental Delivery

1. Complete Setup + Foundational → Foundation ready.
2. Add User Story 1 → Test independently → Validate MVP.
3. Add User Story 3 → Test independently with local `MockWebServer` → Validate HTTP client.
4. Add User Story 2 → Test edge cases and exceptions → Validate full scraper resilience.
5. Complete Phase 6: Polish → Full suite regression and execution under 15 seconds.

### Parallel Team Strategy

With multiple developers:
1. Team completes Setup + Foundational together.
2. Once Foundational is done:
   - Developer A: User Story 1 (`ExternalPlayerWhoScoredScrapperTest.java`)
   - Developer B: User Story 3 (`ScraperHttpClientTest.java`)
3. Developer A continues to User Story 2 (`ExternalPlayerWhoScoredScrapperTest.java`) once US1 is done.
4. Team runs Polish & regression suite together.

---

## Notes

- `[P]` tasks = different files, no dependencies
- `[Story]` label maps task to specific user story for traceability
- All test method names MUST be in Latin American Spanish using camelCase
- Every single test MUST run with 0 outbound network requests to live websites
- Verify pre-existing tests (`ExternalPlayerDAOWhoScoredImplTest`) are NEVER modified or broken
