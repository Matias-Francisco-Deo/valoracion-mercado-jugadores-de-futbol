# Research & Technical Decisions: Player and User Controller Test Suite

**Feature**: specs/003-player-user-controller-tests
**Date**: 2026-09-11

## 1. Test Architecture and Client Framework

- **Decision**: Use @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) with Spring's fluent RestClient, identical to the setup in com.overcode.controller.AuthControllerTest.
- **Rationale**: The user explicitly instructed to use the exact same format as AuthControllerTest. Running with RANDOM_PORT starts the real servlet container and runs the complete SecurityFilterChain, JwtAuthenticationFilter, JSON serialization, and global exception handlers. RestClient provides a concise, modern HTTP client with clear status inspection and typed response extraction.
- **Alternatives Considered**:
  - @WebMvcTest with MockMvc: Mock-based web slice testing does not exercise the complete runtime HTTP pipeline, filter registration, or real JPA repositories.
  - TestRestTemplate: Older Spring test client, less ergonomic than RestClient which is already established in the codebase.

## 2. Test Suite Structure and File Locations

- **Decision**: Create two separate test classes in src/test/java/com/overcode/controller/:
  - PlayerControllerTest.java (testing /players and /players/{id})
  - UserControllerTest.java (testing /users/{id})
- **Rationale**: Isolates domain testing concerns, keeps test classes small and manageable, and ensures each test file maps 1-to-1 with its target controller.
- **Alternatives Considered**:
  - Single combined test class: Bundles unrelated controllers and makes the test file unnecessarily large and difficult to maintain.

## 3. Test Naming and Language

- **Decision**: All test method names are written in Latin-American Spanish following the pattern <accion/endpoint><Condicion><ResultadoEsperado>.
- **Rationale**: Mandated by the user prompt ("Each test name must be in spanish (latin-american)") and reinforced by Constitution Principle IV ("Test names MUST clearly represent the behavior under test").
- **Examples**:
  - listarJugadoresConBaseVaciaDevuelveListaVacia()
  - listarJugadoresConJugadoresExistentesDevuelveListaCompleta()
  - obtenerJugadorPorIdExistenteDevuelveOkConDatosCorrectos()
  - obtenerJugadorPorIdInexistenteLanzaNotFound()
  - obtenerJugadorConIdInvalidoLanzaBadRequest()
  - obtenerUsuarioPorIdExistenteDevuelveOkConDatosCorrectos()
  - obtenerUsuarioPorIdInexistenteLanzaNotFound()
  - obtenerUsuarioConIdInvalidoLanzaBadRequest()
  - ccederEndpointProtegidoSinTokenLanzaForbidden()
  - ccederEndpointProtegidoConTokenInvalidoLanzaForbidden()

## 4. Test Fixtures and Reusable Constants

- **Decision**: Define reusable test inputs as private static final constants in each test class. Reusable constants include:
  - Default user credentials (DEFAULT_USERNAME, DEFAULT_EMAIL, DEFAULT_PASSWORD)
  - Player attributes (PLAYER_NAME)
  - Invalid tokens (INVALID_BEARER_TOKEN = "Bearer invalid.token.value")
  - Non-existent IDs (NON_EXISTENT_ID = 999999L)
  - Malformed IDs (INVALID_ID = "invalid-id")
- **Rationale**: Required by user instructions ("if you use the same object to test, you should make it a constant so its easy to change"). Prevents magic values and ensures centralized test configuration.

## 5. Granular Tests and Specific Exception Assertions

- **Decision**: Each test method asserts a single behavior. Exception assertions must target specific subclasses of HttpClientErrorException (such as HttpClientErrorException.NotFound.class, HttpClientErrorException.BadRequest.class, HttpClientErrorException.Forbidden.class) instead of generic exceptions.
- **Rationale**: User explicitly required: "Asserts specific throws, dont assert general ones, and separate the tests so they dont test too many things at the same time." Specific exception assertions ensure HTTP contract correctness.

## 6. Immutable Production Code & Failure Tagging

- **Decision**: Zero modifications to production source code in src/main/. If any test fails due to current controller or framework behavior, add the designated comment // TODO SDD TEST FAILURE above or within that test method.
- **Rationale**: The user strictly mandated: "You CANNOT change any code, and its possible some tests fail. In that case, just add a TODO comment that says 'TODO SDD TEST FAILURE' so we can fix it later on."
