# Quickstart: WhoScored Scraper & HTTP Client Test Suite

**Feature**: WhoScored Scraper and HTTP Client Test Suite  
**Branch**: `006-whoscored-scraper-tests`  
**Date**: 2026-10-01  

## Prerequisites

1. **Java Development Kit**: JDK 21+ installed and configured.
2. **Maven Wrapper**: `./mvnw.cmd` (Windows PowerShell) or `./mvnw` (Linux/macOS).
3. **Playwright Chromium**: Local browser binaries pre-installed (located at `%LOCALAPPDATA%\ms-playwright`).
4. **Network State**: Fully offline-capable (all tests execute against in-memory mocks or local loopback `127.0.0.1`).

---

## Running the Verification Tests

### 1. Run the Scraper Test Suite
Executes all unit/component scenarios for [`ExternalPlayerWhoScoredScrapper`](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/src/main/java/com/overcode/persistence/repository/dao/external/scrapper/whoscored/ExternalPlayerWhoScoredScrapper.java):

```powershell
.\mvnw.cmd test -Dtest=ExternalPlayerWhoScoredScrapperTest
```

**Expected Outcome**:
- Spring Boot test context starts.
- `ScraperHttpClient` is mocked via `@MockBean`.
- 8 tests execute and pass in under 5 seconds.
- 0 outbound network requests initiated.

### 2. Run the HTTP Client Test Suite
Executes all Playwright navigation, Cloudflare retry, and route interception scenarios for [`ScraperHttpClient`](file:///F:/Users/ROCKITO/Documents/Hamwork/UNQ/desurollo/overcode/valoracion-mercado-jugadores-de-futbol/backend/src/main/java/com/overcode/persistence/repository/dao/external/scrapper/http/ScraperHttpClient.java):

```powershell
.\mvnw.cmd test -Dtest=ScraperHttpClientTest
```

**Expected Outcome**:
- Headless Chromium connects exclusively to local `MockWebServer` (`http://localhost:<dynamic-port>`).
- 6 tests execute and pass.
- 0 outbound network requests to `whoscored.com` or third parties.

### 3. Run the Entire External Scraper Test Package

```powershell
.\mvnw.cmd test -Dtest=*ScrapperTest,*ScraperHttpClientTest
```

**Expected Outcome**:
- All 14 tests run cleanly with `BUILD SUCCESS`.
- Total execution time under 15 seconds.
- Preserves all pre-existing tests without modification or regressions.
