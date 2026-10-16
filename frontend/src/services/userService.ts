import type { OrderSell, PageResponse, PositionToken, TokenSummary, Transaction,  } from "@/types/inventory";
import { futbolApi } from "./api";

export async function getUserPortfolio(userId: string,page: number = 0): Promise<PageResponse<PositionToken>> {//recupera las posiciones del user
  return futbolApi.get<PageResponse<PositionToken>>(`/users/${userId}/portfolio?page=${page}`);
}

export async function getTransactionHistory(userId: string,page: number = 0): Promise<PageResponse<Transaction>>{
  return futbolApi.get<PageResponse<Transaction>>(`/users/${userId}/transactions?page=${page}`);
}

export async function getOrdersSell(userId:string,page: number = 0,search: string = ""):Promise<PageResponse<OrderSell>> {//recupera ordenes de venta 
  const query = new URLSearchParams({page: String(page),});

  if (search.trim()) {query.set("search", search.trim());}

  return futbolApi.get<PageResponse<OrderSell>>( `/orders/${userId}/ordersSell?${query.toString()}`);
}

export async function getAviableTokens(userId:string,page: number = 0,search: string = ""):Promise<PageResponse<TokenSummary>> {//recupera tokens para vender
  const query = new URLSearchParams({page: String(page),});

  if (search.trim()) {query.set("search", search.trim());}
  
  return futbolApi.get<PageResponse<TokenSummary>>(`/users/${userId}/tokens?${query.toString()}`);
}