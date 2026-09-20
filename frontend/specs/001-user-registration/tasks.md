# Tasks: User Registration Page & Layout

**Input**: Design documents from `specs/001-user-registration/` (`plan.md`, `spec.md`, `research.md`, `data-model.md`, `contracts/`, `quickstart.md`)  
**Prerequisites**: `plan.md` (required), `spec.md` (required for user stories), `research.md`, `data-model.md`, `contracts/`  
**Organization**: Tasks are grouped by user story to enable independent implementation, testing, and incremental delivery.

---

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (`[US1]`, `[US2]`, `[US3]`, `[US4]`)
- Every task includes the exact file path

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Project initialization, styling configuration, design tokens, and shared TypeScript types.

- [X] T001 Configure Tailwind CSS v4 in `vite.config.ts` by adding `@tailwindcss/vite` plugin
- [X] T002 [P] Configure design tokens (`--color-brand-orange`, `--color-pitch-green`, `--color-card-gray`) and reset root styling in `src/index.css`
- [X] T003 [P] Define reusable shared CSS utility classes (`.form-label`, `.form-input-base`, `.form-error-text`, `.btn-primary`, `.card-container`) in `src/index.css`
- [X] T004 [P] Create shared TypeScript definitions for user profiles, registration credentials, and authentication sessions in `src/types/auth.ts`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Reusable UI components, HTTP communication service, Auth Context with `localStorage` isolation, layout structure, and centralized routing.

**⚠️ CRITICAL**: No user story work can begin until this foundational phase is complete.

- [X] T005 [P] Implement reusable generic `Button` component with variants, loading state, native props, and `twMerge` in `src/components/ui/Button.tsx`
- [X] T006 [P] Implement reusable generic `Input` component with label, inline error support, accessibility IDs, native props, and `twMerge` in `src/components/ui/Input.tsx`
- [X] T007 [P] Implement reusable generic `Card` container component with native props and `twMerge` in `src/components/ui/Card.tsx`
- [X] T008 [P] Implement reusable `AlertBanner` component for top-level sanitized alert notifications in `src/components/ui/AlertBanner.tsx`
- [X] T009 Implement base API fetch client and sanitized response/error handling in `src/services/api.ts`
- [X] T010 Implement authentication service for backend `POST /auth/register` operations and sanitized error mapping in `src/services/authService.ts`
- [X] T011 Implement centralized `AuthContext` and `AuthProvider` encapsulating all `localStorage` reads/writes (`auth_token`, `auth_user`) in `src/context/AuthContext.tsx`
- [X] T012 [P] Implement custom `useAuth` hook for safe context consumption without direct `localStorage` access in `src/hooks/useAuth.ts`
- [X] T013 Implement top navigation bar `Navbar` with orange styling (`#FF9500`), brand title "Overcode", and login link in `src/components/common/Navbar.tsx`
- [X] T014 Implement shared layout `MainLayout` rendering `Navbar` and content area configured to prevent vertical scrollbars in `src/layouts/MainLayout.tsx`
- [X] T015 Implement centralized React Router routing structure with `<MainLayout />` in `src/routes/AppRoutes.tsx`
- [X] T016 Mount `BrowserRouter` and `AuthProvider` wrapping `AppRoutes` in `src/main.tsx`

**Checkpoint**: Foundation ready — user story implementation can now proceed independently.

---

## Phase 3: User Story 1 - New User Registration and Home Redirection (Priority: P1) 🎯 MVP

**Goal**: Enable visitors to register with email, username, and password, authenticate via AuthContext, store session in `localStorage`, and redirect to the Home page (`/`).

**Independent Test**: Navigate to `/register`, enter valid registration details into the email, username, and password fields, submit the form, and verify that the session is stored in AuthContext and the user is redirected to the Home page (`/`).

### Implementation for User Story 1

- [X] T017 [P] [US1] Create placeholder Home page component (`/`) with a call-to-action link to `/register` in `src/pages/HomePage.tsx`
- [X] T018 [US1] Implement registration form component `RegisterForm` managing input state, submit trigger, and calling `useAuth().register` in `src/components/auth/RegisterForm.tsx`
- [X] T019 [US1] Implement registration card component `RegisterCard` wrapping `RegisterForm` with title "Registrarse" in `src/components/auth/RegisterCard.tsx`
- [X] T020 [US1] Implement registration page `RegisterPage` assembling `RegisterCard` and handling post-registration navigation to `/` in `src/pages/RegisterPage.tsx`
- [X] T021 [US1] Register `/register` and `/` index routes in `src/routes/AppRoutes.tsx`

**Checkpoint**: User Story 1 is fully functional as the MVP — user registration, AuthContext state update, and redirection to Home work end-to-end.

---

## Phase 4: User Story 2 - Form Input Validation & User Feedback (Priority: P2)

**Goal**: Provide prompt inline validation feedback beneath individual fields for missing/invalid email, short username, and short password; display top alert banner for server/network/conflict errors without technical leakage.

**Independent Test**: Attempt submission with blank fields, invalid email format, short username (<3 chars or invalid symbols), or short password (<6 chars) to verify inline errors appear under each input; simulate 409 conflict or network drop to verify sanitized top alert banner appears.

### Implementation for User Story 2

- [X] T022 [US2] Implement client-side validation logic (email regex, username >= 3 alphanumeric/underscore, password >= 6 chars) and bind inline error messages in `src/components/auth/RegisterForm.tsx`
- [X] T023 [US2] Integrate `AlertBanner` into `RegisterCard` to display top-of-card sanitized server errors and 409 conflict alerts in `src/components/auth/RegisterCard.tsx`
- [X] T024 [US2] Add submission debouncing and loading spinner state to `Button` during active registration requests in `src/components/auth/RegisterForm.tsx`

**Checkpoint**: User Stories 1 and 2 work seamlessly — validation prevents bad data and displays clear inline/top-of-card feedback.

---

## Phase 5: User Story 3 - Navigation to Login for Existing Users (Priority: P3)

**Goal**: Provide an accessible link to the login page from the registration form prompt "¿Ya tienes cuenta? Inicia sesión aqui" where the word "aqui" links to `/login`.

**Independent Test**: Locate the informational text on the registration form, click the link "aqui", and confirm the browser route transitions to `/login` without a full page reload.

### Implementation for User Story 3

- [X] T025 [P] [US3] Create placeholder Login page component (`/login`) informing user that login will be available in a future phase in `src/pages/LoginPage.tsx`
- [X] T026 [US3] Render the prompt "¿Ya tienes cuenta? Inicia sesión aqui" with a React Router `<Link>` on "aqui" pointing to `/login` in `src/components/auth/RegisterForm.tsx`
- [X] T027 [US3] Register `/login` route inside `<MainLayout />` in `src/routes/AppRoutes.tsx`

**Checkpoint**: User Story 3 transitions users between registration and login placeholder views cleanly.

---

## Phase 6: User Story 4 - Visual Layout and Responsive Presentation (Priority: P4)

**Goal**: Deliver cohesive visual branding: orange top navigation bar (`#FF9500`), green background (`#096638`) with `cancha-pelota.png` overlay, centered gray form card (`#A8A8A8`), adapting from 320px to 2560px with strictly ZERO vertical scrollbar from layout rendering.

**Independent Test**: Open the registration page across viewport sizes (320px to 2560px); verify the navbar is `#FF9500`, background is `#096638` with `cancha-pelota.png`, card is `#A8A8A8` centered, and NO vertical scrollbar is introduced by the layout.

### Implementation for User Story 4

- [X] T028 [US4] Configure background image overlay `cancha-pelota.png` layered over solid green background `#096638` in `src/pages/RegisterPage.tsx`
- [X] T029 [US4] Enforce dynamic viewport height constraints (`h-[calc(100dvh-4rem)]` with `overflow-hidden`) in `src/pages/RegisterPage.tsx` and `src/layouts/MainLayout.tsx` to guarantee zero vertical scrollbars
- [X] T030 [US4] Apply `#A8A8A8` card background styling and responsive padding across mobile (320px+) to desktop viewports in `src/components/auth/RegisterCard.tsx`

**Checkpoint**: Visual design tokens and zero-vertical-scrollbar requirements are completely satisfied across all viewports.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Cleanup, static type safety verification, linting compliance, and quickstart end-to-end execution.

- [X] T031 [P] Remove obsolete Vite starter boilerplate files (`src/App.tsx`, `src/App.css`) and clean unused demo assets
- [X] T032 Verify strict TypeScript compilation with zero errors (`tsc -b`) across the entire repository
- [X] T033 Verify static analysis and linting rules with zero warnings/errors (`pnpm lint` / `oxlint`)
- [X] T034 Execute end-to-end verification scenarios per `specs/001-user-registration/quickstart.md`

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately.
- **Foundational (Phase 2)**: Depends on Setup completion — BLOCKS all user stories.
- **User Stories (Phase 3+)**: All depend on Foundational phase completion.
  - User Story 1 (P1): MVP gateway — no dependencies on subsequent stories.
  - User Story 2 (P2): Depends on US1 form components; enhances validation and error presentation.
  - User Story 3 (P3): Can be implemented in parallel with US2; adds login link and placeholder route.
  - User Story 4 (P4): Refines styling and viewport constraints on top of US1 layout and page.
- **Polish (Final Phase)**: Depends on all user stories being completed.

### Parallel Opportunities

- **Phase 1 (Setup)**: T002, T003, T004 can run in parallel.
- **Phase 2 (Foundational)**:
  - UI primitives T005 (`Button`), T006 (`Input`), T007 (`Card`), T008 (`AlertBanner`) can run in parallel.
  - T012 (`useAuth`) can run in parallel once T011 (`AuthContext`) interface is defined.
- **Phase 3 (US1)**: T017 (`HomePage.tsx`) can run in parallel with T018 (`RegisterForm.tsx`).
- **Phase 5 (US3)**: T025 (`LoginPage.tsx`) can run in parallel with US2 tasks.
- **Phase 7 (Polish)**: T031 can run in parallel before final build checks.

---

## Parallel Example: Foundational Phase

```bash
# Launch generic UI primitives in parallel:
Task: "Implement reusable generic Button component in src/components/ui/Button.tsx"
Task: "Implement reusable generic Input component in src/components/ui/Input.tsx"
Task: "Implement reusable generic Card container in src/components/ui/Card.tsx"
Task: "Implement reusable AlertBanner component in src/components/ui/AlertBanner.tsx"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)
1. Complete Phase 1: Setup (Tailwind v4, CSS tokens, TypeScript types).
2. Complete Phase 2: Foundational (UI primitives, API service, AuthContext, MainLayout, AppRoutes).
3. Complete Phase 3: User Story 1 (RegisterForm, RegisterCard, RegisterPage, HomePage).
4. **STOP and VALIDATE**: Test User Story 1 independently (registration + session persistence + redirection to Home).

### Incremental Delivery
1. **Foundation Ready**: Setup + Foundational complete.
2. **Increment 1 (MVP)**: User Story 1 complete → register user and redirect.
3. **Increment 2**: User Story 2 complete → inline validation & top alert banner.
4. **Increment 3**: User Story 3 complete → login navigation link.
5. **Increment 4**: User Story 4 complete → full visual polish & zero-scrollbar verification.
6. **Increment 5**: Polish complete → clean boilerplate, `tsc -b`, and `oxlint`.
