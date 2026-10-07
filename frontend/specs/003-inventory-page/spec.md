# Feature Specification: Inventory Page and Token Listing Management

**Feature Branch**: `003-inventory-page`

**Created**: 2026-10-07

**Status**: Draft

**Input**: User description: "Create the inventoryPage using the image located at src/assets/references as a reference. For the page background image, use the cancha.avif file inside the assets folder. Do not copy the navigation bar from the image, since it already exists in the layout. For the token cards shown in the image, create a design that is visually similar to the playerCard component, but only include the following data: Player name, number of tokens, price per token, sell button. Clicking Sell should open a modal displaying the same data as the token card and allowing the user to: Select the number of tokens to sell, See the total value of the tokens being sold, Confirm or cancel the transaction. The color palette should be similar to the rest of the pages. All visible UI text must be in Spanish. Modify the specification to add two sections to the inventory. One section will contain tokens that are not currently listed for sale, and the other will contain tokens that are currently listed for sale. When confirming the sale in the modal, the tokens should be listed for sale, but this should not trigger an immediate sale or transaction. The user must be able to remove tokens from the for-sale status according to the quantity requested by the user. To remove tokens from their for-sale status, a modal should open where the user can select the quantity of tokens to delist and then confirm the cancellation of the token sale listing."

## Clarifications

### Session 2026-10-07

- Q: ¿Cómo deben estructurarse visualmente en la página las dos secciones del inventario ("Tokens Disponibles" y "Tokens en Venta")? → A: Mediante pestañas navegables (tabs) dentro del contenedor principal para alternar entre "Tokens Disponibles" y "Tokens en Venta".
- Q: ¿El precio por token al poner tokens a la venta debe ser fijo según el valor de mercado actual del jugador, o el usuario puede definir un precio personalizado? → A: Precio fijo de mercado (el precio por token se toma automáticamente de la cotización actual del jugador y no puede ser modificado por el usuario en el modal).
- Q: ¿En qué parte de la barra de navegación existente debe ubicarse el acceso a la página de inventario (/inventario) para los usuarios autenticados? → A: Enlace directo en el Navbar (un enlace "Inventario" visible junto a "Catálogo" en la barra de navegación superior cuando el usuario está autenticado).
- Q: ¿Qué debe ocurrir con la tarjeta de un jugador en una pestaña cuando su cantidad de tokens llega a cero? → A: Remover de la pestaña (la tarjeta se oculta de la pestaña actual si su saldo en esa categoría llega a 0 y pasa a mostrarse en la otra pestaña con su saldo correspondiente).
- Q: ¿Cómo deben obtenerse y gestionarse inicialmente los datos del inventario y las publicaciones de tokens en el frontend? → A: Contratos de API REST estrictos (el frontend consume endpoints dedicados de backend para obtener el inventario, publicar tokens para la venta y cancelar publicaciones, manejando estados de carga y errores de conexión o servidor según los estándares del proyecto).

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Two-Section Inventory Overview (Tokens Disponibles vs. Tokens en Venta) (Priority: P1)

As an authenticated user, I want to see my inventory clearly organized into two selectable tabs—tokens not listed for sale (available) and tokens currently listed for sale—so that I have immediate visibility over which tokens are available for new listings and which are already published in the marketplace while retaining a clean, non-cluttered interface.

**Why this priority**: Segregating available holdings from active marketplace listings is the primary organizing principle of the inventory, preventing confusion about token status.

**Independent Test**: Navigate to `/inventario` as an authenticated user, verify the presence of both tab selectors ("Tokens Disponibles" and "Tokens en Venta"), switch between tabs, verify the respective cards render with appropriate status indicators and counts, and confirm that unauthenticated visitors are redirected to `/login`.

**Acceptance Scenarios**:

1. **Given** an authenticated user on `/inventario`, **When** the page renders, **Then** it displays the header with the tag "COLECCIÓN PERSONAL", the main title "Inventario", descriptive subtitle "Revisa los jugadores que posees y gestiona tus tokens en venta.", and a tab selector with "Tokens Disponibles" (active by default) and "Tokens en Venta".
2. **Given** a user with tokens not listed for sale, **When** the "Tokens Disponibles" tab is selected, **Then** each card displays exclusively the player's name, the number of unlisted tokens owned (e.g. "10 tokens disponibles"), the price per token (e.g. "$125"), and a button labeled "Poner a la venta", styled with the aesthetic of `PlayerCard`.
3. **Given** a user with tokens listed for sale, **When** the "Tokens en Venta" tab is selected, **Then** each card displays exclusively the player's name, the number of tokens currently listed (e.g. "4 tokens en venta"), the price per token, and a button labeled "Cancelar venta" (or "Retirar"), styled consistently with `PlayerCard`.
4. **Given** an unauthenticated visitor, **When** they try to open `/inventario`, **Then** the application redirects them to `/login`.
5. **Given** either tab has zero tokens, **When** that tab is viewed, **Then** it displays a dedicated, friendly Spanish empty state message (e.g. "No tienes tokens disponibles" or "No tienes tokens en venta").

---

### User Story 2 - Listing Tokens for Sale via Modal (Priority: P1)

As a token owner, I want to click "Poner a la venta" on an unlisted token card to open a modal where I select how many tokens to list and see their total valuation, so that confirming will publish them for sale without executing an immediate cash transaction.

**Why this priority**: Users need explicit control over how many tokens they place on the market, calculating potential earnings dynamically while knowing this creates a sale listing rather than an instant liquidation.

**Independent Test**: Click "Poner a la venta" on an unlisted token card, verify the modal shows player name, available unlisted tokens, and unit price, adjust the quantity to list, verify real-time total valuation calculation, confirm the listing, and verify that the specified quantity moves from "Tokens Disponibles" to "Tokens en Venta".

**Acceptance Scenarios**:

1. **Given** a token card in "Tokens Disponibles", **When** the user clicks "Poner a la venta", **Then** a modal opens titled "Publicar tokens para la venta" displaying player name, available tokens count, unit price, a quantity selector, the dynamically calculated total listing value (`cantidad × precio`), and buttons "Confirmar publicación" and "Cancelar".
2. **Given** the listing modal is open, **When** the user adjusts the quantity between 1 and the maximum available unlisted tokens, **Then** the total value updates in real time to match `(cantidad) × (precio unitario)`.
3. **Given** a valid quantity selected, **When** the user clicks "Confirmar publicación", **Then** the tokens are listed for sale (without executing an immediate payment or permanent liquidation), the modal closes, a Spanish confirmation notice appears ("Tokens publicados para la venta"), the unlisted count decreases by the selected amount, and the "Tokens en Venta" section increases by that amount.
4. **Given** a user lists all available tokens for a player, **When** the listing is confirmed, **Then** that player is removed from the "Tokens Disponibles" tab and appears in the "Tokens en Venta" tab.
5. **Given** the user clicks "Cancelar" or dismisses the modal, **When** cancelled, **Then** the modal closes without altering any token counts.

---

### User Story 3 - Delisting / Removing Tokens from Sale via Modal (Priority: P1)

As a seller who previously listed tokens, I want to click "Cancelar venta" on a listed token card to open a modal where I can select the quantity of tokens to delist and confirm the cancellation, returning those tokens back to my available inventory.

**Why this priority**: Users must be able to change their mind and retrieve their tokens from the marketplace partially or entirely at any time.

**Independent Test**: Click "Cancelar venta" on a card in "Tokens en Venta", verify the delist modal opens showing player name, currently listed tokens, and unit price, select the quantity of tokens to remove, verify the total value display, click "Confirmar cancelación", and verify that the tokens return to "Tokens Disponibles".

**Acceptance Scenarios**:

1. **Given** a card in the "Tokens en Venta" section, **When** the user clicks "Cancelar venta", **Then** a modal opens titled "Cancelar publicación de venta" displaying the player's name, number of tokens currently listed, unit price per token, a quantity selector to delist, the calculated total value of delisted tokens, and buttons "Confirmar cancelación" and "Cancelar".
2. **Given** the delisting modal is open, **When** the user enters a quantity to delist between 1 and the maximum currently listed tokens for that player, **Then** the modal calculates and displays the total valuation of the tokens being withdrawn.
3. **Given** a valid delisting quantity, **When** the user clicks "Confirmar cancelación", **Then** the selected tokens are removed from the for-sale status, the modal closes, a Spanish confirmation message is displayed ("Publicación cancelada exitosamente"), the listed count decreases by that amount, and the unlisted available count increases by that amount.
4. **Given** all listed tokens for a player are removed from sale, **When** confirmed, **Then** that player is removed from "Tokens en Venta" and appears in "Tokens Disponibles".
5. **Given** an invalid quantity in the delist modal (e.g. 0, negative, non-integer, or greater than currently listed), **When** entered, **Then** an inline Spanish validation warning appears and "Confirmar cancelación" remains disabled.

---

### User Story 4 - Inventory Search, Filtering & Section Pagination (Priority: P2)

As a collector with multiple players across both categories, I want to filter both sections by player name and navigate across pages so that I can easily find specific tokens.

**Why this priority**: Ensures scalable usability when users manage dozens of player holdings across unlisted and listed categories.

**Independent Test**: Use the search input to filter player cards by name across sections; verify that counters update and pagination allows switching pages when item counts exceed page limits.

**Acceptance Scenarios**:

1. **Given** token cards in the inventory, **When** the user enters a name in "Buscar jugadores en tu inventario", **Then** the visible cards in both sections filter in real time to match the search query, with player counters updating accordingly.
2. **Given** a search query with no matching players in either section, **When** entered, **Then** each section displays a localized empty search result message.
3. **Given** any section with more items than the page capacity, **When** rendered, **Then** pagination controls appear with previous/next and numbered page buttons in brand styling.

---

### User Story 5 - Responsive Layout & Visual Harmony (Priority: P3)

As a user on mobile, tablet, or desktop, I want the two-section inventory layout and modals to maintain visual alignment with the application's pitch aesthetics and theme without layout breaks.

**Why this priority**: Guarantees a cohesive, readable, and functional user experience across all device screen sizes.

**Independent Test**: Resize the browser viewport from 320px to desktop sizes, verifying fluid layout scaling, `cancha.avif` background display, proper modal centering, brand orange buttons, and 100% Spanish UI text.

**Acceptance Scenarios**:

1. **Given** any screen resolution from 320px wide up to desktop, **When** `/inventario` renders, **Then** the sections adapt fluidly (1 column on mobile, 2 on tablet, 3 on desktop), the background image `cancha.avif` scales properly, and no horizontal scrollbar is introduced.
2. **Given** either modal (listing or delisting) opens on a mobile screen, **When** rendered, **Then** it remains centered with adequate padding, accessible buttons, and clear text without truncation or clipping.
3. **Given** any view or interaction on `/inventario`, **When** inspected, **Then** 100% of visible texts, labels, buttons, and notifications are in Spanish.

---

### Edge Cases

- **Listing entire holding**: When all available tokens for a player are listed (e.g., 10 out of 10), the player card is removed from "Tokens Disponibles" and added to "Tokens en Venta" with 10 tokens.
- **Delisting entire listing**: When all listed tokens for a player are delisted (e.g., 4 out of 4), the card is removed from "Tokens en Venta" and merged into "Tokens Disponibles" with an increased available balance.
- **Exceeding boundaries**: Attempting to list more tokens than available, or delisting more tokens than currently listed, immediately displays an inline validation message and disables the confirmation action.
- **Zero, negative, or decimal input**: Inputs of 0, negative values, decimals, or non-numeric characters in either modal are rejected with localized validation feedback.
- **Immediate sale non-trigger**: Confirming the listing modal MUST NOT initiate a transaction settlement, payment capture, or removal of user ownership; tokens remain user-owned and are flagged as listed for sale on the marketplace.
- **Rapid repeated submissions**: Confirmation buttons in both modals are disabled while processing to prevent duplicate requests.
- **Modal backdrop dismissal & Escape key**: Clicking outside either modal or pressing Escape dismisses the modal without modifying token statuses.
- **Simultaneous state in both sections**: A player can have tokens in both sections simultaneously (e.g., 6 tokens disponibles and 4 tokens en venta). Each section reflects the respective balance and enables the corresponding action.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST make the inventory page accessible at route `/inventario` within a dedicated `ProtectedLayout` that redirects unauthenticated users to `/login`.
- **FR-002**: System MUST render the pitch background using the `cancha.avif` asset from `src/assets/` at the `ProtectedLayout` level without using `PageWindow`; `InventoryPage` MUST NOT duplicate or declare its own background image directly.
- **FR-003**: System MUST NOT duplicate or recreate the top navigation bar inside the inventory page component, relying on the existing layout header, and MUST provide an "Inventario" navigation link beside "Catálogo" in the header navbar when a user is authenticated.
- **FR-004**: System MUST render an inventory header with the tag "COLECCIÓN PERSONAL", title "Inventario", subtitle "Revisa los jugadores que posees y gestiona tus tokens en venta.", and summary indicators of owned tokens and players.
- **FR-005**: System MUST organize the inventory view into two selectable tabs within the main container card:
  1. **Tokens Disponibles**: Tokens owned by the user that are NOT currently listed for sale (selected by default).
  2. **Tokens en Venta**: Tokens owned by the user that ARE currently listed for sale on the marketplace.
- **FR-006**: System MUST provide a search input with placeholder "Buscar jugadores en tu inventario" that filters the cards in both sections by player name.
- **FR-007**: System MUST render token cards in both sections that visually align with the styling, borders, card container aesthetic, and color palette of `PlayerCard`.
- **FR-008**: Token cards in the "Tokens Disponibles" section MUST display strictly and exclusively:
  1. Player name (Nombre del jugador)
  2. Number of unlisted tokens available (Cantidad de tokens disponibles)
  3. Price per token (Precio por token)
  4. Sell listing button (Botón "Poner a la venta")
- **FR-009**: Token cards in the "Tokens en Venta" section MUST display strictly and exclusively:
  1. Player name (Nombre del jugador)
  2. Number of tokens listed for sale (Cantidad de tokens en venta)
  3. Price per token (Precio por token)
  4. Delist / cancel listing button (Botón "Cancelar venta" o "Retirar")
- **FR-010**: Clicking "Poner a la venta" on an unlisted token card MUST open a generic reusable modal (`TokenActionModal`) configured for listing tokens.
- **FR-011**: The generic modal configured for listing MUST display the player's name, number of available unlisted tokens, fixed price per token (read-only, derived from the player's current market valuation), a quantity selector, the dynamically computed total listing value (`cantidad × precio`), and buttons "Confirmar publicación" and "Cancelar".
- **FR-012**: Confirming the Sell Listing Modal MUST list the selected quantity of tokens for sale, decreasing the available unlisted count and increasing the listed count, WITHOUT executing an immediate purchase, liquidation, or payment transaction.
- **FR-013**: Clicking "Cancelar venta" on a listed token card MUST open the same generic reusable modal (`TokenActionModal`) configured for delisting tokens.
- **FR-014**: The generic modal configured for delisting MUST display the player's name, number of tokens currently listed for sale, fixed price per token (read-only, derived from the player's current market valuation), a quantity selector for delisting, the calculated total value of delisted tokens, and buttons "Confirmar cancelación" and "Cancelar".
- **FR-015**: Confirming the Delisting Modal MUST remove the selected quantity of tokens from the for-sale status, returning them to the "Tokens Disponibles" balance.
- **FR-016**: Both modal configurations MUST enforce quantity validation, requiring positive whole integers between 1 and the respective maximum (available tokens for listing, listed tokens for delisting), disabling confirmation when invalid.
- **FR-017**: System MUST display dedicated Spanish empty states when a section has zero tokens or when a search returns zero results in a section.
- **FR-018**: System MUST provide pagination controls for each section when the number of cards exceeds the display capacity.
- **FR-019**: System MUST ensure that 100% of visible UI text, labels, buttons, placeholders, notifications, and error messages are written in Spanish.
- **FR-020**: System MUST adhere to the application color palette, using brand orange (`#FF9500`) for primary buttons and active indicators, card grays, and pitch greens.
- **FR-021**: System MUST provide fluid responsiveness supporting ultra-compact screens starting from 270px and above through desktop viewports without horizontal scrolling.
- **FR-022**: System MUST retrieve user inventory holdings and active listings from dedicated backend REST API endpoints using the existing `futbolApi` client abstraction and MUST render the shared `Loading` component while data requests are in progress.
- **FR-023**: System MUST execute token listing submissions and delisting cancellations via dedicated backend REST API endpoints, handling responses according to Constitution Principle I (displaying backend error messages for client errors and generic feedback for 500 server errors).

### Key Entities *(include if feature involves data)*

- **Player Token Holding (Posesión de Tokens de Jugador)**:
  - `playerId`: Unique identifier of the player.
  - `playerName`: Full display name of the football player.
  - `unlistedTokens`: Number of tokens owned and available for listing (integer >= 0).
  - `listedTokens`: Number of tokens owned and currently published for sale (integer >= 0).
  - `pricePerToken`: Current valuation price per token in currency units ($).
- **Token Sale Listing (Publicación de Venta de Tokens)**:
  - `listingId`: Identifier of the active sale listing.
  - `playerId`: Identifier of the player whose tokens are listed.
  - `quantity`: Quantity of tokens published for sale.
  - `unitPrice`: Valuation price per token.
  - `totalListingValue`: Total value of the listed tokens (`quantity * unitPrice`).
  - `status`: Listing status (e.g., "active", "cancelled").
- **Delist Request (Solicitud de Retiro de Publicación)**:
  - `playerId`: Identifier of the player.
  - `quantityToDelist`: Number of tokens being removed from the sale listing.
  - `delistedValue`: Valuation of the tokens being returned to unlisted status.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of visible UI text, labels, buttons, alerts, and placeholders are in Spanish.
- **SC-002**: Users can complete a token listing action (from clicking "Poner a la venta" to confirmation) in under 15 seconds.
- **SC-003**: Users can complete a token delisting action (from clicking "Cancelar venta" to confirmation) in under 15 seconds.
- **SC-004**: Real-time total value calculations in both modals update within 50 milliseconds upon modifying the token quantity.
- **SC-005**: 100% of invalid quantities (zero, negative, decimals, or quantities exceeding respective section balance) prevent confirmation with localized inline validation.
- **SC-006**: 100% of confirmed listings and cancellations immediately reflect updated balances across both sections in the UI without page reload.
- **SC-007**: The inventory page, sections, and both modals render without horizontal scrolling or overlapping elements across viewports from 320px to 2560px.
- **SC-008**: Search filtering updates the visible cards in both sections within 100 milliseconds of user input.

## Assumptions

- Tokens listed for sale remain in the user's possession on the marketplace and are not transferred or converted into funds until purchased by another user; listing does not execute an immediate sale transaction.
- Delisting tokens from sale does not incur any cancellation penalty or fee; tokens return immediately to the unlisted pool.
- The inventory page represents a private user area requiring an active session; unauthenticated access redirects to `/login`.
- The top navigation bar is provided by `ProtectedLayout` and is not duplicated inside the inventory page.
- The background asset `cancha.avif` is available at `src/assets/cancha.avif`.
- Token quantities are strictly positive whole integers; fractional tokens are not supported.
- Prices and valuations are displayed in dollar currency denomination (e.g. `$125`).
- The backend provides or will provide REST endpoints for retrieving user token holdings, publishing token listings, and cancelling listings authenticated via the user's active session token.
