# Tasks: Inventory Page, Protected Layout, and Token Listing Management

**Feature**: Inventory Page, Protected Layout, and Token Listing Management  
**Branch**: `003-inventory-page`  
**Specification**: [spec.md](spec.md)  
**Implementation Plan**: [plan.md](plan.md)  
**Target Date**: 2026-10-07

---

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Define TypeScript domain models and REST API client abstractions for inventory and token transactions.

- [x] T001 [P] Create inventory domain types in `src/types/inventory.ts` (`PlayerTokenHolding`, `TokenSaleListing`, `UserInventoryResponse`, `ListTokensRequest`, `CancelListingRequest`, `InventoryTab`, `TokenActionModalState`)
- [x] T002 [P] Create inventory API service in `src/services/inventoryService.ts` implementing `getUserInventory`, `listTokensForSale`, and `cancelTokenListing` using `futbolApi`

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Core routing, authentication protection, layout, generic modal, and reusable card components.

> **CRITICAL**: No user story implementation can begin until this foundational phase is complete.

- [x] T003 Implement `ProtectedLayout` in `src/layouts/ProtectedLayout.tsx` with authentication verification (`useAuth().isAuthenticated` redirecting to `/login`), `Navbar`, full-viewport `cancha.avif` background without `PageWindow`, `<Outlet />`, and `Footer`
- [x] T004 Update `src/components/common/Navbar.tsx` to display an "Inventario" navigation link beside "Catálogo" when a user is authenticated
- [x] T005 Register `/inventario` route in `src/routes/AppRoutes.tsx` under `ProtectedLayout` using lazy loading (`React.lazy`)
- [x] T006 Implement generic reusable modal `TokenActionModal` in `src/components/inventory/TokenActionModal.tsx` supporting both listing and delisting actions with whole-integer quantity bounds `[1, maxTokens]`, dynamic total valuation calculation, inline Spanish errors, and busy state
- [x] T007 Implement `TokenCard` in `src/components/inventory/TokenCard.tsx` matching `PlayerCard` visual styling (rounded-2xl, border, card grays, brand orange button) displaying exclusively player name, token count, price per token, and action button

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Two-Section Inventory Overview & Protected Access (Priority: P1) 🎯 MVP

**Goal**: Authenticated user accesses `/inventario` via `ProtectedLayout` (unauthenticated visitors redirect to `/login`), displaying header, player/token summary counters, and selectable tabs ("Tokens Disponibles" and "Tokens en Venta") with player token cards.

**Independent Test**: Navigate to `/inventario` without an active session and verify immediate redirection to `/login`. Sign in, navigate to `/inventario`, and verify the `cancha.avif` background renders without `PageWindow`, tabs toggle between "Tokens Disponibles" and "Tokens en Venta", and cards display exclusively player name, token count, unit price, and action button.

- [x] T008 [US1] Create `InventoryPage` shell in `src/pages/InventoryPage.tsx` with header ("COLECCIÓN PERSONAL", "Inventario", subtitle, and player/token counters)
- [x] T009 [US1] Implement selectable tab navigation ("Tokens Disponibles" and "Tokens en Venta") in `src/pages/InventoryPage.tsx`
- [x] T010 [US1] Integrate `inventoryService.getUserInventory()` in `src/pages/InventoryPage.tsx` with `Loading` indicator, `ServerErrorComponent`, and localized empty state messages
- [x] T011 [US1] Render `TokenCard` grid in `src/pages/InventoryPage.tsx` displaying available tokens in the "Tokens Disponibles" tab with "Poner a la venta" button and listed tokens in "Tokens en Venta" tab with "Cancelar venta" button

**Checkpoint**: User Story 1 is fully functional and delivers an independently testable MVP.

---

## Phase 4: User Story 2 - Listing Tokens for Sale via Generic Modal (Priority: P1)

**Goal**: User clicks "Poner a la venta" on an unlisted token card to open the generic `TokenActionModal`, selects a quantity, views real-time total valuation, and confirms listing without triggering an immediate cash transaction.

**Independent Test**: Click "Poner a la venta" on an unlisted token card, verify that `TokenActionModal` opens with player data and fixed market price, adjust quantity, verify real-time total calculation, confirm listing, and verify that tokens move from "Tokens Disponibles" to "Tokens en Venta" (removing the card from "Tokens Disponibles" if unlisted balance reaches 0).

- [x] T012 [US2] Wire "Poner a la venta" button in `src/pages/InventoryPage.tsx` to open `TokenActionModal` with actionType `'list'`, player name, unit price, and available unlisted token count
- [x] T013 [US2] Implement listing submission handler in `src/pages/InventoryPage.tsx` calling `inventoryService.listTokensForSale(payload)` without immediate cash liquidation
- [x] T014 [US2] Update local inventory state upon successful listing in `src/pages/InventoryPage.tsx`, transferring tokens to "Tokens en Venta" and removing player card from "Tokens Disponibles" if remaining unlisted balance reaches 0

**Checkpoint**: User Stories 1 AND 2 work together seamlessly.

---

## Phase 5: User Story 3 - Delisting Tokens via Generic Modal (Priority: P1)

**Goal**: User clicks "Cancelar venta" on a listed token card to open the same generic `TokenActionModal`, selects a quantity to delist, and confirms cancellation, returning tokens to available inventory.

**Independent Test**: Click "Cancelar venta" on a listed token card, verify that `TokenActionModal` opens with currently listed tokens, select quantity, verify real-time total calculation, confirm cancellation, and verify that tokens return to "Tokens Disponibles" (removing the card from "Tokens en Venta" if listed balance reaches 0).

- [x] T015 [US3] Wire "Cancelar venta" button in `src/pages/InventoryPage.tsx` to open `TokenActionModal` with actionType `'delist'`, player name, unit price, and currently listed token count
- [x] T016 [US3] Implement delisting submission handler in `src/pages/InventoryPage.tsx` calling `inventoryService.cancelTokenListing(payload)`
- [x] T017 [US3] Update local inventory state upon successful delisting in `src/pages/InventoryPage.tsx`, returning tokens to "Tokens Disponibles" and removing player card from "Tokens en Venta" if listed balance reaches 0

**Checkpoint**: Token listing and delisting workflows operate symmetrically through the generic modal.

---

## Phase 6: User Story 4 - Inventory Search, Filtering & Section Pagination (Priority: P2)

**Goal**: User filters player cards in the active tab by name in real time and navigates across pages using pagination controls.

**Independent Test**: Type a player name into the search bar, verify matching cards update within 100ms, and verify pagination buttons toggle pages when items exceed the page limit.

- [x] T018 [US4] Implement search input in `src/pages/InventoryPage.tsx` with placeholder "Buscar jugadores en tu inventario" that filters cards by player name and updates matching count
- [x] T019 [US4] Integrate `Pagination` component in `src/pages/InventoryPage.tsx` for each tab when the number of cards exceeds the configured page size

**Checkpoint**: Search filtering and pagination work across both inventory tabs.

---

## Phase 7: User Story 5 - Ultra-Responsive Layout (from 270px) & Visual Harmony (Priority: P3)

**Goal**: Ensure fluid layout and accessibility down to 270px screen width without horizontal overflow, and audit 100% Spanish localization.

**Independent Test**: Resize browser viewport down to 270px; verify that header, tabs, cards, and modal render without clipping or horizontal scrollbars; verify 100% of visible UI copy is in Spanish.

- [x] T020 [US5] Apply ultra-responsive CSS and Tailwind classes in `src/pages/InventoryPage.tsx`, `src/components/inventory/TokenCard.tsx`, and `src/components/inventory/TokenActionModal.tsx` ensuring support for viewports down to 270px width without horizontal overflow
- [x] T021 [US5] Audit all visible UI text across `src/pages/InventoryPage.tsx`, `src/components/inventory/TokenActionModal.tsx`, `src/components/inventory/TokenCard.tsx`, and `src/layouts/ProtectedLayout.tsx` ensuring 100% Spanish localization

**Checkpoint**: Full responsive compliance from 270px to desktop viewports with complete Spanish copy.

---

## Phase 8: Polish & Cross-Cutting Concerns

**Purpose**: Quality gate verification, static analysis, and end-to-end scenario validation.

- [x] T022 Run TypeScript compilation gate (`tsc -b`) to verify zero type errors and zero `any` usage
- [x] T023 Run linting gate (`pnpm lint` / `oxlint`) to verify zero errors; pre-existing warnings remain in unrelated files
- [ ] T024 Execute manual validation checklist from `specs/003-inventory-page/quickstart.md` across all 5 verification scenarios

---

## Dependencies & Execution Order

### Phase Dependencies

```
Phase 1: Setup (T001, T002)
   ↓
Phase 2: Foundational (T003, T004, T005, T006, T007)
   ↓
Phase 3: User Story 1 - MVP (T008, T009, T010, T011)
   ↓
Phase 4: User Story 2 - Listing (T012, T013, T014)
   ↓
Phase 5: User Story 3 - Delisting (T015, T016, T017)
   ↓
Phase 6: User Story 4 - Search & Pagination (T018, T019)
   ↓
Phase 7: User Story 5 - Responsiveness & Localization (T020, T021)
   ↓
Phase 8: Polish & Verification (T022, T023, T024)
```

### Parallel Execution Opportunities

- **Phase 1**: `T001` (`types/inventory.ts`) and `T002` (`services/inventoryService.ts`) can be implemented in parallel.
- **Phase 2**: `T004` (`Navbar.tsx`), `T006` (`TokenActionModal.tsx`), and `T007` (`TokenCard.tsx`) touch different files and can be built in parallel.
- **Phases 4 & 5**: Once User Story 1 (`InventoryPage.tsx` shell) is established, listing and delisting handlers follow identical modal wiring patterns.
- **Phase 8**: `T022` (TypeScript) and `T023` (Linter) can run in parallel.

---

## Implementation Strategy

### MVP Scope (Phases 1, 2, and 3)

1. Complete Setup (`T001`, `T002`) and Foundational (`T003` - `T007`).
2. Implement User Story 1 (`T008` - `T011`): `ProtectedLayout` redirection, `cancha.avif` background without `PageWindow`, Navbar link, two-tab view, and card rendering.
3. **Validate MVP**: Test unauthenticated redirect to `/login`, authenticated navigation to `/inventario`, and tab display.

### Incremental Feature Delivery

1. **MVP**: Protected layout + tab overview + cards display.
2. **Increment 1**: Listing flow via generic modal (`US2`).
3. **Increment 2**: Delisting flow via generic modal (`US3`).
4. **Increment 3**: Search filtering and pagination (`US4`).
5. **Increment 4**: Ultra-responsive styling (270px) and Spanish audit (`US5`).
6. **Final Gate**: Type checking (`tsc -b`), linting (`oxlint`), and quickstart verification.
