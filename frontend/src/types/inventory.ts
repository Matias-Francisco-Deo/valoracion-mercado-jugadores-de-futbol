export interface OrderTokensRequest {
  playerId: number;
  //userId: string;
  quantity: number;
}

export interface UserInventoryResponse {
  userId: number;
  totalTokens: number;//borrar?
  availableTokens:Token[];//cambiar por TokenPageResponse<Token>
  sellingTokens: Token[];
  position:TokenPageResponse<PositionToken>;
}

export interface Token {
  playerId: number;
  playerName: string;
  currentPrice : number;
  selling: boolean;
  quantity: number;
}

export interface PositionToken {
  playerId: number;
  playerName: string;
  quantity: number;
  averagePurchasePrice: number;
  currentPrice: number;
  profitLoss: number;
}

export interface TokenPageResponse<T> {
    content: T[];
    number:number;
    size:number;
    totalPages:number;
    totalElements:number;
}

export interface Transaction {
  id:number;
  date:string;
  playerName:string;
  type:"buy"|"sell";//sell pending?
  quantity:number;
  unitPrice:number;
  totalAmount:number;
}