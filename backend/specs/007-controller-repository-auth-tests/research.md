# Research: Controller, Repository, Auth and JSON Extraction Test Coverage

## Decision: boundary-first test design with mocked async scraper execution

The project will validate the target behavior at the correct layer and avoid future rework caused by testing the wrong responsibilities.

### Rationale

- `ExternalPlayerController` is an HTTP entry point and should be tested as an E2E-style API contract using the real Spring MVC layer plus a mocked `ExternalPlayerService`.
- `PlayerGameDataRepositoryImpl` is a persistence adapter, so it should be tested with a real JPA repository/JPA DAO context and Testcontainers, not through the controller.
- `ApiKeyAuthFilter` should be validated as a request filter in a Spring Boot web environment by exercising the filter chain with valid and invalid API-key headers.
- `JsonExtractorUtil` is a stateless utility and should be validated with direct HTML fixtures covering success and malformed inputs.
- The expensive method `actualizarJugadoresAsync()` must be mocked because it invokes the scraper flow that can take hours; the endpoint contract is the value being asserted, not the external scraper runtime.

### Alternatives considered

1. Pure unit tests with direct bean invocation
   - Rejected because they would bypass the request and filter stack and miss real HTTP contract behavior.
2. Running the real scraper in tests
   - Rejected because it violates the project’s deterministic testing rule and would take unacceptable time.
3. Mixing controller, repository and parser assertions in one broad integration test
   - Rejected because it breaks responsibility boundaries and weakens debugging clarity.

## Decision: test names in Spanish latinoamericano

Test names will use Spanish-language identifiers aligned with the project’s current conventions, especially the existing tests already written in Spanish (for example, `actualizarDatosJugadoresSeEjecutaALas12DeLaNoche`).

### Rationale

- The repository already uses Spanish naming in scheduling and service tests.
- English names would be inconsistent with the current codebase and reduce traceability for local contributors.
- The names will still be precise and assertion-oriented, without hiding the behavior under test.

## Decision: use deterministic fixtures, not live external data

The targeted test suite will use synthetic HTML snippets, fabricated `PlayerGameData` values, and mocked service invocations instead of live scrape outputs.

### Rationale

- This keeps all tests fast, stable, and repeatable.
- It matches the constitution requirement to avoid live external systems and flaky behavior.
- It still covers the relevant edge cases: empty HTML, malformed JSON, missing API key, invalid key, and repository save/retrieve behavior.

## Open research items resolved

- `actualizarJugadores` duration is long and asynchronous → resolved by mocking the async call in controller tests.
- Boundary to validate for controller → resolved as the HTTP entry point with MockMvc/real web layer.
- Persistence boundary → resolved using JPA repository + DAO in Testcontainers.
- Security boundary → resolved using filter integration in Spring Boot web context.
- Parser boundary → resolved with direct HTML sample tests and exception assertions.
