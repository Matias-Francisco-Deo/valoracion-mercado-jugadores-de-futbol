# Data Model: WhoScored Scraper

## Entities

### `WhoScoredIdentity`
Internal mapping to resolve IDs without relying on the search engine.
- `teamName` (String): Team name.
- `playerName` (String): Player name.
- `teamId` (Long): Internal WhoScored ID for the team.
- `playerId` (Long): Internal WhoScored ID for the player.

### `WeeklyMetrics`
DTO to represent the metrics extracted weekly.
- `playerId` (Long): WhoScored ID.
- `goals` (Integer): Total goals.
- `assists` (Integer): Total assists.
- `shotsOnTarget` (Integer): Shots on target.
- `passes` (Integer): Completed passes.
- `interceptions` (Integer): Interceptions.
- `rating` (Double): Player rating.
