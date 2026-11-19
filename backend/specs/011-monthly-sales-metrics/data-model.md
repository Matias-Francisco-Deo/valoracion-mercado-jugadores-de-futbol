# Data Model: Monthly Sales Metrics

## 1. Existing Storage Entities

This feature creates **no new persistent entity tables**. It directly queries existing transaction history stored in the PostgreSQL database.

### `TransactionJPADTO` (Table: `transactions`)

| Column | Type | Nullable | Description |
|---|---|---|---|
| `id` | `BIGINT` | NO | Primary key (Auto-increment) |
| `owner_id` | `BIGINT` | YES | Foreign key to `users` table |
| `player_id` | `BIGINT` | NO | Foreign key to `players` table |
| `date` | `TIMESTAMP` | NO | Execution timestamp of the transaction |
| `type` | `VARCHAR` | NO | Transaction type: `BUY` or `SELL` |
| `quantity` | `INTEGER` | NO | Number of tokens transferred |
| `unit_price` | `DOUBLE PRECISION` | NO | Price per token at transaction time |

#### Relevant Query Constraints
- The query targets records where `type = 'SELL'`.
- The query applies date range filtering: `date >= :startDate AND date <= :endDate`.

---

## 2. API Transfer Objects (DTOs)

### `MonthlySalesResponseDTO`
Package: `com.overcode.controller.dto.metrics`

```java
public record MonthlySalesResponseDTO(
    Integer year,
    Integer month,
    Long totalSales
) {}
```

#### Fields
- `year` (`Integer`): The target calendar year (e.g., `2026`).
- `month` (`Integer`): The target calendar month (1 to 12).
- `totalSales` (`Long`): Total count of executed sales transactions in the given month. Zero if no transactions occurred.

---

## 3. Query Parameter Validation Rules

- `year` (optional):
  - Must be a positive integer > 0 (typically 4 digits).
  - If omitted, defaults to the current calendar year (`LocalDate.now().getYear()`).
  - Validation error if `year <= 0`.
- `month` (optional):
  - Must be an integer between `1` and `12` inclusive.
  - If omitted, defaults to the current calendar month (`LocalDate.now().getMonthValue()`).
  - Validation error if `month < 1` or `month > 12`.
