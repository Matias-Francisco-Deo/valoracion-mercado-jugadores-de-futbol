# Research & Architectural Decisions: diary-bonus-dailyBonusMissions

## Executive Summary

This document captures the research, architectural evaluations, and technical decisions for transitioning the daily reward mechanism from `LoginBonus` to `DailyBonus`, introducing the composite `Mission<T>` hierarchy with abstract `OrMission<T>` and `AndMission<T>` base classes, strongly-typed generic parameter completion without raw `Object` or optionality boolean flags, and implementing `UserDailyBonus` with direct reference to the `User` domain entity, zero domain logic in the Service layer, single-object service contracts (model or domain record only), and full adoption of Lombok `@Getter` and `@Setter`.

---

## Technical Context Baseline

- **Language / Runtime**: Java 21 LTS
- **Framework**: Spring Boot 4.1.1 (Spring 6.x)
- **Code Generation / Boilerplate**: Lombok (`@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`)
- **Persistence**: Spring Data JPA, Hibernate ORM, PostgreSQL (flyway/JPA schema generation)
- **Testing**: JUnit 5, Mockito, Testcontainers (PostgreSQL 16), RestClient for E2E tests
- **Architecture**: Domain-Driven Design with strict Layered Architecture (Model -> Service -> Repository -> DAOJPA/JPADTO, Controller -> Service)

---

## Research Tasks & Decision Records

### 1. Renaming and Refactoring from `LoginBonus` to `DailyBonus`

- **Context**: The existing codebase has `LoginBonus`, `LoginBonusRewards`, `LoginBonusService`, `LoginBonusRepository`, `LoginBonusJPADTO`, `LoginBonusDAOJPA`, and references in `ConfiguracionMercado` and `DataInitializer`. The user specified:
  > *"Do not remove the LoginBonus object (and whatever dependencies it has) instead, adapt it to the new logic. Change every name for DiaryBonus (including Service, Persistency, all places where it shows up)."*
- **Decision**: Fully rename and adapt all `LoginBonus*` classes, interfaces, JPA entities, repositories, exceptions, and tests to `DiaryBonus*`:
  - Model: `DailyBonus`, `DailyBonusRewards`, `DailyBonusInvalidoException`
  - Persistence: `DailyBonusJPADTO`, `DailyBonusRewardsJPADTO`, `DailyBonusDAOJPA`, `DailyBonusRepository`, `DailyBonusRepositoryImpl`, `DiaryBonusPersistenciaException`
  - Service: `DiaryBonusService`, `DiaryBonusServiceImpl`
  - Integration: Update `ConfiguracionMercado` to reference `DailyBonus` instead of `LoginBonus`.
  - Test Utils: `TestDailyBonusUtil` replacing `TestLoginBonusUtil`.
- **Rationale**: Clean, consistent naming throughout the entire repository avoids cognitive confusion between legacy login logic and the new dailyBonusMission-driven diary bonus system. Preserves the underlying rewards scaling logic while adding the dailyBonusMission root composite.
- **Alternatives Considered**: Keeping a deprecated `LoginBonus` wrapper class. Rejected because the instruction explicitly directed to change every name for `DailyBonus`.

### 2. Composite Pattern for Daily Missions: Generic `Mission<T>`, Abstract `OrMission<T>` & `AndMission<T>`, and Elimination of Optionality Booleans

- **Context**:
  > *"Mission will have OrMission and AndMission abstract class that represents the Or and And behavior of the composite pattern. IT CANT have booleans representing the optional behavior of it. The dailyBonusMission CANNOT receive Object in complete, it MUST receive a parametrized type defined in the subclass."*
- **Decision**:
  1. **Generic Base Component `Mission<T>`**:
     - `public abstract class Mission<T>`
     - Parameterized completion method: `public abstract void complete(T context);`
     - Status verification: `public abstract boolean isCompleted();`
     - Clones/instantiation: `public abstract Mission<T> clonar();`
     - **NO `Object` parameter**: The parameter `T` must be strongly typed and bound in each concrete subclass.
     - **NO `boolean optional` flag**: Optionality is NOT a boolean flag. It is an emergent property of the composite pattern represented by `OrDailyBonusMission` (where completing any alternative branch satisfies the composite requirement).
  2. **Abstract Composite Nodes `OrMission<T>` and `AndMission<T>`**:
     - `public abstract class OrMission<T> extends Mission<T>`:
       - Encapsulates the logical OR behavior of the composite pattern.
       - Holds child submissions `List<Mission<?>> submissions` (or typed `List<Mission<T>>`).
       - `isCompleted()` returns `submissions.stream().anyMatch(Mission::isCompleted)`.
     - `public abstract class AndMission<T> extends Mission<T>`:
       - Encapsulates the logical AND behavior of the composite pattern.
       - Holds child submissions.
       - `isCompleted()` returns `submissions.stream().allMatch(Mission::isCompleted)`.
  3. **Concrete Subclasses**:
     - `VisitPlayerMission extends Mission<Player>`:
       - Holds a `Player targetPlayer`.
       - Binds `T = Player`: `@Override public void complete(Player player)` checks equality against `this.targetPlayer` and marks completion.
     - `Visit3PlayersMission extends AndMission<Player>`:
       - Composed of 3 distinct `VisitPlayerDailyBonusMission` instances.
       - `@Override public void complete(Player player)` delegates to child `Mission<Player>` instances.
       - `isCompleted()` returns true only when all 3 player visits are completed.
     - `BuyMission extends Mission<Void>`:
       - Binds `T = Void`: `@Override public void complete(Void unused)` sets `completed = true`.
       - Provides parameterless helper `public void complete() { complete(null); }`.
     - `DailyBonusOrMission extends OrMission<Void>`:
       - Concrete `OrDailyBonusMission` root uniting `BuyDailyBonusMission` and `Visit3PlayersDailyBonusMission`.
       - Satisfied when either `buyMission.isCompleted()` OR `visit3PlayersMission.isCompleted()`.
     - `TestMission<T> extends Mission<T>`:
       - Isolated subclass for unit-testing the composite mechanics.
- **Rationale**: Completely type-safe, eliminates runtime casts from raw `Object`, removes artificial boolean flags, and leverages true object-oriented composite semantics.

### 3. Entity Reference in `UserDailyBonus` (`User` entity instead of `userId`)

- **Context**:
  > *"UserDiaryBonus will have the actual User, not its id, in the object."*
- **Decision**:
  - `UserDailyBonus` directly holds `private User user;`
  - Uses Lombok `@Getter` and `@Setter` annotations (no handwritten trivial getters/setters).
  - In persistence: `UserDailyBonusJPADTO` maintains `@OneToOne` / `@ManyToOne` association with `UserJPADTO user;` mapping bidirectionally via `aModelo()` and `desdeModelo()`.
- **Rationale**: Respects Domain-Driven Design and Constitution Principle II (Rich Model). Domain entities hold direct object references to collaborators rather than disconnected foreign keys.

### 4. Lazy Per-User Mission Initialization & Zero Model Logic in Service

- **Context**:
  > *"Also in Research 4, the service MUST call userDailyBonus.inicializarConBonus(DiaryBonus) and inside you check if the userBonus requires initialization. DO NOT DO MODEL IN THE SERVICE EVER."*
- **Decision**:
  - The Service layer MUST NEVER evaluate condition checks like `if (userDailyBonus.requiereInicializacion(fechaActual))`.
  - The Service method simply retrieves `userDailyBonus` and `dailyBonus`, and delegates directly:
    ```java
    userDailyBonus.inicializarConBonus(dailyBonus);
    userDailyBonusRepository.guardar(userDailyBonus);
    ```
  - Inside the domain entity `UserDailyBonus`:
    ```java
    public void inicializarConBonus(DiaryBonus dailyBonus) {
        LocalDate hoy = getFechaActual();
        if (this.activeMissionDate == null || !this.activeMissionDate.isEqual(hoy) || this.activeDailyBonusMission == null) {
            actualizarRacha();
            this.activeMissionDate = hoy;
            this.activeDailyBonusMission = dailyBonus.getMission().clonar();
        }
    }
    ```
  - The scheduled midnight background job ONLY configures the system-wide `DailyBonus` with that day's 3 random players and 1 market buy dailyBonusMission inside `ConfiguracionMercado` / `DailyBonus`. No user records are loaded or iterated.
- **Rationale**: 100% adherence to Constitution Principle II (Rich Model) and user prompt. Business logic remains solely inside domain entities.

### 5. Service Method Signature Contract: Exactly ONE Model Object Parameter

- **Context**:
  > *"Make sure the service also receives just ONE object in each method. The object CANNOT be a RequestDTO, it must be a model object. If needed, it can be a simple record."*
- **Decision**:
  - Every method in `DailyBonusService`, `UserDailyBonusService`, etc., MUST accept **exactly ONE** parameter.
  - The parameter MUST be a domain model entity or domain record (`com.overcode.model.*`). It MUST NOT be a RequestDTO (`RequestDTO` objects remain strictly in `com.overcode.controller.dto`).
  - Method Signatures:
    - `BonusClaim reclamarBonus(User user);` (Accepts 1 `User` domain model)
    - `EstadoBonus obtenerEstadoBonus(User user);` (Accepts 1 `User` domain model)
    - `EstadoBonus registrarVisitaJugador(PeticionVisitaJugador peticion);` (Accepts 1 `PeticionVisitaJugador` domain record: `public record PeticionVisitaJugador(User user, Player player)`)
    - `void registrarCompra(User user);` (Accepts 1 `User` domain model)
    - `UserDiaryBonus recuperarOPermitirCrear(User user);` (Accepts 1 `User` domain model)
- **Rationale**: Protects architecture boundaries. Decouples service interfaces from presentation DTOs while adhering to uniform single-argument invocation semantics (like `PeticionCancelacion` in `TokenMarketService`).

### 6. Lombok Usage Standard

- **Context**:
  > *"You have to use Lombok getter and setter. Dont use simple getters or setters that can be done with those."*
- **Decision**:
  - Use Lombok's `@Getter` and `@Setter` at class or field level across all models, DTOs, and entities.
  - Omit hand-written simple getters/setters that merely return or assign a field.
  - Only write custom methods when implementing domain behavior, validation invariants, or non-trivial logic.

---

## Conclusion

All technical requirements, architectural contracts, and user refinements are resolved. The design artifacts (`data-model.md`, `quickstart.md`, `plan.md`) are updated to strictly reflect these rules.
