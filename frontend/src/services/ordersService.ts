import { futbolApi } from './api';
import type {
  OrderTokensRequest,
  TokenSummary,
  PageResponse,
} from '@/types/inventory';

export async function listTokensForSale(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/sell', payload);
}

export async function cancelTokenListing(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/cancel', payload);
}

export async function getTokensOnSale(page: number = 0,nombre?: string): Promise<PageResponse<TokenSummary>> {
  const params = new URLSearchParams({
        page: page.toString(),
  });

  if (nombre?.trim()) {
      params.set("nombre", nombre.trim());
  }

  return futbolApi.get<PageResponse<TokenSummary>>(`/orders/for-sell?${params.toString()}`);
}

export async function buyListedTokens(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/buy', payload);
}