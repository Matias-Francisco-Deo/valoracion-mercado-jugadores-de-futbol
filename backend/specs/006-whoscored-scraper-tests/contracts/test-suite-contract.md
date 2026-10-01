# Test Suite Specification & Method Contracts

**Feature**: WhoScored Scraper and HTTP Client Test Suite  
**Branch**: `006-whoscored-scraper-tests`  
**Date**: 2026-10-01  

## 1. Class: `ExternalPlayerWhoScoredScrapperTest`

- **Location**: `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapperTest.java`
- **Annotations**: `@SpringBootTest`
- **Dependencies**:
  - `@MockBean private ScraperHttpClient httpClient;`
  - `@Autowired private ExternalPlayerWhoScoredScrapper scrapper;`

### Test Methods Contract (Latin American Spanish)

| Method Name | Input / Condition | Expected Outcome |
|-------------|-------------------|------------------|
| `extraeMetricasDeJugadorConExitoYFiltraTorneosNoTop5` | Valid WhoScored HTML with Top-5 and non-Top-5 tournaments | Returns `Optional<Player>` with metrics summed ONLY from Top-5 tournaments; non-Top-5 statistics excluded |
| `calculaRatingPonderadoYPorcentajeDePasesExitososCorrectamente` | 2 Top-5 tournaments with different appearance counts and pass volumes | Correctly calculates weighted rating `(R1*A1 + R2*A2)/(A1+A2)` and pass percentage `(Accurate/Total)*100` |
| `manejaSinErroresJugadorConCeroPasesIntentadosYCeroPartidosConRating` | Top-5 tournament with 0 attempted passes and 0 rating | Pass percentage is `0`, rating is `0.0`, no arithmetic or division-by-zero exceptions |
| `lanzaExcepcionCuandoJugadorNoRegistraPartidosEnLasCincoLigasPrincipales` | Tournaments array has 0 appearances in Top-5 leagues | Throws `ScraperExtractionException` with message "El jugador no registra actividad en ninguna de las 5 ligas principales." |
| `lanzaExcepcionCuandoElJsonNoContieneElNodoTournamentsOEsInvalido` | Raw JSON missing `"tournaments"` or `"tournaments"` is not an array | Throws `ScraperExtractionException` with message "No se encontró el nodo 'tournaments' en el JSON." |
| `lanzaExcepcionCuandoElHtmlNoContieneElBloqueJsonEsperado` | Malformed or empty HTML lacking `require.config.params['args']` | Throws `ScraperExtractionException` from JSON extraction utility |
| `lanzaExcepcionCuandoElJsonTieneSintaxisCorrupta` | HTML contains broken JSON syntax in script block | Throws `ScraperExtractionException` with message containing "Error al parsear el JSON de estadísticas con Jackson." |
| `propagaExcepcionCuandoElHttpClientFalla` | `httpClient.getHtml(...)` throws `ScraperExtractionException` | Exception propagates to caller without unhandled transformation |

---

## 2. Class: `ScraperHttpClientTest`

- **Location**: `src/test/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClientTest.java`
- **Isolation Mechanism**: Local `MockWebServer` (`okhttp3.mockwebserver.MockWebServer`) on `localhost`
- **Network Boundaries**: 0 external network requests; all URLs target `mockWebServer.url("/...").toString()`

### Test Methods Contract (Latin American Spanish)

| Method Name | Input / Condition | Expected Outcome |
|-------------|-------------------|------------------|
| `obtieneHtmlExitosamenteDesdeMockWebServer` | MockWebServer enqueues 200 OK with `<html><body><h1>Test</h1></body></html>` | `client.getHtml(url)` navigates to local URL and returns full HTML containing `<h1>Test</h1>` |
| `reintentaYObtieneHtmlCuandoElPrimerIntentoEncuentraDesafioCloudflare` | Response 1: Cloudflare challenge HTML; Response 2: Valid HTML | Client detects challenge on attempt 1, retries, and returns valid HTML on attempt 2 |
| `lanzaExcepcionCuandoSeSuperanLosReintentosPorDesafioCloudflare` | Response 1: Cloudflare challenge; Response 2: Cloudflare challenge | Both attempts fail; client throws `ScraperExtractionException` with message containing "Cloudflare challenge no superado" |
| `lanzaExcepcionCuandoLaNavegacionFallaCompletamenteTrasReintentos` | MockWebServer shuts down or connection is refused on both attempts | Client exhausts retries and throws `ScraperExtractionException` with message containing "Error fatal ejecutando Playwright" |
| `abortaPeticionesDeImagenesEstilosYTrackersDuranteLaNavegacion` | Mock HTML references `<img src="/img.png">`, `<link href="/style.css">` | Playwright route intercepts and aborts those assets; `MockWebServer` does not receive requests for filtered assets |
| `inicializaYCierraRecursosDeNavegadorCorrectamente` | Client lifecycle methods `init()` and `cleanup()` invoked | Browser instances launch and terminate cleanly without resource leaks |
