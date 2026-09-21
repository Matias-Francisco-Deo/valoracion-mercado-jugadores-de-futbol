# Data Model: Metric Scraper Synchronization

## Entities

### `Player` (Existing, to be expanded)
The core domain entity representing a football player.

**New Fields:**
- `clubName` (String): The current club of the player, used to match against the scraper's search.
- `metrics` (WeeklyMetrics): The embedded metrics object holding the scraped data.

**Domain Methods:**
- `actualizarMetricas(WeeklyMetrics newMetrics)`: Encapsulates the logic to update the player's metrics. Validates that the provided metrics are not null and updates the internal `@Embedded` field.

### `WeeklyMetrics` (New, `@Embeddable`)
A value object representing the performance metrics of a player for a given week.

**Fields:**
- *Note: Exact fields depend on the scraper's output format, but generally include:*
- `goals` (Integer)
- `assists` (Integer)
- `minutesPlayed` (Integer)
- `averageRating` (Double)

## Relationships
- **Player -> WeeklyMetrics**: 1-to-1 embedded value type. The `WeeklyMetrics` instance is fully owned by the `Player` and is persisted in the same database table using JPA's `@Embedded` and `@Embeddable` annotations.
