# Interface Contracts: Inventory & Token Listings

## 1. REST API Contracts

All requests send `Authorization: Bearer <token>` through the existing `futbolApi` client abstraction configured with `VITE_API_BASE_URL`.

---

### `GET /inventory`
Fetches the current authenticated user's inventory holdings and listings.

**Headers**:
- `Authorization: Bearer <session-token>`

**Response `200 OK`**:
```json
{
  "totalPlayersOwned": 6,
  "totalTokensOwned": 21,
  "holdings": [
    {
      "playerId": "p-101",
      "playerName": "Lionel Messi",
      "unlistedTokens": 10,
      "listedTokens": 0,
      "pricePerToken": 125
    },
    {
      "playerId": "p-102",
      "playerName": "Kylian Mbappé",
      "unlistedTokens": 4,
      "listedTokens": 0,
      "pricePerToken": 98
    },
    {
      "playerId": "p-103",
      "playerName": "Jude Bellingham",
      "unlistedTokens": 7,
      "listedTokens": 2,
      "pricePerToken": 84
    }
  ],
  "activeListings": [
    {
      "listingId": "list-501",
      "playerId": "p-103",
      "quantity": 2,
      "unitPrice": 84,
      "totalListingValue": 168,
      "createdAt": "2026-10-07T14:30:00Z",
      "status": "ACTIVE"
    }
  ]
}
```

**Error Responses**:
- `401 Unauthorized`: Triggers automatic session clearance and redirection to `/login`.
- `500 Internal Server Error`: Generic client error message displayed via `ServerErrorComponent`.

---

### `POST /inventory/listings`
Publishes user tokens for sale on the marketplace (without immediate liquidation).

**Request Body**:
```json
{
  "playerId": "p-101",
  "quantity": 3
}
```

**Response `201 Created` / `200 OK`**:
```json
{
  "message": "Tokens puestos a la venta exitosamente",
  "listingId": "list-502",
  "playerId": "p-101",
  "quantity": 3,
  "unitPrice": 125,
  "totalListingValue": 375
}
```

**Error Responses**:
- `400 Bad Request`: `{ "message": "No posees suficientes tokens disponibles para listar." }` (displayed to user via `AlertBanner`).
- `401 Unauthorized`: Session expired redirection.
- `500 Internal Server Error`: Handled generically without leaking server internals.

---

### `POST /inventory/listings/cancel`
Cancels a token sale publication and returns tokens to unlisted available status.

**Request Body**:
```json
{
  "playerId": "p-103",
  "quantity": 2
}
```

**Response `200 OK`**:
```json
{
  "message": "Publicación cancelada exitosamente",
  "playerId": "p-103",
  "quantityDelisted": 2
}
```

**Error Responses**:
- `400 Bad Request`: `{ "message": "La cantidad a cancelar supera los tokens listados actualmente." }`
- `500 Internal Server Error`: Handled generically.

---

## 2. Component Interface Contracts

### `TokenActionModalProps` (`src/components/inventory/TokenActionModal.tsx`)

```typescript
export interface TokenActionModalProps {
  /** Controls modal visibility */
  isOpen: boolean;
  /** Modal title displayed in header */
  title: string;
  /** Name of the football player */
  playerName: string;
  /** Current fixed price per token (in USD) */
  unitPrice: number;
  /** Maximum number of tokens eligible for this action */
  maxTokens: number;
  /** Primary action button label (e.g., "Confirmar publicación", "Confirmar cancelación") */
  confirmText: string;
  /** Cancel button label (default: "Cancelar") */
  cancelText?: string;
  /** Operation indicator to distinguish listing vs delisting styles */
  actionType: 'list' | 'delist';
  /** Callback triggered when user confirms with selected valid quantity */
  onConfirm: (quantity: number) => Promise<void> | void;
  /** Callback triggered when user dismisses the modal */
  onClose: () => void;
  /** Disables controls while submission is in-flight */
  isLoading?: boolean;
}
```

### `TokenCardProps` (`src/components/inventory/TokenCard.tsx`)

```typescript
export interface TokenCardProps {
  /** Player name to render */
  playerName: string;
  /** Number of tokens to display */
  tokens: number;
  /** Label for tokens badge (e.g. "tokens disponibles" or "tokens en venta") */
  tokensLabel: string;
  /** Price per individual token */
  pricePerToken: number;
  /** Action button label (e.g. "Poner a la venta", "Cancelar venta") */
  actionButtonText: string;
  /** Callback when action button is clicked */
  onActionClick: () => void;
  /** Optional container class overrides */
  className?: string;
}
```
