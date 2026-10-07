# Data Model: Inventory Page and Token Listing Management

## Overview

This document specifies the TypeScript interfaces, domain entities, state lifecycle transitions, and client-side validation rules for the inventory and token listing feature.

---

## 1. Domain Entities & TypeScript Types

### `PlayerTokenHolding`
Represents an individual player token entry within a user's inventory holdings.

```typescript
export interface PlayerTokenHolding {
  /** Unique identifier of the player */
  playerId: string;
  /** Full display name of the football player */
  playerName: string;
  /** Number of tokens owned by the user that are NOT currently listed for sale (integer >= 0) */
  unlistedTokens: number;
  /** Number of tokens currently listed on the marketplace for sale (integer >= 0) */
  listedTokens: number;
  /** Current valuation price per individual token in USD (positive number) */
  pricePerToken: number;
}
```

### `TokenSaleListing`
Represents an active sale publication on the marketplace.

```typescript
export interface TokenSaleListing {
  /** Unique listing identifier */
  listingId: string;
  /** Player identifier */
  playerId: string;
  /** Quantity of tokens placed on sale (integer >= 1) */
  quantity: number;
  /** Fixed snapshot price per token at listing time */
  unitPrice: number;
  /** Total calculated valuation of the listing (quantity * unitPrice) */
  totalListingValue: number;
  /** Timestamp when the listing was created */
  createdAt: string;
  /** Status of the listing */
  status: 'ACTIVE' | 'CANCELLED' | 'SOLD';
}
```

### `UserInventoryResponse`
Aggregated payload returned by the `GET /inventory` REST endpoint.

```typescript
export interface UserInventoryResponse {
  /** Total count of distinct players owned */
  totalPlayersOwned: number;
  /** Aggregated total number of tokens (unlisted + listed) */
  totalTokensOwned: number;
  /** List of player holdings */
  holdings: PlayerTokenHolding[];
  /** List of active listings */
  activeListings: TokenSaleListing[];
}
```

---

## 2. API Request Payloads

### `ListTokensRequest`
Payload sent to `POST /inventory/listings` to publish tokens for sale.

```typescript
export interface ListTokensRequest {
  playerId: string;
  quantity: number;
}
```

### `CancelListingRequest`
Payload sent to `POST /inventory/listings/cancel` (or `DELETE /inventory/listings/:id`) to withdraw tokens from sale.

```typescript
export interface CancelListingRequest {
  playerId: string;
  quantity: number;
}
```

---

## 3. UI State Models

### `InventoryTab`
Active section currently displayed in the main container.

```typescript
export type InventoryTab = 'available' | 'for_sale';
```

### `TokenActionModalState`
Controls the generic modal's presentation and target player data.

```typescript
export interface TokenActionModalState {
  isOpen: boolean;
  actionType: 'list' | 'delist';
  playerId: string;
  playerName: string;
  unitPrice: number;
  maxTokens: number;
}
```

---

## 4. State Transitions & Lifecycle

### Listing Flow (`available` → `for_sale`):
1. **Initial State**: Player has `unlistedTokens = N` (N > 0). Appears in "Tokens Disponibles" tab.
2. **User Action**: User opens modal, selects `k` tokens (1 <= `k` <= `N`), and confirms.
3. **API Call**: `POST /inventory/listings` with `{ playerId, quantity: k }`.
4. **Result State**:
   - `unlistedTokens` decreases by `k` (`unlistedTokens' = N - k`).
   - `listedTokens` increases by `k`.
   - If `unlistedTokens' === 0`, the player card is removed from the "Tokens Disponibles" tab.
   - Player card appears in (or updates in) the "Tokens en Venta" tab with the updated `listedTokens` count.

### Delisting Flow (`for_sale` → `available`):
1. **Initial State**: Player has `listedTokens = M` (M > 0). Appears in "Tokens en Venta" tab.
2. **User Action**: User opens modal, selects `j` tokens (1 <= `j` <= `M`), and confirms.
3. **API Call**: `POST /inventory/listings/cancel` with `{ playerId, quantity: j }`.
4. **Result State**:
   - `listedTokens` decreases by `j` (`listedTokens' = M - j`).
   - `unlistedTokens` increases by `j`.
   - If `listedTokens' === 0`, the player card is removed from the "Tokens en Venta" tab.
   - Player card appears in (or updates in) the "Tokens Disponibles" tab with the updated `unlistedTokens` count.

---

## 5. Validation Rules

| Field / Action | Validation Rule | Error Feedback (Spanish) |
|---|---|---|
| Quantity to List | Integer >= 1 AND <= `unlistedTokens` | "Debe ingresar una cantidad entre 1 y {unlistedTokens}" |
| Quantity to Delist | Integer >= 1 AND <= `listedTokens` | "Debe ingresar una cantidad entre 1 y {listedTokens}" |
| Decimal Inputs | Whole integers only (no decimals or non-numeric) | "Solo se permiten números enteros" |
| Empty / Zero Input | Value must not be empty or 0 | "Debe ingresar al menos 1 token" |
| Search Input | String (trims whitespace, case-insensitive match) | Filter matches on `playerName.toLowerCase()` |
