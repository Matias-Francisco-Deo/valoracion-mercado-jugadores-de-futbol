# Quickstart: Pricing Strategy Controller and E2E Tests Validation

## Prerequisites

- Java 21 JDK installed
- PostgreSQL running or Docker available for Testcontainers
- Maven wrapper (`./mvnw` or `mvnw.cmd`) in backend project root
- Valid `scraper.api.key` configured in `application.properties` (or test profile, e.g., `test-api-key`)

---

## Running the Automated Test Suite

Execute the focused End-to-End test suite covering `EstrategiaCotizacionController`:

```bash
# In Linux/macOS
./mvnw test -Dtest=EstrategiaCotizacionE2eTest

# In Windows PowerShell
.\mvnw.cmd test -Dtest=EstrategiaCotizacionE2eTest
```

To run all web layer controller and security tests:

```bash
.\mvnw.cmd test -Dtest='EstrategiaCotizacionE2eTest,ApiKeyAuthFilterTest,ExternalPlayerControllerTest'
```

---

## Expected Outcomes

1. **Security Verification**:
   - Requests without `X-API-KEY` return `401 Unauthorized`.
   - Requests with an invalid key return `401 Unauthorized`.
   - Requests with the valid API key proceed successfully.

2. **Listing Strategies**:
   - `GET /api/admin/estrategias-cotizacion` returns `200 OK` with an empty array when no strategies exist.
   - Returns array of strategies with `id`, `valorBase`, and `factorEscala` when records are present.

3. **Strategy Creation**:
   - `POST /api/admin/estrategias-cotizacion` with valid parameters returns `201 Created` and persisted strategy details.
   - Submitting `factorEscala <= 0` or `valorBase < 0` returns `400 Bad Request`.

4. **Strategy Selection**:
   - `POST /api/admin/estrategias-cotizacion/{id}/seleccionar` with valid ID returns `200 OK`.
   - Non-existent strategy ID returns `404 Not Found`.

5. **Strategy Update**:
   - `PATCH /api/admin/estrategias-cotizacion/{id}` with positive scale factor returns `200 OK`.
   - Scale factor `0.0` or `-1.0` returns `400 Bad Request`.

6. **Bulk Quotation**:
   - `POST /api/admin/estrategias-cotizacion/cotizar` with active strategy returns `200 OK` and `"Cotización de jugadores finalizada exitosamente"`.
   - Validates that player market prices (`currentPrice`) in the database were recalculated via `PlayerService`.
   - When no active strategy is configured, returns `404 Not Found`.

---

## Troubleshooting

- **401 Unauthorized on all requests**: Confirm that the request header `X-API-KEY` matches `scraper.api.key` in the active Spring environment.
- **500 Internal Server Error on negative factor**: Verify that `GlobalExceptionHandler` includes `@ExceptionHandler(EstrategiaInvalidaException.class)` mapped to `HttpStatus.BAD_REQUEST`.
- **Strategy creation persistence failure in tests**: Ensure test execution runs within the `MockedStatic<TipoEstrategiaCotizacion>` block mapping the test strategy class to `EstrategiaCotizacionJPADTO`.
