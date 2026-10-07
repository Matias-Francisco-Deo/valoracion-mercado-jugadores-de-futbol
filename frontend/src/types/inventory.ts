export interface PlayerTokenHolding {
  playerId: string;
  playerName: string;
  unlistedTokens: number;
  listedTokens: number;
  pricePerToken: number;
}

export interface TokenSaleListing {
  listingId: string;
  playerId: string;
  quantity: number;
  unitPrice: number;
  totalListingValue: number;
  createdAt: string;
  status: 'ACTIVE' | 'CANCELLED' | 'SOLD';
}

export interface UserInventoryResponse {
  totalPlayersOwned: number;
  totalTokensOwned: number;
  holdings: PlayerTokenHolding[];
  activeListings: TokenSaleListing[];
}

export interface ListTokensRequest {
  playerId: string;
  quantity: number;
}

export interface CancelListingRequest {
  playerId: string;
  quantity: number;
}

export type InventoryTab = 'available' | 'for_sale';

export interface TokenActionModalState {
  isOpen: boolean;
  actionType: 'list' | 'delist';
  playerId: string;
  playerName: string;
  unitPrice: number;
  maxTokens: number;
}
