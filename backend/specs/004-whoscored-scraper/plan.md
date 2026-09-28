# Implementation Plan: WhoScored Scraper

**Branch**: `004-whoscored-scraper` | **Date**: 2026-09-14 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/004-whoscored-scraper/spec.md`

## Summary

Spring Boot implementation of a resilient scraper to extract performance metrics from WhoScored. To evade blocks and optimize execution times, we bypass the DOM and headless browsers entirely, utilizing native HTTP clients and Regex to isolate and parse JSONs embedded in `<script>` tags.

## Technical Context

**Language/Version**: Java 17+ (Spring Boot 3.x)

**Primary Dependencies**: `spring-boot-starter-webflux` (for WebClient) or native HttpClient, `jackson-databind` (JSON parsing).

**Storage**: Application DB (to save the League -> Team -> Player mapping).

**Testing**: JUnit 5, Mockito.

**Target Platform**: Backend server.

**Project Type**: Backend Service / Scheduled Task.

**Performance Goals**: < 2 seconds per player (Flow 2).

**Constraints**: Fault tolerance via caching (Vision Document section 5.4).

## Project Structure

### Documentation (this feature)

```text
specs/004-whoscored-scraper/
├── plan.md              # This file
├── spec.md              # Feature specification
└── tasks.md             # Execution steps
```

### Source Code

```text
src/main/java/com/overcode/
├── infrastructure/
│   ├── scraper/
│   │   ├── http/
│   │   │   └── ScraperHttpClient.java     # Task A
│   │   └── util/
│   │       └── JsonExtractorUtil.java     # Task A
├── domain/
│   └── service/
│       ├── PlayerMappingService.java      # Task B (Flow 1)
│       └── PlayerMetricsScraperService.java # Task C (Flow 2)
└── application/
    └── adapter/
        └── WhoScoredAdapter.java          # Task D (Cache & Adapter)
```

## Development Layers (Defined by Architecture)

- **Task A (Infrastructure)**: Create a resilient HTTP client (with retries and headers simulating a browser) and a Regex utility class to extract raw JSON blocks from HTML.
- **Task B (Mapping Service)**: Implement Flow 1 logic (League -> Team -> Player) to discover and store WhoScored IDs.
- **Task C (Extraction Service)**: Implement Flow 2 (Direct reading of statistics by ID isolating the `<script>`).
- **Task D (Cache and Adapter)**: Wrap these services in the "Adapters" layer and implement the cache required in point 5.4 of the Vision Document to tolerate failures and temporary blocks from WhoScored.
