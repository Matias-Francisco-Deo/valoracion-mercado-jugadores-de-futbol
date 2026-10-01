# Phase 1 Data Model & Test Fixtures: WhoScored Scraper Test Suite

**Feature**: WhoScored Scraper and HTTP Client Test Suite  
**Branch**: `006-whoscored-scraper-tests`  
**Date**: 2026-10-01  

## 1. Domain Entities & DTOs Involved

### 1.1 `PlayerDraftDTO`
Input transfer object providing base identity information when querying a player:
- `name` (String): e.g., `"Kylian Mbappé"`
- `clubName` (String): e.g., `"Real Madrid CF"`
- `league` (String): e.g., `"La Liga"`

### 1.2 `Player` (Target Domain Model)
Output model constructed by `ExternalPlayerWhoScoredScrapper.getDatosDeJugador`:
- `externalId` (Long): WhoScored player ID (e.g., `11119L`)
- `name` (String): Preserved from `PlayerDraftDTO`
- `clubName` (String): Preserved from `PlayerDraftDTO`
- `league` (String): Preserved from `PlayerDraftDTO`
- `currentPrice` (Integer): Base initial value (set to `1`)
- `goals` (Integer): Sum of `Goals` across Top-5 leagues
- `assists` (Integer): Sum of `Assists` across Top-5 leagues
- `shotsOnTarget` (Integer): Sum of `ShotsOnTarget` across Top-5 leagues
- `passes` (Integer): Calculated pass accuracy percentage `Math.round((totalAccuratePasses / totalPasses) * 100)`
- `interceptions` (Integer): Sum of `Interceptions` across Top-5 leagues
- `tackles` (Integer): Sum of `TotalTackles` across Top-5 leagues
- `keyPasses` (Integer): Sum of `KeyPasses` across Top-5 leagues
- `successfulDribbles` (Integer): Sum of `Dribbles` across Top-5 leagues
- `rating` (Double): Weighted average rating `Math.round((sumRating / ratingAppsCount) * 100.0) / 100.0`

---

## 2. Test Fixtures & Payloads

### 2.1 Top-5 League Tournament IDs Mapping
Only tournaments with matching `TournamentId` are accumulated:
- `2`: Premier League (England)
- `3`: Bundesliga (Germany)
- `4`: LaLiga (Spain)
- `5`: Serie A (Italy)
- `22`: Ligue 1 (France)
- *All other IDs* (e.g., domestic cups, Champions League, other leagues) are ignored.

### 2.2 Standard WhoScored Mock HTML Fixture
Template HTML used to mock `httpClient.getHtml(...)` responses:
```html
<!DOCTYPE html>
<html>
<head><title>WhoScored Mock</title></head>
<body>
<script type="text/javascript">
require.config.params['args'] = {
  "tournaments": [
    {
      "TournamentId": 4,
      "Goals": 10,
      "Assists": 4,
      "ShotsOnTarget": 20,
      "Interceptions": 6,
      "TotalTackles": 8,
      "KeyPasses": 12,
      "Dribbles": 15,
      "GameStarted": 10,
      "SubOn": 2,
      "TotalPasses": 400.0,
      "AccuratePasses": 340.0,
      "Rating": 7.80
    },
    {
      "TournamentId": 2,
      "Goals": 2,
      "Assists": 1,
      "ShotsOnTarget": 4,
      "Interceptions": 2,
      "TotalTackles": 3,
      "KeyPasses": 4,
      "Dribbles": 5,
      "GameStarted": 3,
      "SubOn": 1,
      "TotalPasses": 100.0,
      "AccuratePasses": 85.0,
      "Rating": 7.20
    },
    {
      "TournamentId": 999,
      "Goals": 99,
      "Assists": 99,
      "ShotsOnTarget": 99,
      "Interceptions": 99,
      "TotalTackles": 99,
      "KeyPasses": 99,
      "Dribbles": 99,
      "GameStarted": 10,
      "SubOn": 0,
      "TotalPasses": 200.0,
      "AccuratePasses": 200.0,
      "Rating": 9.99
    }
  ]
};
</script>
</body>
</html>
```

### 2.3 Cloudflare Anti-Bot Challenge Mock Payload
Simulated challenge response used in `MockWebServer`:
```html
<!DOCTYPE html>
<html>
<head><title>Just a moment...</title></head>
<body>
<h1>Cloudflare</h1>
<p>Checking your browser before accessing the website.</p>
</body>
</html>
```

---

## 3. Exceptions & Error Conditions

### `ScraperExtractionException`
Thrown by:
1. `ExternalPlayerWhoScoredScrapper`:
   - Missing `"tournaments"` node or not an array.
   - Total games played across Top-5 leagues equals `0`.
   - Jackson `JsonProcessingException` when parsing invalid JSON syntax.
   - Unparseable HTML lacking `require.config.params['args']`.
2. `ScraperHttpClient`:
   - Cloudflare challenge detected and retry count reaches `maxRetries` (2).
   - Fatal browser navigation failure after reaching `maxRetries`.
