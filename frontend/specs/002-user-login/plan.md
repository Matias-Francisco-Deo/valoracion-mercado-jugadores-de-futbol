# Implementation Plan: User Login and Session Menu

**Branch**: `002-user-login` | **Date**: 2026-09-22 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/002-user-login/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command; its definition describes the execution workflow.

## Summary

Implement the `/login` flow and authenticated account menu while preserving the registration page's visual language and validation behavior. Extend the existing authentication service/context/session boundaries, add a shared Yup-backed login schema under `src/schemas`, and update the shared navbar to expose the authenticated user's identity and logout action.

## Technical Context

<!--
  ACTION REQUIRED: Replace the content in this section with the technical details
  for the project. The structure here is presented in advisory capacity to guide
  the iteration process.
-->

**Language/Version**: TypeScript 6.0 with React 19.2

**Primary Dependencies**: Vite 8, React Router 7, Tailwind CSS 4, existing `apiClient`, Yup (new runtime dependency)

**Storage**: Browser `localStorage` through `src/lib/session.ts`; token, `expiresAt`, and user profile are persisted as one auth session.

**Testing**: TypeScript build (`pnpm build`), Oxlint (`pnpm lint`), manual/API scenarios in [quickstart.md](quickstart.md), and focused component/schema tests if the repository's test runner is introduced during implementation.

**Target Platform**: Modern browsers, responsive from 320px through desktop widths; backend URL supplied through `VITE_API_BASE_URL`.

**Project Type**: React/Vite frontend web application

**Performance Goals**: Successful login redirects to Home in under 1 second under normal client conditions; form interactions remain immediate and duplicate submissions are prevented.

**Constraints**: Reuse existing layout, field, button, API, context, and session abstractions; no direct storage access from components; no hardcoded backend URL; non-500 client messages remain visible and server failures remain generic; no proactive logout solely from `expiresAt`.

**Scale/Scope**: One login route, one shared navbar account menu, one login schema, and extensions to the existing auth/session model. Password recovery, account editing, and new authorization rules are out of scope.

## Constitution Check

_GATE: Must pass before Phase 0 research. Re-check after Phase 1 design._

The design passes the constitution gates:

- **Frontend-only boundary**: changes stay in the frontend; backend code is untouched.
- **Type safety**: all new contracts use explicit TypeScript types; no `any` is planned.
- **Centralized routing**: `/login` remains declared in `src/routes/AppRoutes.tsx`; pages continue to use `React.lazy`.
- **Reuse and SRP**: login reuses `Field`, `Button`, `AlertBanner`, `AuthContext`, `apiClient`, session helpers, and the shared layout; the navbar account menu remains one focused shared responsibility.
- **Responsive behavior**: login and the account menu inherit fluid layout constraints and are validated at 320px, tablet, and desktop widths.
- **Backend handling**: API calls use `apiClient` and `VITE_API_BASE_URL`; non-500 messages remain client-visible and 500/network errors remain generic.

No gate violations require a complexity exception.

## Project Structure

### Documentation (this feature)

```text
specs/[###-feature]/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

<!--
  ACTION REQUIRED: Replace the placeholder tree below with the concrete layout
  for this feature. Delete unused options and expand the chosen structure with
  real paths (e.g., apps/admin, packages/something). The delivered plan must
  not include Option labels.
-->

```text
src/
├── assets/
│   └── cancha-pelota.png
├── components/
│   ├── auth/
│   │   ├── LoginForm.tsx          # new
│   │   └── RegisterForm.tsx       # migrate shared validation to schema
│   ├── common/
│   │   └── Navbar.tsx             # authenticated menu and logout
│   └── ui/
│       ├── AlertBanner.tsx
│       ├── Button.tsx
│       ├── Field.tsx
│       └── Input.tsx
├── context/
│   └── AuthContext.tsx             # login and session expiry metadata
├── lib/
│   └── session.ts                  # AuthSession persistence
├── pages/
│   ├── LoginPage.tsx               # login background and form composition
│   └── RegisterPage.tsx
├── routes/
│   └── AppRoutes.tsx
├── schemas/
│   └── loginSchema.ts              # new Yup schema
├── services/
│   ├── api.ts
│   └── authService.ts              # POST /auth/login
└── types/
  └── auth.ts                     # LoginCredentials and context types

specs/002-user-login/
├── contracts/
│   └── auth-login.md
├── data-model.md
├── plan.md
├── quickstart.md
├── research.md
└── spec.md
```

**Structure Decision**: Keep the existing single Vite frontend structure. Add only the feature-specific `src/schemas` folder requested for Yup validation, and extend existing authentication/layout modules rather than introducing parallel services or storage.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

| Violation | Why Needed | Simpler Alternative Rejected Because                               |
| --------- | ---------- | ------------------------------------------------------------------ |
| None      | N/A        | The feature fits the existing frontend structure and abstractions. |
