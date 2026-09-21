# Quickstart: Metric Scraper Synchronization

## Prerequisites
- Local PostgreSQL database running (e.g., via Docker).
- Application running locally (`./gradlew bootRun` or `./mvnw spring-boot:run` depending on the build tool).
- Some `Player` records populated in the database with valid `clubName` and `name` values that exist in the target external scraper source.

## Validation Scenario: Manual Synchronization

1. **Check current metrics (Optional)**
   Verify the current state of a player's metrics in the database or via an existing GET endpoint to ensure they are empty or outdated.

2. **Trigger the Synchronization**
   Execute a POST request to the new endpoint:
   ```bash
   curl -X POST http://localhost:8080/players/sync-metrics
   ```

3. **Expected Outcome**
   You should receive a `200 OK` response similar to:
   ```json
   {
     "message": "Metrics synchronization completed successfully",
     "processedPlayers": 5,
     "updatedPlayers": 5
   }
   ```

4. **Verify the Updates**
   Query the database or use a GET endpoint to verify that the `WeeklyMetrics` fields (goals, assists, etc.) for the players have been correctly populated with the latest scraped data.
