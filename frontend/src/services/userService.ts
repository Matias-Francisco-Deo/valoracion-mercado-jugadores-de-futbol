import type { OrderSell, PageResponse, PositionToken, TokenSummary, Transaction,  } from "@/types/inventory";
import { futbolApi } from "./api";

export async function getUserPortfolio(userId: string,page: number = 0): Promise<PageResponse<PositionToken>> {//recupera las posiciones del user
  return futbolApi.get<PageResponse<PositionToken>>(`users/${userId}/portfolio?page=${page}`);
}

export async function getTransactionHistory(userId: string,page: number = 0): Promise<PageResponse<Transaction>>{
  return futbolApi.get<PageResponse<Transaction>>(`users/${userId}/transactions?page=${page}`);
}

export async function getOrdersSell(userId:string,page: number = 0):Promise<PageResponse<OrderSell>> {//recupera ordenes de venta 
  return futbolApi.get<PageResponse<OrderSell>>(`users/${userId}/ordersSell?page=${page}`);
}

export async function getAviableTokens(userId:string,page: number = 0):Promise<PageResponse<TokenSummary>> {//recupera tokens para vender
  return futbolApi.get<PageResponse<TokenSummary>>(`users/${userId}/tokens?page=${page}`);
}