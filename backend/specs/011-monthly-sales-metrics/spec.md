# Feature Specification: Monthly Sales Metrics

**Feature Branch**: `011-monthly-sales-metrics`

**Created**: 2026-11-18

**Status**: Draft

**Input**: User description: "We are going to develop metrics for our application specifically for the sales done in a month. That includes an admin endpoint, a service, and a repository."

## Clarifications

### Session 2026-11-18

- Q: Which types of market operations should be included in the monthly sales metrics? → A: Completed peer-to-peer sell transactions (`SELL` records from fulfilled market orders).
- Q: How many player assets should be included in the monthly top-selling breakdown? → A: None. Top-selling player breakdown is out of scope; the feature strictly registers how many sales get executed in a month.
- Q: Should monthly sales metrics be persisted in their own dedicated repository table or computed on demand from transaction records? → A: Computed on demand by querying transaction records through the repository for the target month.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - View Executed Sales Count in a Month (Priority: P1)

As an administrator, I want to query the total number of executed sales for a specified calendar month and year so that I can monitor the monthly transaction activity and volume of completed sales in the marketplace.

**Why this priority**: Core MVP delivering direct insight into marketplace transaction activity for any selected month.

**Independent Test**: Can be fully tested by generating completed sell transactions in a target month, querying the metrics for that month, and verifying that the count matches the number of executed sales records.

**Acceptance Scenarios**:

1. **Given** confirmed sell transactions exist within a specified month and year, **When** an administrator requests the sales metrics for that month and year, **Then** the system computes on demand and returns the count of completed sales executed in that month.
2. **Given** no sales transactions occurred within the requested month and year, **When** an administrator queries the sales metrics, **Then** the system returns a count of 0 with a successful status.

---

### User Story 2 - Query Monthly Sales for the Current Active Month (Priority: P2)

As an administrator, I want the system to default to the current calendar month and year when no time parameters are provided, so that I can immediately check month-to-date sales activity without manually inputting dates.

**Why this priority**: Operational convenience for administrators monitoring ongoing month-to-date sales activity.

**Independent Test**: Can be fully tested by requesting metrics without month/year parameters and verifying that the returned count includes all sales executed from day 1 of the current month up to the current moment.

**Acceptance Scenarios**:

1. **Given** sales transactions exist in the current calendar month, **When** an administrator requests monthly metrics without specifying month or year parameters, **Then** the system automatically defaults to the current calendar month and year and returns the month-to-date executed sales count.

---

### Edge Cases

- **Invalid Month or Year Parameters**: What happens when an administrator provides a month outside the range 1-12 or an invalid/negative year? The system must reject the request with a clear, descriptive validation error explaining which parameter is out of bounds.
- **Future Month Queries**: What happens when an administrator queries a month in the future? The system must return 0 executed sales without error.
- **Varying Month Lengths and Leap Years**: How does the system handle months with 28, 29, 30, or 31 days (e.g., February during a leap year)? The calculation period must automatically resolve the exact calendar start (day 1 at 00:00:00) and calendar end (last day of the month at 23:59:59) for that specific year and month.
- **Access Control**: How does the system handle non-administrative or unauthenticated users attempting to query sales metrics? The system must reject the request and deny access immediately.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow administrative users to retrieve monthly sales metrics for a specified calendar month (1-12) and year.
- **FR-002**: If no month and year are specified, the system MUST default the query period to the current calendar month and year.
- **FR-003**: The monthly sales metrics MUST report the total count of executed sales transactions completed within the specified calendar month.
- **FR-004**: The system MUST return 0 executed sales when a queried month has no sales activity.
- **FR-005**: The system MUST validate that the month parameter is an integer between 1 and 12, and the year is a valid four-digit positive integer.
- **FR-006**: The system MUST reject invalid month or year inputs with an appropriate validation error message indicating the invalid parameter.
- **FR-007**: The system MUST restrict access to sales metrics exclusively to authorized administrative users, rejecting unauthorized requests.
- **FR-008**: The metrics calculation MUST count only confirmed, completed peer-to-peer sell transactions (`SELL` records from fulfilled market orders) whose execution timestamp falls strictly within the queried month's boundary (from first day 00:00:00 to last day 23:59:59 inclusive), explicitly excluding buyer-side records to prevent duplicate counting and excluding primary platform token emissions.
- **FR-009**: The monthly sales count MUST be calculated dynamically on demand by querying the transaction repository for records within the month range, avoiding duplicate persistent state or out-of-sync counters.

### Key Entities *(include if feature involves data)*

- **MonthlySalesMetrics**: Aggregated analytical read model representing sales metrics for a specific calendar month and year. Key attributes include: target year, target month, and total executed sales count.
- **SalesTransaction**: Underlying historical entity representing a completed sell transaction (`SELL` type record) containing the execution timestamp, seller identifier, player asset identifier, quantity of tokens transferred, unit price, and total transaction amount.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Administrators can retrieve monthly sales metrics in under 500 milliseconds for months containing up to 100,000 transactions.
- **SC-002**: The executed sales count achieves 100% calculation accuracy when verified against completed `SELL` transaction records for that month.
- **SC-003**: 100% of requests with invalid month or year values receive clear, informative validation feedback without system failure.
- **SC-004**: 100% of unauthorized or unauthenticated requests are prevented from accessing administrative sales metrics.
- **SC-005**: 100% of edge case month lengths (leap years, 28/29/30/31 day months) correctly bound transaction timestamps without omitting or bleeding across monthly boundaries.

## Assumptions

- Sales metrics are derived strictly from confirmed, executed peer-to-peer sell transactions (`SELL` type records from matched market orders).
- The existing transaction persistence records serve as the single source of truth for executed sales, with metrics computed on demand via repository queries.
- Administrative authentication and authorization rely on the existing administrative security mechanisms already established in the application.
- Date and time boundaries for calendar months are evaluated using the application's configured system timezone.
- Asset breakdown rankings and individual player details are explicitly out of scope.
