# Contract: admin player refresh endpoint

## Endpoint

`POST /api/admin/players/actualizar-jugadores`

## Authentication

The endpoint is protected by the `ApiKeyAuthFilter` and requires the `X-API-KEY` request header.

## Success response

- HTTP status: `202 Accepted`
- Body: `Scraper manual iniciado en background. Este proceso puede tardar varias horas.`

## Unauthorized response

- HTTP status: `401 Unauthorized`
- Body: `Unauthorized: Invalid or missing API Key`

## Side effect contract

- The endpoint triggers `ExternalPlayerService.actualizarJugadoresAsync()` exactly once.
- The actual scraper execution is mocked in tests because it can take several hours.

## Notes

This is the external contract the controller must satisfy, independent of the scraper implementation. The tests do not validate the long-running scraper run itself; they validate the API behavior and security gate around it.
