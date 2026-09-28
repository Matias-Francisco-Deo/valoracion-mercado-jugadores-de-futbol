# API Contract: Sync Metrics

## Endpoint
`POST /players/sync-metrics`

## Purpose
Manually triggers the batch synchronization process that updates all players' weekly metrics using the external scraper.

## Request
- **Headers**: Authorization (Requires Admin roles/permissions, if authentication is configured in the project)
- **Body**: Empty

## Response

### Success (200 OK)
```json
{
  "message": "Metrics synchronization completed successfully",
  "processedPlayers": 45,
  "updatedPlayers": 42
}
```

### Server Error (500 Internal Server Error)
```json
{
  "error": "Failed to synchronize metrics",
  "details": "External scraper service unavailable"
}
```
