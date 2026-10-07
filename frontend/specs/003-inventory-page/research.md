# Research: Inventory Page, Protected Layout, and Token Listing Management

## Phase 0 Research Findings

This document consolidates technical investigations and architectural decisions for implementing the inventory page, authentication protection, generic action modal, and token listing management.

---

### Decision 1: Authentication Guard & ProtectedLayout Architecture

- **Decision**: Create a dedicated `ProtectedLayout` component at `src/layouts/ProtectedLayout.tsx` that inspects `useAuth().isAuthenticated`. If unauthenticated, it renders `<Navigate to="/login" replace />`. If authenticated, it renders `Navbar`, a `<main>` container with `cancha.avif` background (without wrapping children in `PageWindow`), `<Outlet />`, and `Footer`.
- **Rationale**: 
  - Directly fulfills the user's specification: "If necessary, a ProtectedLayout should be created, similar to the existing GeneralLayout, to prevent unauthenticated users from accessing the inventoryPage. ProtectedLayout must not use the PageWindow component and must use the cancha.avif image from the assets instead of pasto.jpg. If ProtectedLayout is created, the inventoryPage must no longer use the cancha.avif image directly, since the background will be handled by ProtectedLayout."
  - Centralizes route protection and background presentation at the layout level in accordance with Constitution Principle III (Centralized Routing).
  - Eliminates duplicate authentication checks across individual protected pages.
- **Alternatives Considered**:
  - *Route wrapper / HOC around Page component*: Rejected because layout-level encapsulation cleanly separates shell layout concerns (Navbar, Footer, `cancha.avif` background) from page content, and avoids conditional rendering boilerplate inside `InventoryPage`.
  - *Extending `GeneralLayout` with props*: Rejected because `GeneralLayout` is tightly coupled to `pasto.jpg` and `PageWindow`, and user specifically requested a dedicated `ProtectedLayout` mirroring `GeneralLayout`'s structural role.

---

### Decision 2: Generic Reusable Token Action Modal (`TokenActionModal`)

- **Decision**: Implement a single, generic reusable modal component (`src/components/inventory/TokenActionModal.tsx`) that drives both the "Poner a la venta" (listing) and "Cancelar venta" (delisting) workflows.
- **Rationale**:
  - Directly complies with user instruction: "The modal that appears when listing tokens for sale and canceling the sale must be a generic reusable modal that can be used in both cases."
  - Adheres to Constitution Principle IV (SRP, Reuse & Pragmatic Abstraction). Both actions share identical modal UX: displaying player name, unit price, quantity selector (with boundaries `[1, maxTokens]`), real-time calculated total (`quantity × unitPrice`), localized confirmation and cancellation buttons, and busy-state disabling.
- **Contract / Props**:
  - `isOpen: boolean`
  - `title: string` (e.g. "Publicar tokens para la venta" vs "Cancelar publicación de venta")
  - `playerName: string`
  - `unitPrice: number`
  - `maxTokens: number`
  - `confirmText: string` (e.g. "Confirmar publicación" vs "Confirmar cancelación")
  - `cancelText?: string` (default: "Cancelar")
  - `onConfirm: (quantity: number) => Promise<void> | void`
  - `onClose: () => void`
  - `isLoading?: boolean`
- **Alternatives Considered**:
  - *Two separate modal components (`SellModal` and `DelistModal`)*: Explicitly forbidden by user instructions to avoid redundant duplicate UI logic.

---

### Decision 3: Responsive Viewport Baseline Down to 270px

- **Decision**: Structure `InventoryPage`, `TokenCard`, and `TokenActionModal` with fluid Tailwind utilities supporting screens from 270px upwards (`min-w-[270px]`, `w-full`, flexible grids `grid-cols-1 sm:grid-cols-2 lg:grid-cols-3`, `px-2 sm:px-4`, `text-xs sm:text-sm`).
- **Rationale**:
  - Fulfills the requirement: "The responsive design of the page must support screen widths starting from 270px and above."
  - Devices such as the Samsung Galaxy Fold (folded outer screen ~280px) and ultra-compact devices require avoiding fixed min-widths larger than 250px.
  - Buttons, modal actions, and card rows will use `flex-wrap` or stack vertically on sub-320px screens while expanding to horizontal layouts on larger displays.
- **Alternatives Considered**:
  - *Standard 320px mobile baseline*: Rejected because user explicitly specified 270px minimum width support.

---

### Decision 4: Inventory Data Fetching & State Synchronization

- **Decision**: Create `src/services/inventoryService.ts` utilizing `futbolApi` (`request<T>`) to call backend REST endpoints:
  - `GET /inventory`: Retrieves user's token holdings (both unlisted available tokens and active sale listings).
  - `POST /inventory/listings`: Submits token listing (`{ playerId, quantity }`).
  - `DELETE /inventory/listings/{listingId}` or `POST /inventory/listings/cancel`: Delists tokens.
- **Rationale**:
  - Follows Constitution Principle I (Frontend-Only, backend communication exclusively through `futbolApi` and `.env` configuration).
  - Satisfies user clarification Q5 ("Contratos de API REST estrictos").
  - Upon successful listing/delisting response, the client updates its internal holdings state and reflects the updated balances across the "Tokens Disponibles" and "Tokens en Venta" tabs without requiring a full page reload.
  - When backend is unavailable or returns 500, displays `ServerErrorComponent` or localized user error message via `AlertBanner`.
- **Alternatives Considered**:
  - *Mock-only client store*: Rejected per user's explicit choice in clarification session Q5 for strict REST API contracts.

---

### Decision 5: Tab Organization & Card Lifecycle at Balance Zero

- **Decision**: The main container in `InventoryPage` provides tabs for "Tokens Disponibles" (default) and "Tokens en Venta". A single search input filters the active tab's card list. When a player's token count in a tab reaches 0, the card is removed from that tab and appears in the alternate tab with the corresponding updated balance.
- **Rationale**:
  - Aligned with clarification Q1 (Tabs) and clarification Q4 (Remove card from tab on zero balance).
  - Keeps each view focused on actionable items only, preventing clutter from 0-balance cards.
