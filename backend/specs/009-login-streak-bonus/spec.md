# Feature Specification: login-streak-bonus

**Feature Branch**: `[009-login-streak-bonus]`

**Created**: 2026-10-22

**Status**: Draft (Updated)

**Input**: User description: "We are going to develop a login bonus in this backend (not frontend) that is going to allow users to get small amounts of credit each day, and if they chain days the bonus will be a bit bigger. It will reset weekly, and also if they user fails to keep the streak going."

## Clarifications

### Session 2026-10-22

- Q: How do users receive their daily bonus? → A: Users must explicitly claim it via `POST /api/daily-bonus/claim`. The bonus is **never** granted automatically upon login or registration; the user MUST explicitly call the claim endpoint.
- Q: Where and how should the weekly bonus reward amounts (credits for Days 1 through 7) be configured? → A: Encapsulated in the `LoginBonus` domain model as `Map<DiaDeLaSemana, Double>`, stored in the database with multiple rows supported, and one active instance linked to `ConfiguracionCotizaciones`. Seeded by `DataInitializer`. (Admin dynamic PATCH endpoint deferred).
- Q: Should the Day 1 login bonus be automatically granted upon user registration or login? → A: No. Registration and credential authentication alone do NOT award bonus credits or modify streak progression.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Explicit Daily Bonus Claim on Consecutive Days (Priority: P1)

As an authenticated user claiming my daily bonus on consecutive days, I want to receive an increasing daily credit bonus for each day I maintain my streak, so that I am rewarded and incentivized to return every day.

**Why this priority**: Core value proposition of the daily login incentive. Without consecutive bonus progression, the streak mechanism has no incentive effect.

**Independent Test**: Can be tested independently by simulating user claims via `POST /api/daily-bonus/claim` across consecutive calendar days and verifying that the user's credit balance increases by the scheduled bonus amount (`Double`) for each streak day, and verifying that registering or logging in without calling the endpoint awards zero credits.

**Acceptance Scenarios**:

1. **Given** an authenticated user who has never claimed a daily bonus, **When** they call `POST /api/daily-bonus/claim`, **Then** their streak count becomes 1 and their credit balance increases by the Day 1 bonus amount (1.0 credit).
2. **Given** a user registers a new account or logs in with existing credentials, **When** authentication succeeds, **Then** their streak count and credit balance remain unchanged until an explicit claim is executed.
3. **Given** a user whose last bonus was claimed yesterday (streak count = N, where 1 <= N < 7), **When** they call `POST /api/daily-bonus/claim` today, **Then** their streak count increases to N + 1 and their credit balance increases by the Day (N + 1) bonus amount.
4. **Given** a user successfully claims a daily bonus, **When** the transaction finishes, **Then** the user's `streakCount` and `lastBonusClaimDate` are updated and the response returns the credited amount and updated balance.

---

### User Story 2 - Streak Reset on Missed Days (Priority: P1)

As the system, I want to reset a user's streak back to Day 1 when they fail to claim on consecutive calendar days, so that the streak reflects strictly uninterrupted daily activity.

**Why this priority**: Essential to enforce the rules of a streak; users must know that missing a day resets their bonus progression.

**Independent Test**: Can be tested independently by recording a claim on day T, letting at least one full calendar day lapse without claiming (e.g. claiming on day T + 2), and verifying the streak count resets to 1 with the Day 1 bonus amount (1.0 credit).

**Acceptance Scenarios**:

1. **Given** a user last claimed their bonus 2 or more calendar days ago, **When** they call `POST /api/daily-bonus/claim` today, **Then** their streak count resets to 1 and they receive the Day 1 bonus amount (1.0 credit).
2. **Given** a user with a broken streak claims today, **When** the bonus is awarded, **Then** their credit balance increases by the Day 1 bonus amount and the last claim date is updated to today.

---

### User Story 3 - Weekly Streak Cycle Reset (Priority: P2)

As a user who completes a full 7-day streak cycle, I want the bonus cycle to cleanly reset back to Day 1 on the 8th consecutive day, so that I can begin a new weekly cycle of rewards without losing engagement.

**Why this priority**: Keeps the reward economy bounded while giving users a recurring weekly milestone loop.

**Independent Test**: Can be tested by chaining 7 consecutive daily claims, claiming the Day 7 maximum bonus (15.0 credits), and verifying that the 8th consecutive day claim awards the Day 1 bonus (1.0 credit) and restarts streak count at 1.

**Acceptance Scenarios**:

1. **Given** a user who claimed Day 7 bonus yesterday, **When** they call `POST /api/daily-bonus/claim` today, **Then** the streak cycle restarts, the streak count becomes 1, and the user receives the Day 1 bonus amount (1.0 credit).
2. **Given** the weekly cycle restart, **When** the bonus is credited, **Then** the user's total credit balance reflects the addition of the Day 1 reward.

---

### User Story 4 - Single Bonus Award Per Calendar Day and Idempotency (Priority: P2)

As a user or client application, I want duplicate claim requests within the same calendar day to be rejected with a clear message, so that credits are never granted more than once per UTC day.

**Why this priority**: Prevents reward exploitation and maintains strict daily claim limits.

**Independent Test**: Can be tested by calling `POST /api/daily-bonus/claim` twice on the same UTC calendar day. The first call succeeds (HTTP 200 OK), while the second call is rejected with HTTP 400 Bad Request and error message `"El bonus diario de hoy ya ha sido reclamado."`.

**Acceptance Scenarios**:

1. **Given** an authenticated user with an active session who has not yet claimed today's bonus, **When** they call `POST /api/daily-bonus/claim`, **Then** their streak advances according to streak rules and credits are awarded.
2. **Given** a user has already received today's bonus, **When** they call `POST /api/daily-bonus/claim` again on the same day, **Then** no additional bonus credits are granted, the streak count remains unchanged, and the system responds with HTTP 400 Bad Request throwing `BonusYaReclamadoHoyException`.

---

### User Story 5 - Bonus and Streak Status Query (Priority: P3)

As a user or client application, I want to query `GET /api/daily-bonus` to inspect current streak status, today's projected bonus, whether today's bonus is already claimed, and the full weekly rewards schedule, so that the interface can display clear streak progress.

**Why this priority**: Enables transparency and allows the user interface to show streak counters and reward tiers.

**Independent Test**: Can be tested by retrieving `GET /api/daily-bonus` for a user before and after claiming today's bonus and verifying the returned response structure.

**Acceptance Scenarios**:

1. **Given** an authenticated user who has not claimed today, **When** their streak status is queried via `GET /api/daily-bonus`, **Then** the response indicates `alreadyClaimedToday: false`, the current active streak count, today's projected bonus amount (`Double`), and the complete `weeklyBonuses` map.
2. **Given** an authenticated user who has already received today's bonus, **When** their streak status is queried via `GET /api/daily-bonus`, **Then** the response indicates `alreadyClaimedToday: true`, current streak count, and today's claimed bonus amount.

---

### User Story 6 - Administrator Modifies Bonus Rewards (Deferred / Out of Scope for Phase 1)

*(Deferred)* Dynamic administrator updates via a dedicated REST endpoint are deferred to a subsequent administrative release. In this release, the active `LoginBonus` schedule is seeded and configured in `ConfiguracionCotizaciones` during application initialization.

---

### Edge Cases

- **Concurrent Claims**: If a user triggers `POST /api/daily-bonus/claim` multiple times concurrently within milliseconds, the system MUST process the bonus claim idempotently, awarding credits exactly once and returning HTTP 400 on concurrent duplicates.
- **Timezone Boundary**: Calendar day transitions are determined using UTC midnight (00:00:00 UTC) as the global standard reference, avoiding discrepancies caused by local device clocks.
- **Brand New Users**: Initialized with streak count 0 and null last claim date. The first explicit claim sets streak count to 1 and awards Day 1 bonus (1.0 credit).
- **Long Inactivity**: If an account has remained inactive for weeks or months, the first subsequent claim safely resets the streak counter to 1 without encountering historical date overflow or errors.
- **Zero or Negative Credit Addition Prevention**: All reward values are positive `Double` numbers strictly greater than 0.0.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST award bonus credits to an authenticated user strictly upon explicit invocation of `POST /api/daily-bonus/claim`. User registration and login alone MUST NOT award bonus credits.
- **FR-002**: The system MUST maintain persistent streak tracking for each user, including current streak count (Integer 1 to 7) and the date of the most recent bonus award (UTC).
- **FR-003**: If a user claims a bonus on consecutive calendar days (the current UTC date is exactly one calendar day after the last award date), the system MUST increment the streak count by 1 (up to Day 7) and credit the corresponding `Double` bonus amount.
- **FR-004**: If more than one calendar day has elapsed since the user's last bonus award date, the system MUST reset the streak count to 1 and award the Day 1 bonus amount (`1.0`).
- **FR-005**: If the user's previous bonus award was on Day 7 of the streak and the user claims on the next consecutive day, the system MUST reset the streak cycle to Day 1 and award the Day 1 bonus amount (`1.0`).
- **FR-006**: The system MUST ensure that only one bonus is credited per user per calendar day (UTC); additional claim requests on the same day MUST throw `BonusYaReclamadoHoyException` returning HTTP 400 Bad Request and MUST NOT award credits or alter streak progression.
- **FR-007**: The system MUST add awarded bonus credits (`Double`) directly to the user's credit balance upon a successful claim.
- **FR-008**: The system MUST provide the outcome of the daily login bonus (`givenBonus`, `newStreak`, `currentCreditBalance`, `claimDate`, `message`) in the response to `POST /api/daily-bonus/claim`.
- **FR-009**: The system MUST provide an endpoint `GET /api/daily-bonus` to inspect the user's current streak status (`currentStreak`, `nextBonus`, `alreadyClaimedToday`, and `weeklyBonuses`).
- **FR-010**: The system MUST persist the reward schedule as a `LoginBonus` entity (`Map<DiaDeLaSemana, Double>`) in the database with base default values, with the active instance linked to `ConfiguracionCotizaciones`.
- **FR-011**: *(Deferred)* Dynamic administrator modification via endpoint is deferred to a future administrative feature.
- **FR-012**: *(Deferred)* Dynamic administrator validation rules are deferred.

### Key Entities *(include if feature involves data)*

- **User (Updated)**:
  - *Attributes*: `streakCount` (Integer 0 to 7), `lastBonusClaimDate` (LocalDate), `creditBalance` (Double).
- **LoginBonus**:
  - *Attributes*: `bonusRewards` (`Map<DiaDeLaSemana, Double>`), scaling and streak calculation methods (`verificarPuedeReclamarBonus`, `getBonusParaUser`).
- **DiaDeLaSemana**:
  - *Values*: `LUNES`, `MARTES`, `MIERCOLES`, `JUEVES`, `VIERNES`, `SABADO`, `DOMINGO`.
- **ConfiguracionCotizaciones (Updated)**:
  - *Attributes*: Holds reference to the active `LoginBonus`.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of first daily claims via `POST /api/daily-bonus/claim` on a calendar day receive the exact scheduled credit bonus (`Double`) for their streak level.
- **SC-002**: 100% of duplicate claim calls within the same calendar day result in HTTP 400 Bad Request and 0 additional credits granted (0% duplicate bonus rate).
- **SC-003**: 100% of claims following a missed day (>1 day gap) properly reset the streak counter to Day 1.
- **SC-004**: 100% of consecutive claims after Day 7 successfully cycle back to Day 1.
- **SC-005**: Status query (`GET /api/daily-bonus`) and claim processing (`POST /api/daily-bonus/claim`) complete in under 30 milliseconds under standard operational load.

## Assumptions

- **Calendar Boundary**: All daily cycle calculations are based on UTC (00:00:00 UTC to 23:59:59 UTC) to ensure consistent, timezone-agnostic streak progression.
- **Initial Database Seeding**: The database is initialized via `DataInitializer` with default values for the weekly bonus schedule:
  - `LUNES` (Day 1): `1.0` credit
  - `MARTES` (Day 2): `2.0` credits
  - `MIERCOLES` (Day 3): `2.0` credits
  - `JUEVES` (Day 4): `5.0` credits
  - `VIERNES` (Day 5): `5.0` credits
  - `SABADO` (Day 6): `10.0` credits
  - `DOMINGO` (Day 7): `15.0` credits
- **Triggering Points**: The daily bonus is evaluated and awarded strictly upon explicit user request via `POST /api/daily-bonus/claim`. Registration (`/auth/register`) and authentication (`/auth/login`) alone do not award bonus credits.
- **Frontend Independence**: This specification is strictly for the backend service logic, domain model, persistence, and API contracts.
