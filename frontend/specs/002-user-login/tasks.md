---
description: "Task list for User Login and Session Menu"
---

# Tasks: User Login and Session Menu

**Input**: Design documents from `/specs/002-user-login/`

**Prerequisites**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), [contracts/auth-login.md](contracts/auth-login.md), [quickstart.md](quickstart.md)

**Tests**: No automated test tasks are included because the feature specification requests build/lint verification and manual/API validation through `quickstart.md`, but does not request a TDD workflow.

**Organization**: Tasks are grouped by user story so each story can be implemented and validated independently after shared authentication foundations are complete.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel when it touches a different file and has no dependency on incomplete work.
- **[Story]**: Maps a task to the corresponding user story in `spec.md`.
- Every task includes the exact file path it changes or validates.

## Path Conventions

- Single Vite frontend: `src/` at repository root.
- Feature design and validation artifacts: `specs/002-user-login/`.

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Add the requested validation dependency without changing application behavior.

- [x] T001 Add `yup` as a runtime dependency in `package.json` and update `pnpm-lock.yaml` with the workspace package manager.
- [x] T002 [P] Create the requested schema directory at `src/schemas/` and document its login-schema ownership in the implementation structure.

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish typed login credentials, complete session persistence, and shared auth APIs before implementing user-facing stories.

**CRITICAL**: User story work depends on this phase.

- [x] T003 Extend `src/types/auth.ts` with `LoginCredentials`, login form error typing, `expiresAt` in the session type, and the `login` method in `AuthContextType` without using `any`.
- [x] T004 Extend `src/lib/session.ts` so `AuthSession` persists and restores `token`, `expiresAt`, and `user` together under the existing session boundary.
- [x] T005 Add `login(credentials)` to `src/services/authService.ts`, sending trimmed email and unmodified password to `POST /auth/login` through `apiClient` and returning the existing `AuthResponse` shape.
- [x] T006 Extend `src/context/AuthContext.tsx` with the typed login action, session saving, loading state handling, and session clearing behavior required after logout or backend rejection.
- [x] T007 [P] Update `src/services/api.ts` so non-500 client messages remain available through `ApiError` while HTTP 500 and network failures use a generic user-facing message without exposing raw technical details.

**Checkpoint**: Shared authentication/session foundations are ready; user stories can be implemented in priority order.

---

## Phase 3: User Story 1 - Existing User Login (Priority: P1) 🎯 MVP

**Goal**: Let a registered user submit valid credentials, establish a session from `{ token, expiresAt, user }`, and reach Home.

**Independent Test**: Open `/login`, submit valid existing-account credentials, verify `POST /auth/login` receives `{ email, password }`, confirm the session is persisted, and confirm navigation reaches `/`.

### Implementation for User Story 1

- [x] T008 [US1] Create the Yup login schema with required email/password, standard email syntax, password length rules matching registration, and registration-consistent Spanish validation messages in `src/schemas/loginSchema.ts`.
- [x] T009 [US1] Create the typed `LoginForm` component with controlled email/password fields, submit/loading state, inline field errors, top-level server error handling, and `AuthContext.login` integration in `src/components/auth/LoginForm.tsx`.
- [x] T010 [US1] Implement the login screen composition with the `cancha-pelota.png` background, centered form container, and existing registration visual tokens in `src/pages/LoginPage.tsx`.
- [x] T011 [US1] Redirect successful login submissions to `/` from `src/components/auth/LoginForm.tsx` and keep failed submissions on `/login` with their entered values intact.

**Checkpoint**: A user can independently complete the core login journey and reach Home.

---

## Phase 4: User Story 3 - Authenticated Navbar and Logout (Priority: P1)

**Goal**: Show the authenticated user's username and account details in the shared navbar, and allow logout back to the public state.

**Independent Test**: Start with a valid session, open the navbar username control, verify username/email/logout content, select `Cerrar sesión`, then confirm the session is cleared, navigation reaches `/`, and `Iniciar sesión` returns.

### Implementation for User Story 3

- [x] T012 [US3] Update `src/components/common/Navbar.tsx` to consume `useAuth` and conditionally render either the `/login` link or an authenticated user control with a user icon and username.
- [x] T013 [US3] Add the authenticated-only dropdown state and menu content to `src/components/common/Navbar.tsx`, including username, email, and a logout action with a logout icon and accessible control labeling.
- [x] T014 [US3] Wire the logout action in `src/components/common/Navbar.tsx` to clear auth state, close the dropdown, and navigate to `/` while restoring the public navbar link.
- [x] T015 [US3] Ensure authenticated navbar state updates across the shared layout after login/logout through `src/context/AuthContext.tsx` and `src/layouts/MainLayout.tsx` without direct localStorage access in components.

**Checkpoint**: Authenticated identity visibility and logout work independently from the login form presentation.

---

## Phase 5: User Story 2 - Login Validation and Navigation (Priority: P2)

**Goal**: Match registration validation behavior and provide a direct path from login to registration.

**Independent Test**: Submit empty, invalid-email, and invalid-password values on `/login`, confirm field-specific messages and no auth request, then select `aquí` and arrive at `/register`.

### Implementation for User Story 2

- [x] T016 [US2] Integrate `src/schemas/loginSchema.ts` into `src/components/auth/LoginForm.tsx` so validation runs before authentication, trims email for validation/submission, and maps Yup field errors beneath the matching fields.
- [x] T017 [US2] Update `src/components/auth/LoginForm.tsx` to clear a field's validation error when the user edits that field, prevent duplicate submissions while loading, and preserve server errors according to registration feedback behavior.
- [x] T018 [US2] Add the exact `¿No tienes cuenta? Regístrate aquí` prompt below the login button in `src/components/auth/LoginForm.tsx`, making only `aquí` a link to `/register`.
- [x] T019 [P] [US2] Migrate registration validation to the shared Yup schema boundary in `src/components/auth/RegisterForm.tsx` and any additional schema file under `src/schemas/` needed to preserve the existing registration rules without changing its user-facing behavior.

**Checkpoint**: Login validation, feedback, and authentication-page navigation are independently usable and consistent with registration.

---

## Phase 6: User Story 4 - Consistent Authentication Presentation (Priority: P3)

**Goal**: Ensure the completed login flow visually matches registration and remains usable from mobile through desktop.

**Independent Test**: Compare `/login` and `/register` at 320px, 768px, 1024px, and wide desktop widths; verify background, navbar, card, controls, feedback, dropdown, and links have no overlap or horizontal scrolling.

### Implementation for User Story 4

- [x] T020 [US4] Align `src/pages/LoginPage.tsx` and `src/components/auth/LoginForm.tsx` with the registration page's card spacing, background positioning, colors, labels, button treatment, and responsive width constraints.
- [x] T021 [US4] Refine shared responsive styles in `src/index.css` only where needed so login fields, validation messages, navbar controls, and account dropdown remain readable at 320px and wider viewports.
- [x] T022 [US4] Verify `src/routes/AppRoutes.tsx` keeps `/login` lazy-loaded and wrapped by `MainLayout` without adding distributed route declarations.
- [x] T023 [US4] Verify `src/components/common/Navbar.tsx` account dropdown positioning, focus behavior, and narrow-screen layout do not clip or overlap the navbar or page content.

**Checkpoint**: Login and registration present one coherent, responsive authentication experience.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Validate the complete feature against the design artifacts and project quality gates.

- [x] T024 [P] Review `src/services/authService.ts`, `src/context/AuthContext.tsx`, and `src/lib/session.ts` against `specs/002-user-login/contracts/auth-login.md` and `specs/002-user-login/data-model.md`.
- [x] T025 [P] Run `pnpm build` and resolve TypeScript or bundler errors across the changed files.
- [x] T026 [P] Run `pnpm lint` and resolve lint warnings/errors across the changed files without modifying unrelated user changes.
- [ ] T027 Run every manual scenario in `specs/002-user-login/quickstart.md`, including login failure, server/network failure, navbar menu, logout, and responsive viewport checks.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies; T001 must complete before implementation can import Yup.
- **Foundational (Phase 2)**: Depends on Setup; blocks all user stories.
- **User Story 1 (Phase 3)**: Depends on Foundational; delivers the MVP login journey.
- **User Story 3 (Phase 4)**: Depends on Foundational and the session updates from US1; can begin after T006 but should integrate after T011.
- **User Story 2 (Phase 5)**: Depends on Foundational and the login form from US1; T019 also touches registration and should be coordinated with any existing registration changes.
- **User Story 4 (Phase 6)**: Depends on the completed login form and navbar behavior from US1, US2, and US3.
- **Polish (Phase 7)**: Depends on all desired user stories being implemented.

### User Story Dependencies

- **US1 (P1)**: Foundational only; MVP candidate.
- **US3 (P1)**: Foundational plus the authenticated session produced by US1; logout can be implemented in parallel with visual login work after T006.
- **US2 (P2)**: Foundational plus LoginForm structure from US1; validation schema can be created with US1, while registration migration should be coordinated.
- **US4 (P3)**: Depends on the final UI from US1-US3; no independent backend dependency.

### Parallel Opportunities

- T002 and T007 can run in parallel after setup context is available.
- T003, T004, and T005 can run in parallel within the foundational phase; T006 follows their contracts.
- T012 and T008 can run in parallel after foundational auth types are available, provided each developer avoids overlapping edits to `AuthContext`.
- T020 and T022 can run in parallel after the core login page exists.
- T024, T025, and T026 can run in parallel during polish; T027 follows the build/lint fixes.

---

## Parallel Example: User Story 1

```text
Task: "T008 [US1] Create the Yup login schema in src/schemas/loginSchema.ts"
Task: "T010 [US1] Implement the LoginPage background and centered composition in src/pages/LoginPage.tsx"
```

These tasks can proceed in parallel after the foundational auth types and session contracts exist. T009 integrates both and depends on the schema and auth context.

## Parallel Example: User Story 3

```text
Task: "T012 [US3] Add authenticated username control in src/components/common/Navbar.tsx"
Task: "T015 [US3] Verify shared auth state propagation in src/context/AuthContext.tsx and src/layouts/MainLayout.tsx"
```

Coordinate edits to `Navbar.tsx` and `AuthContext.tsx` before merging; both tasks depend on the foundational session contract.

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete T001-T007: dependency, schema location, typed auth/session/API foundation.
2. Complete T008-T011: Yup-backed login form and Home redirect.
3. Run the independent US1 scenario from `quickstart.md`.
4. Stop and validate the core login flow before adding navbar menu and visual polish.

### Incremental Delivery

1. Add US1 for a working login MVP.
2. Add US3 for authenticated identity and logout.
3. Add US2 for schema-driven validation and registration navigation consistency.
4. Add US4 for responsive and visual parity.
5. Run the complete quickstart and project quality gates.

### Parallel Team Strategy

1. Complete Setup and Foundational phases together.
2. Assign login form/schema work to one contributor and navbar/session UI work to another after T006.
3. Coordinate the shared `AuthContext` and `Navbar` edits before integration.
4. Finish validation migration and responsive polish after the core stories are demonstrable.

## Notes

- Every implementation task uses the required `- [ ] T###` checklist format.
- `[P]` appears only on tasks identified as independently parallelizable.
- `[US1]` through `[US4]` map directly to the four stories in `spec.md`.
- No backend code is created or modified; all API work remains in the frontend service boundary.
- T027 remains unchecked because complete manual login, failure, logout, and responsive validation requires a running backend and test account that were not available in this session.
