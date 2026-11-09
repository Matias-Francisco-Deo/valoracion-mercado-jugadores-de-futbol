# Quickstart & Verification Guide: diary-bonus-missions

## Prerequisites

- **Java Development Kit**: OpenJDK 21 LTS installed and on `PATH`
- **Docker**: Docker engine running (required for Testcontainers PostgreSQL)
- **Maven**: `mvn` 3.9+ or local `./mvnw` wrapper

---

## Environment Setup

Ensure test database containers can start:

```powershell
docker info
```

Build and compile the backend without running tests:

```powershell
./mvnw clean compile test-compile
```

---

## Validation Scenarios

### Scenario 1: Model Composite Mission Unit Tests
Validates the composite structure (`OrMission<T>`, `AndMission<T>`, `Visit3PlayersMission`, `VisitPlayerMission`, `BuyMission`) with strongly-typed `.complete(T)` arguments, no boolean optional flags, and Lombok `@Getter`/`@Setter` usage.

**Test Command**:
```powershell
./mvnw test -Dtest=MissionTest,VisitPlayerMissionTest,BuyMissionTest,UserDiaryBonusTest,DiaryBonusTest
```

**Expected Result**:
- Unit tests pass with 0 failures and 0 errors.
- `TestMission<String>` verifies `AndMission` and `OrMission` completion logic in isolation.
- `VisitPlayerMission` verifies that `complete(Player player)` requires an exact player ID match and rejects wrong players.
- `BuyMission` verifies parameterless completion `complete()` sets completed.
- `UserDailyBonus` verifies that `inicializarConBonus(dailyBonus)` encapsulates the date checks and sets up today's cloned mission tree cleanly.

---

### Scenario 2: UserDiaryBonus and Mission Persistence Integration Tests
Validates that `UserDiaryBonusJPADTO` (with direct `@OneToOne`/`@ManyToOne` mapping to `UserJPADTO`), `MissionJPADTO`, and `DailyBonusJPADTO` correctly persist recursive composite trees in PostgreSQL via Testcontainers.

**Test Command**:
```powershell
./mvnw test -Dtest=UserDiaryBonusRepositoryTest,DiaryBonusRepositoryTest,MissionRepositoryTest
```

**Expected Result**:
- Testcontainers spins up PostgreSQL 16.
- Saves composite mission trees and retrieves them matching domain structures.
- Persists user-specific progress linking `UserDailyBonus` to `User`.

---

### Scenario 3: Lazy Mission Initialization & Service Logic Integration Tests
Validates that users querying bonus status get initialized lazily on-demand without bulk database queries at midnight, verifying that service methods accept strictly one domain model or record parameter and delegate to `userDailyBonus.inicializarConBonus(dailyBonus)`.

**Test Command**:
```powershell
./mvnw test -Dtest=BonusServiceImplTest,UserDiaryBonusServiceImplTest
```

**Expected Result**:
- Querying status on day T initializes missions matching day T's template.
- Consecutive day claim increases streak count.
- Skipped day claim resets streak count to 0 with base reward.
- Attempting to claim before mission completion throws `RequisitosMisionesNoCumplidosException`.

---

### Scenario 4: End-to-End API Flow Verification
Validates the complete HTTP workflow using Spring Boot Test.

**Test Command**:
```powershell
./mvnw test -Dtest=DailyBonusE2eTest
```

**Expected Flow**:
1. `GET /api/daily-bonus` with bearer token: returns status with 4 missions (3 visits + 1 buy), `puedeReclamar: false`.
2. `POST /api/daily-bonus/claim`: fails with `400 Bad Request` ("Requisitos de misiones no cumplidos").
3. `POST /api/daily-bonus/visit/{p1.id}`, `POST /api/daily-bonus/visit/{p2.id}`, `POST /api/daily-bonus/visit/{p3.id}`: all 3 visits succeed, updating `puedeReclamar: true`.
4. `POST /api/daily-bonus/claim`: succeeds with `200 OK`, credits awarded, `streakCount` incremented.
5. `POST /api/daily-bonus/claim` (duplicate attempt): fails with `400 Bad Request` ("Bonus ya reclamado hoy").

---

## References

- Data Model Details: [data-model.md](data-model.md)
- REST API Contracts: [contracts/daily-bonus-api.yaml](contracts/daily-bonus-api.yaml)
- Technical Decisions: [research.md](research.md)
