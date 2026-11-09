# Implementation Plan: diary-bonus-missions

**Branch**: `010-diary-bonus-missions` | **Date**: 2026-11-06 | **Spec**: [specs/010-diary-bonus-missions/spec.md](spec.md)

**Input**: Feature specification from `specs/010-diary-bonus-missions/spec.md` and user architectural directives.

## Summary

This feature adapts and renames the daily login bonus system from `LoginBonus` to `DailyBonus`, introducing a flexible composite `Mission<T>` architecture featuring abstract classes `OrMission<T>` and `AndMission<T>`, strongly-typed generic completion (no raw `Object`, no boolean optional flags), and a persistent per-user daily bonus tracking profile (`UserDailyBonus`) that encapsulates the actual `User` domain entity.

Users can claim their daily bonus by either completing 3 distinct daily player profile visits OR purchasing a token from the marketplace (modeled naturally as an `OrMission`). To maintain system performance and prevent O(N) memory pressure during daily rollovers, daily missions are assigned to users on-demand via `userDailyBonus.inicializarConBonus(dailyBonus)` upon their first daily bonus interaction. Zero domain logic resides in Services or Controllers. All service methods adhere to a strict single-parameter contract accepting only domain models or domain records (never a kind of `RequestDTO`). Lombok `@Getter` and `@Setter` are used throughout the codebase for clean property encapsulation.

---

## Technical Context

**Language/Version**: Java 21 LTS  
**Primary Dependencies**: Spring Boot 4.1.1 (`spring-boot-starter-web`, `spring-boot-starter-data-jpa`, `spring-boot-starter-security`), Lombok, Jakarta Persistence  
**Storage**: PostgreSQL 16 (via Spring Data JPA and Hibernate ORM)  
**Testing**: JUnit 5, Mockito, Testcontainers 1.20.4 (`org.testcontainers:postgresql`), `RestClient`, `@SpringBootTest`  
**Target Platform**: JVM (Linux / Windows server)  
**Project Type**: REST Web Service (Spring Boot backend)  
**Performance Goals**: On-demand lazy assignment of daily missions (O(1) per active user request) to avoid O(N) bulk memory mapping across thousands of users at 00:00:00 UTC.  
**Constraints**:
- Strict Layered Architecture (Controller -> Service -> Repository -> DAOJPA -> JPADTO).
- Lombok `@Getter` and `@Setter` used across models, DTOs, and entities (no simple hand-written getters/setters).
- Model-only domain logic (zero business rules in Controller or Service; Service calls `userDailyBonus.inicializarConBonus(dailyBonus)`).
- Single-parameter service methods: every service method receives exactly ONE object, which must be a domain model entity or domain record (never a `RequestDTO`).
- Generic `Mission<T>` where subclasses define parameter type `T` in `complete(T context)` (no raw `Object`).
- Abstract classes `OrMission<T>` and `AndMission<T>` representing composite behaviors.
- No boolean flags for optionality in `Mission`.
- `UserDailyBonus` references the actual `User` domain object (not a foreign key `userId`).
- Custom domain, service, and persistence exceptions (no raw runtime exceptions).
- Latin American Spanish test method names.
- Static factory test utility methods in `TestDailyBonusUtil` and `TestMissionUtil`.

---

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Gate Criterion | Compliance Status | Details |
|---|---|---|---|
| **I. Layered Architecture** | Controller -> Service -> Repository -> DAOJPA / JPADTO. DTOs in dedicated `dto/` packages. Persistency isolated in `persistence/repository/`. | **PASS** | `DailyBonusController` maps request/response DTOs only. `UserDailyBonusService` and `MissionService` mediate between persistence and models. Repositories abstract JPA entities. Service methods take only domain models or records. |
| **II. Rich Model** | All business logic resides in Model objects (`DailyBonus`, `UserDailyBonus`, `Mission<T>`, `OrMission<T>`, `AndMission<T>`). New model classes approved by project owner. | **PASS** | User explicitly approved `DailyBonus`, `UserDailyBonus`, `Mission<T>`, `OrMission<T>`, `AndMission<T>`, `VisitPlayerMission`, `BuyMission`, `PeticionVisitaJugador`. Initialization and streak logic strictly live in `userDailyBonus.inicializarConBonus(dailyBonus)`. |
| **III. Validation at Every Layer** | DTO format validation, Service entity existence checks, Model domain invariant validation with custom exceptions. | **PASS** | Validates entity existence in Service (`EntidadNoEncontradaException`); domain invariants enforced in Model (`BonusYaReclamadoHoyException`, `RequisitosMisionesNoCumplidosException`, `MisionInvalidaException`). |
| **IV. Testing as a Delivery Gate** | Pure unit tests for Model; Testcontainers integration tests for Service/Repository; isolated `@SpringBootTest` E2E tests using `RestClient`. Spanish naming. | **PASS** | Unit tests cover `Mission`, `UserDailyBonus`, and `DailyBonus`. Integration tests verify PostgreSQL persistence via Testcontainers. E2E tests verify full HTTP flows. |
| **V. Definition of Done** | All unit and integration tests passing; local application compiles without error. | **PASS** | Defined in verification scenarios in `quickstart.md`. |

---

## Project Structure

### Documentation (this feature)

```text
specs/010-diary-bonus-missions/
├── spec.md              # Feature specification
├── plan.md              # Implementation plan (this file)
├── research.md          # Phase 0 architectural decisions and research
├── data-model.md        # Phase 1 domain entities, composite hierarchy, and JPA schemas
├── quickstart.md        # Phase 1 verification and testing guide
├── contracts/           # Phase 1 OpenAPI interface definitions
│   └── daily-bonus-api.yaml
└── tasks.md             # Phase 2 task breakdown (created by /speckit-tasks)
```

### Source Code Layout

```text
src/
├── main/
│   └── java/
│       └── com/
│           └── overcode/
│               ├── controller/
│               │   ├── DailyBonusController.java                        # [UPDATE] Add player visit endpoint
│               │   └── dto/
│               │       └── bonus/
│               │           ├── ClaimBonusRequestDTO.java
│               │           ├── ClaimBonusResponseDTO.java
│               │           ├── DailyBonusRequestDTO.java
│               │           ├── DailyBonusStatusResponseDTO.java         # [UPDATE] Include mission list DTOs
│               │           ├── RegistrarVisitaRequestDTO.java           # [NEW] DTO for visit request
│               │           └── MissionStatusResponseDTO.java            # [NEW] DTO for mission display (no optionality boolean)
│               ├── model/
│               │   ├── bonus/
│               │   │   ├── DiaryBonus.java                              # [RENAME/ADAPT] @Getter @Setter
│               │   │   ├── DiaryBonusRewards.java                       # [RENAME/ADAPT] @Getter
│               │   │   ├── UserDiaryBonus.java                          # [NEW] Holds User user, @Getter @Setter
│               │   │   ├── BonusClaim.java
│               │   │   ├── EstadoBonus.java                             # [UPDATE] Include missions status
│               │   │   ├── PeticionVisitaJugador.java                   # [NEW] Domain record (User user, Player player)
│               │   │   └── mission/
│               │   │       ├── Mission.java                             # [NEW] Abstract base Mission<T> @Getter @Setter
│               │   │       ├── OrMission.java                           # [NEW] Abstract class OrMission<T>
│               │   │       ├── AndMission.java                          # [NEW] Abstract class AndMission<T>
│               │   │       ├── DailyBonusOrMission.java                 # [NEW] Root OrMission<Void> for bonus
│               │   │       ├── Visit3PlayersMission.java                # [NEW] Concrete AndMission<Player>
│               │   │       ├── VisitPlayerMission.java                  # [NEW] Concrete Mission<Player>
│               │   │       ├── BuyMission.java                          # [NEW] Concrete Mission<Void>
│               │   │       └── TestMission.java                         # [NEW] Generic Mission<T> for unit tests
│               │   └── exception/
│               │       └── bonus/
│               │           ├── DiaryBonusInvalidoException.java         # [RENAME]
│               │           ├── MisionInvalidaException.java             # [NEW]
│               │           ├── BonusYaReclamadoHoyException.java
│               │           ├── RequisitosMisionesNoCumplidosException.java # [NEW]
│               │           └── FechaReclamoBonusDiarioInvalidaException.java # [RENAME]
│               ├── persistence/
│               │   ├── dto/jpa/bonus/
│               │   │   ├── DiaryBonusJPADTO.java                        # [RENAME/ADAPT]
│               │   │   ├── DiaryBonusRewardsJPADTO.java                 # [RENAME/ADAPT]
│               │   │   ├── UserDiaryBonusJPADTO.java                    # [NEW] Links to UserJPADTO
│               │   │   └── MissionJPADTO.java                           # [NEW] Single-table recursive tree
│               │   ├── repository/
│               │   │   ├── dao/jpa/
│               │   │   │   ├── DiaryBonusDAOJPA.java                    # [RENAME]
│               │   │   │   ├── UserDiaryBonusDAOJPA.java                # [NEW]
│               │   │   │   └── MissionDAOJPA.java                       # [NEW]
│               │   │   ├── interfaces/
│               │   │   │   ├── DiaryBonusRepository.java                # [RENAME]
│               │   │   │   ├── UserDiaryBonusRepository.java            # [NEW]
│               │   │   │   └── MissionRepository.java                   # [NEW]
│               │   │   └── impl/
│               │   │       ├── DiaryBonusRepositoryImpl.java            # [RENAME]
│               │   │       ├── UserDiaryBonusRepositoryImpl.java        # [NEW]
│               │   │       └── MissionRepositoryImpl.java               # [NEW]
│               │   └── exception/
│               │       ├── DiaryBonusPersistenciaException.java         # [RENAME]
│               │       ├── UserDiaryBonusPersistenciaException.java     # [NEW]
│               │       └── MisionPersistenciaException.java             # [NEW]
│               ├── service/
│               │   ├── interfaces/
│               │   │   ├── BonusService.java
│               │   │   ├── DiaryBonusService.java                       # [RENAME]
│               │   │   ├── UserDiaryBonusService.java                   # [NEW]
│               │   │   └── MissionService.java                          # [NEW]
│               │   └── impl/
│               │       ├── BonusServiceImpl.java                        # [UPDATE] 1-param methods, delegates to model
│               │       ├── DiaryBonusServiceImpl.java                   # [RENAME]
│               │       ├── UserDiaryBonusServiceImpl.java               # [NEW]
│               │       ├── MissionServiceImpl.java                      # [NEW]
│               │       └── TokenMarketServiceImpl.java                  # [UPDATE] Notify purchase for mission
│               └── config/
│                   └── DataInitializer.java                             # [UPDATE] Seed DiaryBonus with missions
└── test/
    └── java/
        └── com/
            └── overcode/
                ├── model/
                │   └── bonus/
                │       ├── DiaryBonusTest.java                          # [RENAME/UPDATE]
                │       ├── DiaryBonusRewardsTest.java                   # [RENAME]
                │       ├── UserDiaryBonusTest.java                      # [NEW]
                │       └── mission/
                │           ├── MissionTest.java                         # [NEW] Unit tests on TestMission, AndMission, OrMission
                │           ├── VisitPlayerMissionTest.java              # [NEW]
                │           └── BuyMissionTest.java                      # [NEW]
                ├── persistence/
                │   └── repository/
                │       ├── DiaryBonusRepositoryTest.java                # [RENAME]
                │       ├── UserDiaryBonusRepositoryTest.java            # [NEW]
                │       └── MissionRepositoryTest.java                   # [NEW]
                ├── service/
                │   └── bonus/
                │       ├── BonusServiceImplTest.java                    # [UPDATE]
                │       └── UserDiaryBonusServiceImplTest.java           # [NEW]
                ├── e2e/
                │   └── DailyBonusE2eTest.java                           # [UPDATE] Include visits and buy paths
                └── testUtils/
                    ├── TestDiaryBonusUtil.java                          # [RENAME/ADAPT]
                    └── TestMissionUtil.java                             # [NEW] Static factories for tests
```

**Structure Decision**: The backend code adheres to the standard single Java Spring Boot layout already established under `src/main/java/com/overcode` and `src/test/java/com/overcode`. The naming convention `*DAOJPA`, `*JPADTO`, `*Repository`, `*RepositoryImpl`, `*Service`, and `*ServiceImpl` is strictly preserved.

---

## Complexity Tracking

No constitution violations detected. All requirements fit cleanly within the existing layered design patterns.
