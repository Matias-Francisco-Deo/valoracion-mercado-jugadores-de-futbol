export interface OrderTokensRequest {
  playerId: number;
  //userId: string;
  quantity: number;
}

export interface UserInventoryResponse {
  userId: number;
  availableTokens:Token[];
  sellingTokens: Token[];
}

export interface Token {
  playerId: number;
  playerName: string;
  currentPrice: number;
  selling: boolean;
  quantity: number;
}

export interface TokenPageResponse {
    content: Token[];
    number:number;
    size:number;
    totalPages:number;
    totalElements:number;
}

export interface TokenActionModalState {
  isOpen: boolean;
  actionType: 'list' | 'delist';
  playerId: string;
  playerName: string;
  unitPrice: number;
  maxTokens: number;
}
