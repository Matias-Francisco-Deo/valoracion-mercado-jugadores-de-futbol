# Task Breakdown: WhoScored Scraper

**Feature**: [spec.md](./spec.md)
**Plan**: [plan.md](./plan.md)
**Status**: In Progress

## Tasks

### Task A: Infrastructure (HTTP Client and Regex)
- [x] Implement `ScraperHttpClient.java` with browser headers (User-Agent, Accept-Language) and retry logic.
- [x] Implement `JsonExtractorUtil.java` with Regex logic to isolate the `require.config.params['args'] = {...};` block.
- [x] Unit tests for extraction and deserialization of isolated JSONs.

### Task B: Mapping Service (Flow 1)
- [x] Implement logic to navigate the league URL and obtain team IDs.
- [x] Implement logic to navigate the team URL and extract the player's WhoScored ID.
- [x] Integrate with the database to save the mapping.

### Task C: Extraction Service (Flow 2)
- [x] Implement `PlayerMetricsScraperService.java`.
- [x] Configure the Jackson ObjectMapper to map the JSON to the `WeeklyMetrics` DTO (goals, assists, shots on target, passes, interceptions, rating).

### Task D: Cache and Adapter
- [x] Implement `WhoScoredAdapter.java`.
- [x] Annotate the adapter methods with `@Cacheable` to ensure the system returns the last cached value if WhoScored goes down or blocks the IP (Requirement 5.4).
- [x] Configure Spring Cache in the application configuration.
