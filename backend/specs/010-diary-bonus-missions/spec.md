# Feature Specification: diary-bonus-missions

**Feature Branch**: `[010-diary-bonus-missions]`

**Created**: 2026-11-06

**Status**: Ready for Planning

**Input**: User description: "We are going to develop some changes to our LoginBonus. For instance, everything should be renamed to DiaryBonus, since we are changing its behavior. Now, the User will have the data related to the bonus stored in UserDiaryBonus, which will be persisted. We are going to use Missions for these bonuses. Basically, DiaryBonus will be scheduled each day to get missions, which for now will be "Visit x player page" (where x is random) and "Buy a token from the market". If you do one of these, it will allow you to claim the daily reward. The mechanics of streak will persist, you still have to do these everyday or else you go back to a 0 streak, with the minimum reward. We want to have multiple missions that can be done in one day, to be clear, not just 2. For this first version, we will have 3 separate Missions of "visit x player page" and 1 mission of "buy any token from the market". If you do the first 3 missions, OR do the buy one, you will have the right to claim the today's bonus."

## Clarifications

### Session 2026-11-06

- Q: Are daily missions generated globally once per day for all users, or independently generated per user? → A: System-wide generation. The daily schedule selects 3 random players and 1 market buy mission for the day; all users share the same daily mission targets.
- Q: How should player profile visits be tracked? → A: Dedicated visit notification action. The client explicitly signals a visit action for the designated player profile for the authenticated user, isolating mission progress from passive read queries.
- Q: How should the UserDiaryBonus entity be associated with a user and structured over time? → A: Single 1-to-1 persistent profile per user. Each user has one UserDiaryBonus record storing current streak count, last claim date, and active day's completed mission states (refreshed as calendar days roll over).
- Q: How should the daily mission schedule for each calendar day be initialized in the system? → A: Scheduled background task for system targets with lazy per-user assignment. The system-wide `DiaryBonus` targets (3 random players + 1 market buy mission) are refreshed by the scheduled background task at midnight UTC; each user's `UserDiaryBonus` mission tree is cloned on-demand upon their first daily interaction via `inicializarConBonus` to avoid mapping all users into JVM memory at midnight.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Completing Daily Missions via Player Visits to Unlock and Claim Bonus (Priority: P1)

As an active user, I want to complete three distinct daily "Visit player profile" missions so that I qualify to claim today's daily bonus reward and advance my streak without having to purchase a token.

**Why this priority**: Core free-to-play engagement path. Allows any active user to earn their daily bonus solely through exploration and engagement.

**Independent Test**: Can be tested independently by simulating a user registering visits for each of the 3 designated daily players, verifying mission completion progress, verifying claim eligibility changes from ineligible to eligible, and verifying that claiming successfully awards credits and increments streak count.

**Acceptance Scenarios**:

1. **Given** an authenticated user on a new calendar day with zero daily missions completed, **When** the user checks their bonus status, **Then** they see 3 distinct player visit missions and 1 token purchase mission, with claim eligibility set to false.
2. **Given** a user who has visited 1 or 2 of the 3 required player profiles, **When** they attempt to claim today's bonus, **Then** the claim is rejected because the visit requirement (3 out of 3) is not yet satisfied.
3. **Given** a user who has visited all 3 designated player profiles for the current day, **When** they request to claim today's bonus, **Then** the claim succeeds, credits corresponding to their current streak reward tier are credited, and today's claim status is marked as completed.

---

### User Story 2 - Completing Daily Missions via Market Purchase to Unlock and Claim Bonus (Priority: P1)

As a trader or active market user, I want purchasing any player token on the market to immediately qualify me for today's daily bonus so that I have a fast-track alternative to claiming my daily reward.

**Why this priority**: Core marketplace incentive path. Directly connects market participation with daily progression rewards.

**Independent Test**: Can be tested independently by having a user purchase any token from the marketplace without completing any player visit missions, verifying that claim eligibility becomes true, and verifying that claiming awards the daily bonus.

**Acceptance Scenarios**:

1. **Given** an authenticated user who has completed zero player visit missions today, **When** they purchase any token on the market, **Then** the "Buy a token from the market" mission is marked as completed and claim eligibility becomes true.
2. **Given** a user who has satisfied claim eligibility via token purchase, **When** they submit a claim for today's bonus, **Then** the bonus is awarded, streak count increments, and further claims for the same day are blocked.
3. **Given** a user who has completed both the market purchase AND all 3 player visit missions, **When** they claim today's bonus, **Then** the daily bonus is awarded exactly once for that calendar day without duplicate rewards.

---

### User Story 3 - Daily Streak Progression and Reset on Inactivity (Priority: P1)

As a daily user, I want my streak count to increase consecutively for each day I qualify and claim my bonus, and reset to the base level if I miss a calendar day, so that consistent daily engagement is rewarded.

**Why this priority**: Drives retention and defines the core economic incentive of the daily bonus system.

**Independent Test**: Can be tested independently by claiming on consecutive days to observe increasing reward tiers, and then skipping one calendar day to observe that the streak resets to 0 with the base reward tier upon the next qualifying claim.

**Acceptance Scenarios**:

1. **Given** a user whose last claimed bonus was on the previous consecutive calendar day, **When** they qualify and claim today's bonus, **Then** their streak count increases by 1, and they receive the higher reward tier corresponding to that streak level.
2. **Given** a user who achieved a streak on day T but did not claim on day T + 1, **When** they qualify and claim on day T + 2, **Then** their streak resets to 0 (or base streak level) and they receive the minimum base reward.
3. **Given** a user who reaches the maximum streak tier (e.g., Day 7 of the cycle), **When** they qualify and claim on the subsequent consecutive day, **Then** the streak cycle rolls over to the starting tier.

---

### User Story 4 - Tracking Daily Mission Progress and Reward Schedule (Priority: P2)

As a user, I want to inspect my current daily missions, completion status for each mission, claim eligibility, current streak, and the upcoming reward schedule so that I know what tasks to complete and what rewards to expect.

**Why this priority**: Provides essential visibility into daily goals and user progression.

**Independent Test**: Can be tested independently by requesting bonus status across different states (unstarted, partially completed, fully qualified, and already claimed) and verifying that returned data accurately reflects current mission states and streak.

**Acceptance Scenarios**:

1. **Given** a user starts a new day where the daily mission schedule has been generated, **When** they view their daily bonus status, **Then** they receive the list of all daily missions (3 player targets with player identity details and 1 buy token mission), each marked incomplete, along with today's potential reward and active streak count.
2. **Given** a user has visited 2 out of 3 players, **When** they view their daily bonus status, **Then** those 2 visit missions are displayed as completed, the 3rd visit mission and the purchase mission are displayed as incomplete, and claim eligibility is false.
3. **Given** a user has already claimed today's bonus, **When** they view their status, **Then** the status clearly indicates that today's reward has already been claimed.

---

### User Story 5 - Rejection of Premature Claims and Duplicate Claim Prevention (Priority: P2)

As the system, I want to prevent users from claiming daily rewards before satisfying mission requirements and prevent duplicate claims within the same calendar day, so that the reward economy remains fair and secure.

**Why this priority**: Safeguards the virtual economy and prevents exploitation.

**Independent Test**: Can be tested independently by attempting to claim without meeting qualifications (expecting failure) and attempting to claim twice in the same calendar day after qualifying (expecting rejection on the second attempt).

**Acceptance Scenarios**:

1. **Given** a user who has visited only 2 player pages and has not bought a token, **When** they attempt to claim today's bonus, **Then** the system rejects the request with an informative error stating qualification requirements are unmet.
2. **Given** a user who has already claimed today's bonus, **When** they attempt to claim again on the same calendar day, **Then** the system rejects the request with an error indicating the bonus has already been claimed today.

---

### Edge Cases

- What happens if the scheduled task for today has not yet executed? The system rejects daily bonus requests with an informative error indicating that the daily mission schedule has not yet been initialized for today.
- What happens if the system contains fewer than 3 available players in total? The scheduled task must verify that at least 3 active players exist in the system before generating missions; if fewer exist, it raises a configuration error.
- What happens if a user visits the same designated player profile multiple times? Visiting the same player multiple times satisfies only that specific player's mission; the 3 player visit missions require visiting 3 distinct designated players.
- What happens if a user buys a token before checking their missions? Any market token purchase executed during the calendar day automatically credits the daily purchase mission, regardless of whether the user viewed their mission list prior to purchasing.
- What happens when calendar days roll over across timezones? Daily mission schedules and claim records must operate strictly on standardized UTC calendar days (resetting at 00:00:00 UTC).
- What happens if a user completes qualification requirements just before midnight UTC and claims after midnight UTC? Eligibility is evaluated for the calendar day of the claim; if the day has rolled over, new daily missions apply.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST rename all domain concepts, persistence entities, services, and endpoints formerly known as "LoginBonus" to "DiaryBonus".
- **FR-002**: System MUST store each user's daily bonus state in a dedicated 1-to-1 persisted `UserDiaryBonus` profile linked to the user, tracking streak count, last claim date, and current day mission completions.
- **FR-003**: System MUST schedule and generate daily missions for each calendar day strictly via an automated background task executing at midnight UTC (00:00:00 UTC).
- **FR-004**: System MUST generate daily missions system-wide once per day, where all users share the same set of 3 designated player visit targets and 1 market token purchase mission for that calendar date.
- **FR-005**: For the initial version, the daily mission set MUST include exactly three distinct "Visit player profile" missions (each targeting a distinct random player) and one "Buy a token from the market" mission.
- **FR-006**: System MUST mark a user as qualified to claim today's bonus if and only if the user has completed ALL three "Visit player profile" missions OR has completed the "Buy a token from the market" mission.
- **FR-007**: System MUST track individual mission completion status per user in `UserDiaryBonus` for the active calendar day.
- **FR-008**: System MUST track player profile visit mission completions via an explicit visit notification action performed by the authenticated user for the designated player.
- **FR-009**: System MUST allow an authenticated user to claim today's reward only when their qualification condition is met and they have not yet claimed today.
- **FR-010**: System MUST reject claim requests with an explicit error when the user has not satisfied either qualifying condition (all 3 visits or 1 purchase).
- **FR-011**: System MUST reject duplicate claim attempts within the same calendar day.
- **FR-012**: System MUST maintain the streak count: incrementing by 1 when claimed on consecutive calendar days, resetting to 0 (base tier) when at least one calendar day is missed, and cycling appropriately according to the configured reward schedule.
- **FR-013**: System MUST credit the user's account with the bonus amount corresponding to their streak level upon successful claim.
- **FR-014**: System MUST allow users to query their daily status, including today's missions, individual completion progress, claim eligibility, current streak, and the weekly reward schedule.

### Key Entities *(include if feature involves data)*

- **DiaryBonus**: Represents the daily bonus configuration and daily schedule, including weekly streak reward tiers and the set of missions scheduled for the day.
- **UserDiaryBonus**: Represents a user's persistent daily bonus profile in a 1-to-1 relationship with `User`. Encapsulates the user's ongoing streak count, last claim date, and active calendar day mission completion records (cleared/refreshed when calendar days advance).
- **Mission**: Represents an individual daily objective or composite branch (via `OrMission` and `AndMission`) that can be completed by a user.
  - Subtypes/Variations:
    - *VisitPlayerMission*: Targets a specific player whose profile must be visited (`complete(Player player)`).
    - *Visit3PlayersMission*: Composite `AndMission<Player>` requiring 3 player visits.
    - *BuyMission*: Requires purchasing any token from the marketplace (`complete(Void unused)`).
    - *DailyBonusOrMission*: Root composite `OrMission<Void>` uniting the buy path and the 3-visit path.
    - *Note*: Individual completion states are encapsulated directly within the user's active cloned mission tree on `UserDiaryBonus` rather than an isolated join table.
- **DiaryBonusRewards**: Encapsulates the reward tiers mapped to streak levels.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users who complete all 3 player visit missions or 1 token purchase can successfully claim their daily bonus in a single interaction.
- **SC-002**: 100% of premature claim attempts (without fulfilling either condition) and duplicate daily claims are reliably rejected with clear explanatory messages.
- **SC-003**: Streak progression is 100% accurate across consecutive days, weekly rollover, and broken streaks (resetting to base level after missed days).
- **SC-004**: Users can view their mission progress, claim readiness, and current streak status at any time with up-to-date completion indicators.
- **SC-005**: All existing references and behaviors previously tied to login-triggered bonuses are cleanly transitioned to mission-governed `DiaryBonus` without regressing user balances.

## Assumptions

- Daily reset occurs at midnight UTC (00:00:00 UTC) via the scheduled background process.
- In version 1, buying any player token in the marketplace satisfies the "Buy a token from the market" mission regardless of player, price, or quantity.
- Visiting 1 or 2 player profiles provides partial progress but grants zero claim eligibility until all 3 are visited (unless the token purchase mission is completed).
- A user can only claim one daily bonus reward per UTC calendar day, even if they fulfill both the visit missions and the buy token mission.
- Existing streak reward tier values (e.g. Day 1 through Day 7 rewards) are preserved under the new `DiaryBonus` configuration.
