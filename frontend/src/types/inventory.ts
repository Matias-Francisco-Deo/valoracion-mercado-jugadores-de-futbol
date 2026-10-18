export interface OrderTokensRequest {
  playerId: number;
  //userId: string;//quitar?
  quantity: number;
}

export interface TokenSummary {
  idPlayer: number;
  playerName: string;
  unitPrice : number;
  quantity: number;
}

export interface OrderSell{
  idPlayer: number;
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
  profitLoss: number;
}

export interface Transaction {//orderbuy?? revisar
  id:number;
  playerId: number
  playerName:string;
  date:string;
  type: "BUY" | "SELL";
  quantity:number;
  unitPrice:number;
  total:number;
}

//-------------------------------------general
export interface PageResponse<T> {
    content: T[];
    number:number;
    size:number;
    totalPages:number;
    totalElements:number;
}

