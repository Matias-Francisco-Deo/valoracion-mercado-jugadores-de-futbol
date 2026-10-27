# Research & Decisions: Login Streak Bonus (Updated)

## Overview

This feature introduces a daily login streak bonus system into the backend. Users receive credits based on their daily streak progression (1 to 7 days), which cycles back to Day 1 after reaching Day 7 or upon missing a day. The bonus calculation and scaling logic is encapsulated in a dedicated `LoginBonus` domain model. The system allows multiple `LoginBonus` definitions to be stored in the database, while one active instance is configured in `ConfiguracionCotizaciones`. Bonuses are never granted automatically upon login/registration; users must explicitly invoke a claim endpoint.

---

## Technical Context & Decisions

### 1. `DiaDeLaSemana` Enum & Bonus Map
- **Decision**: Define `DiaDeLaSemana` enum (`LUNES`, `MARTES`, `MIERCOLES`, `JUEVES`, `VIERNES`, `SABADO`, `DOMINGO`).
- **Domain Structure**: `LoginBonus` MUST store rewards in a `Map<DiaDeLaSemana, Double> bonusRewards`.
- **Streak Mapping**: `DiaDeLaSemana` provides helper method `desdeRacha(int racha)` (mapping 1 to LUNES, 2 to MARTES, ..., 7 to DOMINGO) and `desdeDayOfWeek(DayOfWeek dayOfWeek)`.
- **Validation**: Any invalid streak index or null day triggers `DiaInvalidoException` or `DiaDeLaSemanaInvalidoException` (never `IllegalArgumentException`).

### 2. Credit Values as `Double`
- **Decision**: All credit-related values across the feature (bonus amounts, credits awarded, rewards map, and balance credits) MUST use `Double` instead of `Integer`.
- **Streak Preservation**: Streak counts remain `Integer` (1 to 7).
- **User Balance**: `User` credit balance operations support `Double` (`Double creditBalance`, `creditCredits(Double amount)`), ensuring decimal precision and consistency with other monetary models like `Player` valuations.

### 3. Strict Custom Exceptions (NO Java Default Exceptions)
- **Constraint**: The system CANNOT use Java default exceptions (such as `IllegalArgumentException`, `IllegalStateException`, `NoSuchElementException`).
- **Model Layer Exceptions** (`com.overcode.model.bonus.exception` / `com.overcode.model.exception`):
  - `BonusYaReclamadoHoyException`: Attempting to claim bonus twice on the same UTC calendar day.
  - `DiaInvalidoException`: Streak count outside expected bounds.
  - `DiaDeLaSemanaInvalidoException`: Null or unmapped day of week.
  - `ValorBonusInvalidoException`: Bonus reward amount is null, $\le 0.0$.
  - `FechaReclamoBonusLoginInvalidaException`: Claim date is null or invalid.
  - `LoginBonusInvalidoException`: LoginBonus map does not contain all 7 days or is improperly constructed.
- **Service Layer Exceptions** (`com.overcode.service.exception`):
  - `LoginBonusNoConfiguradoException`: No active `LoginBonus` configured in `ConfiguracionCotizaciones`.
  - `ReclamoBonusNoPermitidoException`: Business rule check fails during bonus claim.

### 4. Active Bonus Configuration in `ConfiguracionCotizaciones`
- `ConfiguracionCotizaciones` holds the active `LoginBonus`:
  ```java
  private LoginBonus loginBonus;
  ```
- Multiple `LoginBonus` entities can reside in the database, but only one is selected in `ConfiguracionCotizaciones`.
- In `DataInitializer`:
  - Instantiates default `LoginBonus` with `Map<DiaDeLaSemana, Double>`:
    - `LUNES`: `1.0`
    - `MARTES`: `2.0`
    - `MIERCOLES`: `2.0`
    - `JUEVES`: `5.0`
    - `VIERNES`: `5.0`
    - `SABADO`: `10.0`
    - `DOMINGO`: `15.0`
  - Saves the instance in `LoginBonusRepository` if not present.
  - Sets it as the active bonus in `ConfiguracionCotizaciones`.

### 5. Explicit User Claim Requirement
- **Rule**: A login bonus is **NEVER** awarded just for logging in or registering.
- The user MUST execute `POST /api/daily-bonus/claim`.
- Claims are idempotent per UTC calendar day. Duplicate attempts return a handled error backed by `BonusYaReclamadoHoyException`.

### 6. Testing Strategy in Latin American Spanish & Scalability
- Unit tests for `LoginBonusTest` and `UserBonusTest` test happy path and edge cases in Spanish.
- Scalable test factories:
  - `TestUserUtil.getUsuarioDeNombre(String name)`
  - `TestUserUtil.getUsuarioConRacha(String name, int racha, LocalDate ultimoReclamo)`
  - `TestLoginBonusUtil.getLoginBonusBase()`
  - `TestLoginBonusUtil.getLoginBonusPersonalizado(Map<DiaDeLaSemana, Double> rewards)`
- Complete edge case coverage:
  - Attempting to claim twice on the same day.
  - Broken streaks (missing 1 or more calendar days).
  - Day 7 to Day 8 weekly cycle reset.
  - Verification that logging in or creating account yields zero bonus credits until explicitly claimed.
  - Exception assertions verify specific custom exceptions, never generic Java exceptions.
