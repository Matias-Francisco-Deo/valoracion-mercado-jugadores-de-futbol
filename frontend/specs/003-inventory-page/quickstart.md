# Quickstart & Verification Guide: Inventory Page and Token Listing Management

## Overview

This guide details the step-by-step validation scenarios to verify that the inventory page, authentication guard (`ProtectedLayout`), generic reusable modal (`TokenActionModal`), tab switching, and ultra-responsive layout (down to 270px) function correctly.

---

## Prerequisites & Setup

1. Start the development server:
   ```bash
   pnpm dev
   ```
2. Open the application in your browser (default: `http://localhost:5173`).

---

## Validation Scenarios

### Scenario 1: Authentication Guard & Redirection
1. Ensure you are **not** logged in (or open an Incognito browser window).
2. Manually enter the URL: `http://localhost:5173/inventario`.
3. **Expected Outcome**:
   - The user is immediately redirected to `http://localhost:5173/login`.
   - The protected layout prevents any flashing of private inventory content.

---

### Scenario 2: Authenticated Navigation & ProtectedLayout Presentation
1. Log in with valid credentials at `/login`.
2. Observe the top Navbar:
   - An "Inventario" navigation link appears beside "Catálogo".
3. Click "Inventario" (navigating to `/inventario`).
4. **Expected Outcome**:
   - `ProtectedLayout` renders the page with `cancha.avif` background (covering the viewport) without using `PageWindow`.
   - The page header displays "COLECCIÓN PERSONAL", "Inventario", subtitle, and player counters.
   - The main white container card renders with the search bar and the two tabs: "Tokens Disponibles" (active by default) and "Tokens en Venta".

---

### Scenario 3: Token Listing via Generic Reusable Modal
1. On the "Tokens Disponibles" tab, locate a player card with available tokens (e.g. Lionel Messi with 10 tokens @ $125).
2. Click the button **"Poner a la venta"**.
3. **Expected Outcome**:
   - The generic `TokenActionModal` opens with title "Publicar tokens para la venta".
   - Shows player name, unit price ($125), and maximum tokens (10).
   - Enter `3` in the quantity field:
     - The calculated total updates instantly to `$375`.
   - Test invalid inputs (e.g. `0`, `-1`, `11`, `abc`):
     - Inline Spanish error appears; "Confirmar publicación" button is disabled.
4. Enter `3` and click **"Confirmar publicación"**:
   - Modal closes, success message appears.
   - Lionel Messi's available tokens count updates from 10 to 7 in "Tokens Disponibles".
   - Switch to the "Tokens en Venta" tab: Lionel Messi now appears with 3 tokens listed.
5. If the user lists all remaining 7 tokens:
   - Lionel Messi card is removed from "Tokens Disponibles" and displays with 10 tokens in "Tokens en Venta".

---

### Scenario 4: Delisting via Generic Reusable Modal
1. On the "Tokens en Venta" tab, locate a player with listed tokens (e.g. Lionel Messi with 10 tokens en venta).
2. Click the button **"Cancelar venta"** (or **"Retirar"**).
3. **Expected Outcome**:
   - The same generic `TokenActionModal` opens with title "Cancelar publicación de venta".
   - Primary button displays "Confirmar cancelación".
   - Quantity selector allows selecting between 1 and 10.
   - Enter `4`: total valuation displays `$500`.
4. Click **"Confirmar cancelación"**:
   - Modal closes, success message appears.
   - Lionel Messi's listed count updates to 6 in "Tokens en Venta".
   - Switch to "Tokens Disponibles": Lionel Messi now has 4 tokens disponibles.

---

### Scenario 5: Ultra-Responsive Design Validation (Down to 270px)
1. Open Chrome DevTools (`F12`), toggle Device Toolbar (`Ctrl+Shift+M`).
2. Set responsive dimensions to **270px width** (and test up through 320px, 768px, 1280px).
3. Verify:
   - No horizontal scrollbars appear on the page or inside the main container.
   - Navbar, search bar, tabs, and token cards wrap gracefully.
   - Opening `TokenActionModal` at 270px keeps the modal centered with fully visible, accessible inputs and buttons.

---

## Static Analysis & Quality Gate Commands

Run the quality verification commands:

```bash
# Verify TypeScript strict type-checking (Zero errors, no 'any')
pnpm run build

# Verify linter rules
pnpm lint
```
