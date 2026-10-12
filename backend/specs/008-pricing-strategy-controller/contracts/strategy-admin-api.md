# Contract: Pricing Strategy Admin API

## Base Path & Security

- **Base Path**: `/api/admin/estrategias-cotizacion`
- **Security**: Protected by `ApiKeyAuthFilter`. Requires HTTP header:
  `X-API-KEY: <scraper.api.key>`
- If header is absent or incorrect:
  - HTTP Status: `401 Unauthorized`
  - Body: `Unauthorized: Invalid or missing API Key`

---

## Endpoints

### 1. List All Pricing Strategies

- **Method**: `GET`
- **Path**: `/api/admin/estrategias-cotizacion`
- **Request Body**: None
- **Responses**:
  - `200 OK`:
    ```json
    [
      {
        "id": 1,
        "valorBase": 0.0,
        "factorEscala": 1.5
      }
    ]
    ```
  - `200 OK` (empty list):
    ```json
    []
    ```

---

### 2. Create Pricing Strategy

- **Method**: `POST`
- **Path**: `/api/admin/estrategias-cotizacion`
- **Request Headers**: `Content-Type: application/json`
- **Request Body**:
  ```json
  {
    "tipo": "com.overcode.testUtils.cotizacion.EstrategiaCotizacionSiempre2",
    "valorBase": 0.0,
    "factorEscala": 1.5
  }
  ```
- **Responses**:
  - `201 Created`:
    ```json
    {
      "id": 1,
      "valorBase": 0.0,
      "factorEscala": 1.5
    }
    ```
  - `400 Bad Request` (Validation error - e.g. factorEscala ≤ 0 or valorBase < 0):
    ```json
    {
      "mensaje": "El factor escala debe ser mayor a 0"
    }
    ```

---

### 3. Select Active Pricing Strategy

- **Method**: `POST`
- **Path**: `/api/admin/estrategias-cotizacion/{id}/seleccionar`
- **Path Variables**:
  - `id`: `Long` (identifier of the strategy to activate)
- **Request Body**: None
- **Responses**:
  - `200 OK`: empty body
  - `404 Not Found` (Strategy does not exist):
    ```json
    {
      "mensaje": "La estrategia a seleccionar no existe"
    }
    ```
  - `400 Bad Request` (Malformed or non-numeric ID):
    ```json
    {
      "mensaje": "ID inválido"
    }
    ```

---

### 4. Update Strategy Scale Factor

- **Method**: `PATCH`
- **Path**: `/api/admin/estrategias-cotizacion/{id}`
- **Path Variables**:
  - `id`: `Long` (identifier of the strategy to update)
- **Request Headers**: `Content-Type: application/json`
- **Request Body**:
  ```json
  {
    "factorEscala": 2.5
  }
  ```
- **Responses**:
  - `200 OK`: empty body
  - `400 Bad Request` (factorEscala null or ≤ 0):
    ```json
    {
      "mensaje": "El factor escala debe ser mayor a 0"
    }
    ```
  - `404 Not Found` (Strategy does not exist):
    ```json
    {
      "mensaje": "La entidad no fue encontrada"
    }
    ```

---

### 5. Bulk Recalculate Player Quotations

- **Method**: `POST`
- **Path**: `/api/admin/estrategias-cotizacion/cotizar`
- **Request Body**: None
- **Responses**:
  - `200 OK`:
    ```text
    Cotización de jugadores finalizada exitosamente
    ```
  - `404 Not Found` (No active strategy configured):
    ```json
    {
      "mensaje": "No se encontró la configuración de cotizaciones activa"
    }
    ```
