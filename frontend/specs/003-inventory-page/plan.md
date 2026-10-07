# Implementation Plan: Inventory Page, Protected Layout, and Token Listing Management

**Branch**: `003-inventory-page` | **Date**: 2026-10-07 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/003-inventory-page/spec.md`

## Summary

Implement the `/inventario` page with authentication enforcement through a dedicated `ProtectedLayout` that renders the `cancha.avif` background without `PageWindow`, redirecting unauthenticated visitors to `/login`. The inventory page organizes player token holdings into two selectable tabs: "Tokens Disponibles" (unlisted) and "Tokens en Venta" (listed). Player token cards visually harmonize with `PlayerCard` while displaying strictly player name, token count, price per token, and the action button. A single generic reusable modal (`TokenActionModal`) manages both token listing ("Poner a la venta") and delisting ("Cancelar venta") workflows, performing dynamic total valuation calculations without immediate liquidation. The page and all components are engineered to be fully responsive from 270px width up to desktop viewports, with 100% Spanish UI copy and strict REST API service contracts.

## Technical Context

**Language/Version**: TypeScript 5.8+ with React 19.1+

**Primary Dependencies**: React Router 7 (`react-router-dom`), Tailwind CSS 4, Lucide React (`lucide-react`), Vite 6+, existing `futbolApi` abstraction

**Storage**: Browser session storage via `src/lib/session.ts` for authenticated session token

**Testing**: Static type checking with `pnpm run build` (`tsc -b`), linting with `pnpm lint` (`oxlint`), and end-to-end verification scenarios detailed in [quickstart.md](quickstart.md)

**Target Platform**: Modern web browsers across mobile (from 270px width), tablet, and desktop viewports

**Project Type**: Client-side React Single Page Application (frontend only)

**Performance Goals**: Dynamic total calculations in modal update in <50ms; tab switching and client filtering in <100ms; initial page load in <1s under standard connection

**Constraints**:
- Strictly frontend-only; no backend code modifications (Constitution Principle I).
- Zero use of `any`; complete TypeScript type safety (Constitution Principle II).
- Centralized routes in `src/routes/AppRoutes.tsx` using `React.lazy` (Constitution Principle III).
- Generic reusable modal (`TokenActionModal`) for both listing and delisting actions (Constitution Principle IV).
- Responsive fluid design starting from a 270px minimum width baseline (Constitution Principle V).
- `ProtectedLayout` wraps authenticated routes, provides `cancha.avif` background without `PageWindow`, and redirects unauthenticated users to `/login`.
- `InventoryPage` does not duplicate the background image or top navigation bar.
- All visible UI copy, labels, placeholders, and error messages strictly in Spanish.

**Scale/Scope**:
- 1 new layout: `src/layouts/ProtectedLayout.tsx`
- 1 new page: `src/pages/InventoryPage.tsx`
- 1 generic reusable modal: `src/components/inventory/TokenActionModal.tsx`
- 1 token card component: `src/components/inventory/TokenCard.tsx`
- 1 service module: `src/services/inventoryService.ts`
- 1 types definition: `src/types/inventory.ts`
- Navigation update in `src/components/common/Navbar.tsx` (direct "Inventario" link for authenticated users)
- Centralized route registration in `src/routes/AppRoutes.tsx`

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

The design and technical strategy fully pass all constitutional gates:

1. **Frontend-Only Architecture (Principle I)**: All work is restricted to the frontend repository. REST API contracts are defined and accessed through the existing `futbolApi` client. Backend non-500 error messages are surfaced to the user, while 500 errors are handled generically without exposing internals.
2. **Strict TypeScript & Type Safety (Principle II)**: All data models, component props, and API payloads are strictly typed. Zero usage of `any`.
3. **Centralized Routing & Lazy Loading (Principle III)**: `/inventario` is declared exclusively within `src/routes/AppRoutes.tsx` inside the newly created `ProtectedLayout`, lazy-loaded via `React.lazy`.
4. **Component Modularity, Reuse & Pragmatic Abstraction (Principle IV)**: Fulfills the requirement for a single generic reusable modal (`TokenActionModal`) rather than two duplicate modals. Reuses existing UI components (`Button`, `Loading`, `ServerErrorComponent`, `Navbar`, `Footer`).
5. **Fluid Responsive Layout (Principle V)**: Specifically satisfies and exceeds standard constraints by supporting viewports starting from 270px and above without horizontal overflow.

No constitutional violations exist; no complexity exceptions required.

## Project Structure

### Documentation (this feature)

```text
specs/003-inventory-page/
├── plan.md              # This implementation plan
├── research.md          # Phase 0 research findings and architectural decisions
├── data-model.md        # Phase 1 domain entities, state transitions, and validation rules
├── quickstart.md        # Phase 1 runnable validation scenarios
├── contracts/           # Phase 1 interface contracts
│   └── inventory-api.md # REST endpoints and component props specifications
└── checklists/
    └── requirements.md  # Specification quality checklist
```

### Source Code Layout

```text
src/
├── assets/
│   └── cancha.avif                       # Existing pitch background asset used by ProtectedLayout
├── components/
│   ├── common/
│   │   ├── Navbar.tsx                    # Updated to display "Inventario" link when authenticated
│   │   ├── Footer.tsx                    # Shared layout footer
│   │   └── Loading.tsx                   # Loading indicator
│   └── inventory/
│       ├── TokenCard.tsx                 # Card component (player name, tokens, price, action button)
│       └── TokenActionModal.tsx          # Generic reusable modal for listing and delisting actions
├── layouts/
│   ├── GeneralLayout.tsx                 # Existing general layout (pasto.jpg + PageWindow)
│   └── ProtectedLayout.tsx               # New layout (auth guard, cancha.avif background, no PageWindow)
├── pages/
│   └── InventoryPage.tsx                 # Main inventory page with tabs, search, pagination, cards
├── routes/
│   └── AppRoutes.tsx                     # Centralized routes with lazy-loaded InventoryPage
├── services/
│   └── inventoryService.ts               # REST API calls for inventory, listing, and delisting
└── types/
    └── inventory.ts                      # TypeScript interfaces for holdings, listings, and requests
```

**Structure Decision**: The chosen structure preserves the established repository organization. Reusable inventory-specific components are encapsulated in `src/components/inventory/`, domain types in `src/types/`, service calls in `src/services/`, and route guarding in `src/layouts/ProtectedLayout.tsx`.

## Complexity Tracking

*No constitutional violations; no complexity exceptions required.*
