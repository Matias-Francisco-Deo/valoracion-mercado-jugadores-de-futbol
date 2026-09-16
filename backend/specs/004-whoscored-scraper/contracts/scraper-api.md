# API Contracts: WhoScored Scraper

Defines the internal interface of the adapter that exposes the Scraper to the rest of the application.

## Interface: `WhoScoredAdapter`

### Method: `resolvePlayerId` (Flow 1)
- **Input**: `teamName` (String), `playerName` (String)
- **Output**: `Long` (WhoScored Player ID)
- **Throws**: `ScraperExtractionException` if the league, team, or player is not found.

### Method: `extractWeeklyMetrics` (Flow 2)
- **Input**: `playerId` (Long)
- **Output**: `WeeklyMetrics` (DTO)
- **Throws**: `ScraperExtractionException` if the JSON cannot be parsed or the player does not exist.
