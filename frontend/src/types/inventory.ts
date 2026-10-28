export interface OrderTokensRequest {
  playerId: number;
  //userId: string;//quitar?
  quantity: number;
}

export interface TokenSummary {
  playerId: number;
  playerName: string;
  unitPrice : number;
  quantity: number;
}

export interface OrderSell{
  playerId: number;
  playerName: string;
  unitPrice : number;
  quantity: number;
}

export type InventoryToken = TokenSummary | OrderSell;//todavia no se usa de verdad

export interface PositionToken {
  playerId: number;
  playerName: string;
  quantity: number;
  averagePurchasePrice: number;
  currentPrice: number;
  currentValue?: number;
  profitLoss: number;
  profitLossPercentage?: number;
}

export interface Transaction {
  id:number;
  playerName:string;
  date:string;
  quantity:number;
  unitPrice:number;
  totalPrice:number;
  type: "BUY" | "SELL";
}

//-------------------------------------general
export interface PageResponse<T> {
    content: T[];
    number:number;
    size:number;
    totalPages:number;
    totalElements:number;
}

