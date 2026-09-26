# Implementation Plan: Metric Scraper Synchronization

**Branch**: `005-metric-scraper-sync` | **Date**: 2026-09-19 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `specs/005-metric-scraper-sync/spec.md`

## Summary

This plan outlines the technical implementation for the manual synchronization of player performance metrics from an external scraper into the core database. The implementation is strictly structured into the following 4 sequential tasks to comply with the project's architectural guidelines:

- **Tarea 1: Refactor arquitectónico**. Mover las clases de integración del Scraper (cliente HTTP, adaptadores) a la capa `persistence` y sus servicios de negocio a `service.impl`.
- **Tarea 2: Preparar el Modelo Player**. Expandir la entidad `Player` agregando el campo `clubName` y la clase `WeeklyMetrics` mapeada como `@Embedded`. Exponer el método de dominio `actualizarMetricas()`.
- **Tarea 3: Crear el Mánager Orquestador**. Crear el método `sincronizarMetricas()` en `PlayerServiceImpl` que recupere todos los jugadores, busque sus métricas en el scraper usando `clubName` y `nombre`, actualice el modelo y persista los cambios.
- **Tarea 4: Crear el Gatillo Manual**. Exponer el endpoint `POST /players/sync-metrics` en `PlayerController` para gatillar el proceso de orquestación.

## Technical Context

**Language/Version**: Java 17+ (Spring Boot)
**Primary Dependencies**: Spring Web, Spring Data JPA
**Storage**: PostgreSQL
**Testing**: JUnit 5, Testcontainers, RestClient
**Target Platform**: JVM
**Project Type**: Web Service (Backend)
**Performance Goals**: N/A
**Constraints**: Strict adherence to the layered architecture and rich domain model rules defined in the constitution.
**Scale/Scope**: Batch processing of existing `Player` records.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- **Layered Architecture**: PASS. The plan explicitly enforces moving external clients to `persistence` (Tarea 1) and keeping orchestrators in `service` (Tarea 3).
- **Rich Model**: PASS. The metrics update logic will reside inside the `Player` domain entity (`actualizarMetricas()`) rather than being a setter-heavy transaction in the service (Tarea 2).
- **Validation**: PASS. The controller will handle the trigger, and the service will orchestrate without mixing responsibilities.
- **Testing**: PASS. Integration tests (with Testcontainers) and Unit tests will be required for the new service and model behaviors.

## Project Structure

### Documentation (this feature)

```text
specs/005-metric-scraper-sync/
├── plan.md              # This file
├── research.md          # Phase 0 output
├── data-model.md        # Phase 1 output
├── quickstart.md        # Phase 1 output
└── contracts/           # Phase 1 output
```

### Source Code (repository root)

```text
src/main/java/com/dapp/valoracionmercadojugadoresdefutbol/
├── controller/
│   └── PlayerController.java (Tarea 4)
├── model/
│   ├── Player.java (Tarea 2)
│   └── WeeklyMetrics.java (Tarea 2)
├── persistence/
│   └── scraper/ (Tarea 1 - Refactor)
└── service/
    └── impl/
        └── PlayerServiceImpl.java (Tarea 3)

src/test/java/com/dapp/valoracionmercadojugadoresdefutbol/
├── model/
├── service/
└── controller/
```

**Structure Decision**: A standard Spring Boot layered structure, strictly bounded by the constitution. The scraper's physical location shifts to `persistence`, and domain entities are enriched.

## Complexity Tracking

No violations of the constitution.
