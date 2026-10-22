# Quickstart & Validation Guide: Login Streak Bonus (Updated)

## Overview

This guide provides runnable instructions and end-to-end validation scenarios for the daily login streak bonus feature, incorporating `DiaDeLaSemana` enum, `Double` values for credits, and custom domain exceptions.

---

## 1. Prerequisites

- Java 21 SDK installed
- Docker (for Testcontainers PostgreSQL tests)
- Maven wrapper (`./mvnw` or `mvn`)

---

## 2. Test Execution Commands

Run unit tests for domain model and custom exceptions (pure Java, fast, no Spring context):
```bash
./mvnw test -Dtest=LoginBonusTest,UserBonusTest
```

Run service and repository integration tests (Testcontainers):
```bash
./mvnw test -Dtest=DailyBonusServiceTest,LoginBonusRepositoryTest
```

Run End-to-End API tests (RestClient + SpringBootTest):
```bash
./mvnw test -Dtest=DailyBonusE2eTest
```

---

## 3. Validation Scenarios & Edge Cases

### Scenario 1: Bonus is NEVER Awarded Automatically on Login
- **Setup**: User logs in with valid credentials via `POST /auth/login`.
- **Validation**: Verify that `creditBalance` remains `0.0`, `streakCount` remains `0`, and `lastBonusClaimDate` is `null`.

### Scenario 2: First Time Bonus Claim (Happy Path Day 1 - LUNES)
- **Setup**: User executes `POST /api/daily-bonus/claim`.
- **Validation**:
  - Response status: `200 OK`.
  - `creditsAwarded`: `1.0` (LUNES value in base streak map).
  - `newStreak`: `1`.
  - User's `creditBalance`: `1.0`.
  - `lastBonusClaimDate`: Today's UTC date.

### Scenario 3: Consecutive Daily Claims (Streak Progression - MARTES)
- **Setup**: Simulate or advance clock to Day 2 (`today + 1 day`). User executes `POST /api/daily-bonus/claim`.
- **Validation**:
  - Response status: `200 OK`.
  - `creditsAwarded`: `2.0` (MARTES value).
  - `newStreak`: `2`.
  - User's cumulative `creditBalance`: `3.0`.

### Scenario 4: Missed Day Resets Streak (Edge Case)
- **Setup**: User claimed Day 2 on Tuesday. On Thursday (`today + 2 days`, skipping Wednesday), user calls `POST /api/daily-bonus/claim`.
- **Validation**:
  - Response status: `200 OK`.
  - `creditsAwarded`: `1.0` (Resets to Day 1 / LUNES reward).
  - `newStreak`: `1` (Streak reset).
  - User's `creditBalance` increases by `1.0`.

### Scenario 5: Day 7 to Day 8 Weekly Cycle Loop (Edge Case)
- **Setup**: User maintains a streak up to Day 7 (claiming `15.0` credits for DOMINGO). On Day 8, user calls `POST /api/daily-bonus/claim`.
- **Validation**:
  - Response status: `200 OK`.
  - `creditsAwarded`: `1.0` (LUNES reward).
  - `newStreak`: `1` (Cycled back cleanly to week start).

### Scenario 6: Re-claiming on the Same Calendar Day Throws Custom Domain Exception
- **Setup**: User claims today's bonus, then sends another `POST /api/daily-bonus/claim` on the same UTC calendar day.
- **Validation**:
  - Domain model / service throws `BonusYaReclamadoHoyException` (NOT `IllegalArgumentException` or `IllegalStateException`).
  - Response status: `400 Bad Request`.
  - Error response message: `"El bonus diario de hoy ya ha sido reclamado."`
  - Balance and streak remain unchanged.

### Scenario 7: Querying Daily Bonus Status
- **Setup**: User calls `GET /api/daily-bonus` before and after claiming.
- **Validation Before Claim**:
  - `alreadyClaimedToday`: `false`.
  - `todayBonus`: `1.0` (or next scheduled amount based on streak).
  - `weeklyBonuses`: Map containing `LUNES: 1.0, MARTES: 2.0, MIERCOLES: 2.0, JUEVES: 5.0, VIERNES: 5.0, SABADO: 10.0, DOMINGO: 15.0`.
- **Validation After Claim**:
  - `alreadyClaimedToday`: `true`.
  - `currentStreak`: `1`.
  - `todayBonus`: `1.0`.

---

## 4. Test Helper Utilities & Scalability

Unit, service, and E2E tests MUST use reusable static factory methods:
- `TestUserUtil.getUsuarioDeNombre(String name)`
- `TestUserUtil.getUsuarioConRacha(String name, int racha, LocalDate ultimoReclamo)`
- `TestLoginBonusUtil.getLoginBonusBase()`: returns a `LoginBonus` with `Map<DiaDeLaSemana, Double>` containing `[LUNES: 1.0, MARTES: 2.0, MIERCOLES: 2.0, JUEVES: 5.0, VIERNES: 5.0, SABADO: 10.0, DOMINGO: 15.0]`.
- All test methods written in Latin American Spanish:
  - `alReclamarBonusDiarioPorPrimeraVezSeOtorgaBonusLunesYLaRachaEs1()`
  - `alReclamarEnDiasConsecutivosSeIncrementaLaRachaYElBonus()`
  - `alPasarMasDeUnDiaSinReclamarSeReiniciaLaRachaADia1()`
  - `alCompletarCicloDe7DiasElSiguienteReclamoComienzaEnLunes()`
  - `elBonusNuncaSeOtorgaSoloPorLoguearse()`
  - `alIntentarReclamarDosVecesElMismoDiaLanzaBonusYaReclamadoHoyException()`
  - `alCrearLoginBonusConValoresNulosONegativosLanzaValorBonusInvalidoException()`
  - `alConsultarDiaInvalidoLanzaDiaDeLaSemanaInvalidoException()`
