# Specification: Metric Scraper Synchronization

## 1. Overview
The system requires a mechanism to synchronize player performance metrics from an external data source (Scraper) into the core application database. This feature introduces a manual synchronization process that updates each player's weekly metrics based on their current club and name, ensuring the platform's valuation data remains accurate and up-to-date. The implementation must adhere to strict architectural boundaries, treating the scraper as an external data source.

## 2. User Scenarios
- **Manual Synchronization**: A system administrator or automated job triggers the synchronization process. The system processes all players, fetches their latest metrics from the external scraper, updates their profiles, and persists the new data.

## 3. Functional Requirements
- **FR1: External Source Integration**: The scraper must be integrated as an external data source at the persistence boundary. Its orchestration must be handled exclusively by the service layer.
- **FR2: Player Data Expansion**: The Player entity must capture the player's current club name and a dedicated structure for weekly metrics.
- **FR3: Encapsulated Updates**: The Player model must encapsulate the behavior to update its own metrics, maintaining rich domain model principles.
- **FR4: Batch Synchronization Flow**: The system must provide a synchronization flow that retrieves all registered players, queries the external source for each player using their name and club, applies the retrieved metrics to the player model, and persists the changes.
- **FR5: Manual Trigger**: The system must expose an administrative trigger to manually initiate the batch synchronization process.

## 4. Technical Constraints (Architecture Pillars)
*Note: These constraints dictate the structural implementation of the functional requirements as requested.*
- **Architecture**: Move scraper components (HTTP client, adapters) to the `persistence` layer. Move scraper services to `service.impl`.
- **Model**: Expand `Player` entity with `clubName` and `WeeklyMetrics` (mapped as `@Embedded`). Expose `actualizarMetricas()` method.
- **Orchestrator**: Create `sincronizarMetricas()` in `PlayerServiceImpl` to execute the batch synchronization flow.
- **Endpoint**: Expose `POST /players/sync-metrics` in `PlayerController`.

## 5. Success Criteria
- Administrators can successfully trigger the synchronization process and receive a confirmation response.
- After a successful synchronization, all players with available data in the external source reflect their newly fetched weekly metrics.
- The synchronization process correctly matches players using both their name and club name.
- The system architecture clearly separates the external scraping logic from the core domain model, strictly following the defined architectural pillars.

## 6. Key Entities
- **Player**: The core entity, expanded to include club affiliation and weekly metrics.
- **WeeklyMetrics**: A structured set of performance data points for a given week.

## 7. Assumptions & Dependencies
- **Assumption**: The external scraper is available and can process requests based on player name and club name.
- **Assumption**: Players without corresponding data in the external source will remain unchanged or retain their last known metrics.
- **Dependency**: The external metrics scraping infrastructure must be properly configured and accessible to the persistence layer.
