# Data Model: Pricing Strategy Controller and End-to-End Verification

## Entities, DTOs, and Interfaces

### `EstrategiaCotizacionResponseDTO`

Response object representing a pricing strategy exposed through the HTTP API.

| Field | Type | Constraints | Description |
|---|---|---|---|
| `id` | `Long` | Required | Unique identifier of the persisted strategy |
| `valorBase` | `Double` | Required (≥ 0) | Base valuation applied in the quotation calculation |
| `factorEscala` | `Double` | Required (> 0) | Multiplier scaling the metric score |

Factory method:
- `static EstrategiaCotizacionResponseDTO desdeModelo(EstrategiaCotizacion modelo)`: converts domain model to API response DTO.

---

### `CrearEstrategiaCotizacionRequestDTO`

Request payload for registering a new pricing strategy.

| Field | Type | Validation Constraints | Description |
|---|---|---|---|
| `tipo` | `String` | Optional / Type identifier | Strategy type identifier or class descriptor |
| `valorBase` | `Double` | `@NotNull`, `@PositiveOrZero` | Base valuation for quotation formula |
| `factorEscala` | `Double` | `@NotNull`, `@Positive` | Scaling factor multiplier (must be strictly > 0) |

Mapping method:
- `EstrategiaCotizacion aModelo()`: instantiates the appropriate domain strategy instance based on configuration/type.

---

### `ActualizarFactorEscalaRequestDTO`

Request payload for updating the scale factor of an existing strategy.

| Field | Type | Validation Constraints | Description |
|---|---|---|---|
| `factorEscala` | `Double` | `@NotNull`, `@Positive` | New scale factor (must be strictly > 0) |

---

### `EstrategiaCotizacionController`

REST Controller exposing the administrative endpoints.

| Method | HTTP Verb | Path | Request Body | Response Status | Response Body |
|---|---|---|---|---|---|
| `recuperarTodos` | `GET` | `/api/admin/estrategias-cotizacion` | None | `200 OK` | `List<EstrategiaCotizacionResponseDTO>` |
| `guardar` | `POST` | `/api/admin/estrategias-cotizacion` | `CrearEstrategiaCotizacionRequestDTO` | `201 Created` | `EstrategiaCotizacionResponseDTO` |
| `seleccionarEstrategia` | `POST` | `/api/admin/estrategias-cotizacion/{id}/seleccionar` | None | `200 OK` | None / Empty body |
| `actualizar` | `PATCH` | `/api/admin/estrategias-cotizacion/{id}` | `ActualizarFactorEscalaRequestDTO` | `200 OK` | None / Empty body |
| `cotizarJugadores` | `POST` | `/api/admin/estrategias-cotizacion/cotizar` | None | `200 OK` | `"Cotización de jugadores finalizada exitosamente"` |

---

### Domain Entities Involved

#### `EstrategiaCotizacion` (`com.overcode.model.cotizacion`)
- `id`: `Long`
- `valorBase`: `Double` (invariant: ≥ 0)
- `factorEscala`: `Double` (invariant: > 0)
- Invariants enforced by setter methods throwing `EstrategiaInvalidaException`.

#### `ConfiguracionCotizaciones` (`com.overcode.model.cotizacion`)
- `id`: `Long`
- `estrategiaCotizacion`: `EstrategiaCotizacion` (currently active strategy)

#### `Player` (`com.overcode.model`)
- `id`: `Long`
- `name`: `String`
- `currentPrice`: `Long` / `Double` (updated during bulk quotation recalculation)
