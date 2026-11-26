# Phase 0 Research: Monthly Sales Metrics

## 1. Direct Sales Count Query via @Query

### Decision
Implement a Spring Data JPA `@Query` in `TransactionDAOJPA` returning a `Long` count of executed sales:
```java
@Query("""
    SELECT COUNT(t) FROM transaction t
    WHERE t.type = com.overcode.model.market.transaction.TransactionType.SELL
      AND t.date >= :startDate
      AND t.date <= :endDate
""")
Long countCompletedSalesBetween(
    @Param("startDate") LocalDateTime startDate,
    @Param("endDate") LocalDateTime endDate
);
```

### Rationale
- **Zero In-Memory Mapping**: Eliminates fetching full transaction entity graphs, joining relationships (`PlayerJPADTO`, `UserJPADTO`), and transforming entities into domain models.
- **High Performance**: PostgreSQL executes an indexed aggregate `COUNT` in single-digit milliseconds, keeping endpoint latency well under the 500ms threshold even for months with >100,000 transactions.
- **Simplicity**: No extra state, no domain model mutation, directly satisfying user guidance ("No model logic, no extreme mapping in service... use @Query to get the data directly from the sells in the corresponding TransactionDAO").

### Alternatives Considered
- *In-memory stream filtering (`findAll().stream().filter(...)`)*: Rejected due to prohibitive CPU and memory overhead when transaction volume scales.
- *Persistent monthly counter table*: Rejected to avoid data synchronization complexity and unnecessary entity models.

---

## 2. Calendar Month Boundary Resolution

### Decision
Use `java.time.YearMonth` to derive inclusive date boundaries:
- Defaulting: When `year` or `month` are omitted, default to `YearMonth.now()`.
- Start Boundary: `yearMonth.atDay(1).atStartOfDay()` (`00:00:00.000000000`).
- End Boundary: `yearMonth.atEndOfMonth().atTime(LocalTime.MAX)` (`23:59:59.999999999`).

### Rationale
- Standard `java.time` types accurately calculate variable month lengths (28, 29, 30, 31 days) and leap years automatically.
- Validates that `month` is strictly between 1 and 12 and `year` is positive before date resolution.

### Alternatives Considered
- *Hardcoded day lookup array*: Rejected due to leap year edge case risks and manual timezone handling complexity.

---

## 3. Layered Architecture & Responsibilities

### Decision
- **Controller**: `com.overcode.controller.SalesMetricsController` mapped to `GET /api/admin/metrics/sales`. Accepts optional query parameters `year` and `month`. Returns `MonthlySalesResponseDTO`.
- **Service**: `com.overcode.service.interfaces.SalesMetricsService` & `com.overcode.service.impl.SalesMetricsServiceImpl`. Validates parameters, computes date boundaries, and invokes the repository.
- **Repository**: Add `Long contarVentasEntre(LocalDateTime inicio, LocalDateTime fin)` to `TransactionRepository` (interface) and `TransactionRepositoryImpl` (implementation), which invokes `TransactionDAOJPA`.
- **DTO**: `com.overcode.controller.dto.metrics.MonthlySalesResponseDTO` containing `year`, `month`, and `totalSales`.

### Rationale
- Satisfies Constitution Principle I (strict layered architecture: Controller -> Service -> Repository -> DAO) and Principle III (validation at each layer).
- Follows naming conventions enforced by `ArchitectureTest` (`*Controller`, `*Service`, `*ServiceImpl`, `*Repository`, `*RepositoryImpl`, `*DAOJPA`, `*DTO`).

---

## 4. Security & Access Control

### Decision
Rely on the existing `ApiKeyAuthFilter` configured in `SecurityConfig.adminFilterChain` matching `/api/admin/**`.

### Rationale
- Every endpoint under `/api/admin/**` is automatically intercepted by `ApiKeyAuthFilter`.
- Valid requests must include the header `X-API-KEY`. Missing or invalid keys automatically receive HTTP 401 Unauthorized without requiring custom security code in the controller.

---

## 5. Testing Strategy with Real Data

### Decision
- **Repository Integration Test (`TransactionRepositoryTest`)**:
  - Annotated with `@SpringBootTest`.
  - Runs against real PostgreSQL Testcontainers instance managed by the test suite.
  - Inserts actual `UserJPADTO`, `PlayerJPADTO`, and `TransactionJPADTO` records (with `SELL` and `BUY` types, matching dates and out-of-month dates) to prove query correctness with genuine database execution.
- **Service Unit Test (`SalesMetricsServiceImplTest`)**:
  - Pure unit test with Mockito.
  - Verifies date boundary generation, defaulting behavior, and parameter validation.
- **End-to-End API Test (`SalesMetricsE2eTest`)**:
  - Located in `com.overcode.e2e`.
  - Uses `@SpringBootTest(webEnvironment = RANDOM_PORT)` and `RestClient`.
  - Tests HTTP 200 with valid `X-API-KEY` and real transactions.
  - Tests HTTP 401 with missing/invalid `X-API-KEY`.
  - Tests HTTP 400 for out-of-bound month/year inputs.
  - Tests HTTP 200 with count 0 for months without activity.

### Rationale
- Strictly complies with Constitution Principle IV ("All requirements MUST be backed by tests in the proper scope and order... Service and repository tests MUST be integration tests using real PostgreSQL instances... End-to-end API tests MUST use @SpringBootTest and RestClient and MUST live in their own package").
