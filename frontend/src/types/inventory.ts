export interface OrderTokensRequest {
  playerId: number;
  //userId: string;
  quantity: number;
}

export interface UserInventoryResponse {
  userId: number;
  userPosition:number;
  totalTokens: number;//cant tokens totales del user
  totalAccountValue: number;//valor del portfolio entero
  averagePurchasePrice: number;//precio promedio deo user
  profitLoss: number;//ganancia/perdida del portfolio entero
  availableTokens:Token[];
  sellingTokens: Token[];
}

export interface Token {
  playerId: number;
  playerName: string;
  currentPrice : number;
  selling: boolean;
  quantity: number;
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