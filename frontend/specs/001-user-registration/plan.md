# Implementation Plan: User Registration Page & Layout

**Branch**: `001-user-registration` | **Date**: 2026-09-19 | **Spec**: [specs/001-user-registration/spec.md](specs/001-user-registration/spec.md)

**Input**: Feature specification from `specs/001-user-registration/spec.md`

## Summary

Deliver a responsive user registration experience and shared application layout for the football player market valuation platform. The layout features an orange (`#FF9500`) navigation bar with the "Overcode" brand and a login link. The registration view is hosted at `/register`, framed by a green background (`#096638`) overlaid with `cancha-pelota.png`, presenting a centered gray (`#A8A8A8`) registration card with labeled inputs for email, username, and password, inline validation error feedback, and a submit button. The registration page is architected to guarantee zero vertical scrollbars when rendered inside the layout. Authentication state and session management (JWT token and user profile) are centralized in a React Auth Context, which strictly encapsulates all `localStorage` access so components never interact with `localStorage` directly.

## Technical Context

**Language/Version**: TypeScript 5.x / 6.0 (`~6.0.2`), Node.js (Vite 6/8, React 19)

**Primary Dependencies**: React 19, React Router DOM v7 (`react-router-dom`), Tailwind CSS v4 (`tailwindcss`, `@tailwindcss/vite`), `tailwind-merge`

**Storage**: Client-side `localStorage` (`auth_token`, `auth_user`) exclusively encapsulated within `AuthContext`

**Testing & Verification**: Static type verification (`tsc -b`), linting and static analysis (`oxlint`), and cross-viewport responsive verification (320px to 2560px)

**Target Platform**: Modern Web Browsers (Chrome, Firefox, Safari, Edge; Mobile, Tablet, Desktop)

**Project Type**: Single Page Application (SPA) Web Frontend

**Performance Goals**: Sub-second form submission response and page redirection (< 1s), zero unwanted layout scrollbars, zero layout shift (CLS < 0.1)

**Constraints**:
- Strict responsive compliance across viewports from 320px to 2560px.
- The registration page MUST NOT display a vertical scrollbar as a result of being rendered inside the layout.
- Individual components MUST NOT access `localStorage` directly; all authentication state and actions must flow through `AuthContext`.
- No backend code or database access in this frontend repository (Principle I).
- Zero `any` types permitted (Principle II).
- Raw server/database errors or stack traces must never be exposed to users (Principle I).
- Centralized routes defined in a single file `src/routes/AppRoutes.tsx` (Principle III).

**Scale/Scope**: 3 application routes (`/`, `/register`, `/login` placeholder), 1 shared layout (`MainLayout`), 1 Auth Context, 4 reusable UI primitives (`Button`, `Input`, `Card`, `AlertBanner`), and 2 auth feature components.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Requirement | Status | Verification / Justification |
|---|---|---|---|
| **I. Frontend-Only Architecture & Scope Isolation** | Strictly client-side; no backend code; sanitize all API errors; no stack traces exposed. | **PASS** | Registration API is abstracted in `src/services/authService.ts`. Errors are mapped to friendly user notices; raw diagnostics restricted to developer console. |
| **II. Strict TypeScript & Type Safety Gate** | React + Tailwind + TypeScript; zero `any` types allowed; pass `tsc -b` and `oxlint`. | **PASS** | All entities, credentials, component props, and context states are fully typed in `src/types/auth.ts` and component interfaces. No `any` used. |
| **III. Centralized Routing Architecture** | All routes declared in a single dedicated routes file (`src/routes/AppRoutes.tsx`). No scattered `<Route>` tags. | **PASS** | Single routing source of truth established in `src/routes/AppRoutes.tsx`. |
| **IV. Component Modularity & Single Responsibility** | Granular, reusable components adhering to SRP; clean naming; no monster files. | **PASS** | Decomposed into reusable UI primitives (`Button`, `Input`, `Card`, `AlertBanner`), layout (`Navbar`, `MainLayout`), and auth components (`RegisterCard`, `RegisterForm`). |
| **V. Responsive & Adaptive Fluid Layouts** | Fluid layouts adapting to 320px–2560px; images scale proportionally; zero layout scrollbars on register view. | **PASS** | Dynamic viewport sizing (`h-[calc(100dvh-4rem)]` with `overflow-hidden`), fluid containers, and responsive Tailwind breakpoints. |

## Project Structure

### Documentation (this feature)

```text
specs/001-user-registration/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
├── contracts/           # Phase 1 output (/speckit-plan command)
│   ├── auth-api.md      # Backend HTTP contract (POST /auth/register)
│   └── ui-contracts.md  # Reusable UI component, layout & context contracts
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

### Source Code (repository root)

```text
src/
├── assets/
│   └── cancha-pelota.png           # Soccer field background asset
├── components/
│   ├── auth/
│   │   ├── RegisterCard.tsx        # Centered form card container
│   │   └── RegisterForm.tsx        # Registration form inputs and submit trigger
│   ├── common/
│   │   └── Navbar.tsx              # Top navigation bar (#FF9500)
│   └── ui/
│       ├── AlertBanner.tsx         # User-facing error and conflict alert banner
│       ├── Button.tsx              # Generic reusable button with native props + twMerge
│       ├── Card.tsx                # Generic reusable card container with native props + twMerge
│       └── Input.tsx               # Generic reusable labeled input with native props + twMerge
├── context/
│   └── AuthContext.tsx             # React Auth Context managing state and localStorage
├── hooks/
│   └── useAuth.ts                  # Custom hook for accessing AuthContext
├── layouts/
│   └── MainLayout.tsx              # Shared layout with Navbar and Outlet
├── pages/
│   ├── HomePage.tsx                # Primary index view ('/') placeholder
│   ├── LoginPage.tsx               # Login page placeholder ('/login')
│   └── RegisterPage.tsx            # Full-viewport registration view ('/register')
├── routes/
│   └── AppRoutes.tsx               # Centralized routing configuration
├── services/
│   ├── api.ts                      # Base fetch wrapper and error response handling
│   └── authService.ts              # Authentication API operations (POST /auth/register)
├── types/
│   └── auth.ts                     # Shared TypeScript types for auth, user, and credentials
├── index.css                       # Tailwind v4 import, theme tokens, and reusable styles
├── main.tsx                        # Application root entry point
└── vite-env.d.ts                   # Environment type declarations
```

**Structure Decision**: Single project layout matching frontend repository root. Code types are cleanly organized into `pages/`, `components/`, `hooks/`, `routes/`, `layouts/`, `types/`, `services/`, and `context/`. Reusable UI components provide sensible default styling and accept `className` and native props using `tailwind-merge` (`twMerge`).

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

*No violations detected. All design specifications strictly comply with the Constitution.*
