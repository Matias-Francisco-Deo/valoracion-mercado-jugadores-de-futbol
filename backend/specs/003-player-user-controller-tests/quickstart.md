# Quickstart Validation Guide: Player and User Controller Test Suite

## Overview

This guide describes how to run and validate the automated controller tests for `PlayerController` and `UserController`.

## Prerequisites

- Java 21 JDK installed
- Docker running (required for Testcontainers PostgreSQL support in the test environment)
- Maven wrapper (`./mvnw` or `./mvnw.cmd`)

## Execution Commands

### 1. Run all controller tests

```bash
./mvnw.cmd test -Dtest=PlayerControllerTest,UserControllerTest
```

### 2. Run PlayerController tests only

```bash
./mvnw.cmd test -Dtest=PlayerControllerTest
```

### 3. Run UserController tests only

```bash
./mvnw.cmd test -Dtest=UserControllerTest
```

### 4. Run entire backend test suite to ensure zero regressions

```bash
./mvnw.cmd test
```

## Expected Test Scenarios

### PlayerControllerTest
| Test Method | HTTP Method / URI | Condition | Expected Status / Result |
|---|---|---|---|
| `listarJugadoresConBaseVaciaDevuelveListaVacia` | `GET /players` | Base de datos sin jugadores, token válido | `200 OK`, lista vacía `[]` |
| `listarJugadoresConJugadoresExistentesDevuelveListaCompleta` | `GET /players` | Base de datos con jugadores registrados, token válido | `200 OK`, lista de `PlayerResponseDTO` |
| `obtenerJugadorPorIdExistenteDevuelveOkConDatosCorrectos` | `GET /players/{id}` | Jugador existente, token válido | `200 OK`, `PlayerResponseDTO` con id y nombre correctos |
| `obtenerJugadorPorIdInexistenteLanzaNotFound` | `GET /players/{id}` | ID no existente (999999), token válido | `404 Not Found` (`HttpClientErrorException.NotFound`) |
| `obtenerJugadorConIdInvalidoLanzaBadRequest` | `GET /players/{id}` | ID no numérico ("invalid-id"), token válido | `400 Bad Request` (`HttpClientErrorException.BadRequest`) |
| `listarJugadoresSinTokenLanzaForbidden` | `GET /players` | Sin cabecera Authorization | `403 Forbidden` (`HttpClientErrorException.Forbidden`) |
| `listarJugadoresConTokenInvalidoLanzaForbidden` | `GET /players` | Cabecera con token malformado | `403 Forbidden` (`HttpClientErrorException.Forbidden`) |
| `obtenerJugadorPorIdSinTokenLanzaForbidden` | `GET /players/{id}` | Sin cabecera Authorization | `403 Forbidden` (`HttpClientErrorException.Forbidden`) |
| `obtenerJugadorPorIdConTokenInvalidoLanzaForbidden` | `GET /players/{id}` | Cabecera con token malformado | `403 Forbidden` (`HttpClientErrorException.Forbidden`) |

### UserControllerTest
| Test Method | HTTP Method / URI | Condition | Expected Status / Result |
|---|---|---|---|
| `obtenerUsuarioPorIdExistenteDevuelveOkConDatosCorrectos` | `GET /users/{id}` | Usuario existente, token válido | `200 OK`, `UserResponseDTO` con id, username, email |
| `obtenerUsuarioPorIdNoExponeContrasenaNiCredenciales` | `GET /users/{id}` | Usuario existente, token válido | `200 OK`, respuesta no contiene campo `password` |
| `obtenerUsuarioPorIdInexistenteLanzaNotFound` | `GET /users/{id}` | ID no existente (999999), token válido | `404 Not Found` (`HttpClientErrorException.NotFound`) |
| `obtenerUsuarioConIdInvalidoLanzaBadRequest` | `GET /users/{id}` | ID no numérico ("invalid-id"), token válido | `400 Bad Request` (`HttpClientErrorException.BadRequest`) |
| `obtenerUsuarioPorIdSinTokenLanzaForbidden` | `GET /users/{id}` | Sin cabecera Authorization | `403 Forbidden` (`HttpClientErrorException.Forbidden`) |
| `obtenerUsuarioPorIdConTokenInvalidoLanzaForbidden` | `GET /users/{id}` | Cabecera con token malformado | `403 Forbidden` (`HttpClientErrorException.Forbidden`) |

## Handling Test Failures

If an asserted status code fails due to existing backend behavior without code modification permission, annotate or tag the test with:
```java
// TODO SDD TEST FAILURE
```
