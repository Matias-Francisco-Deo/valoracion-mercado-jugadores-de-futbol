import { futbolApi } from './api';
import type {
  OrderTokensRequest,
  PageResponse,
  OrderSell,
} from '@/types/inventory';

export async function listTokensForSale(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/sell', payload);
}

export async function cancelTokenListing(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/cancel', payload);
}

export async function getTokensOnSale(page: number = 0,nombre?: string): Promise<PageResponse<OrderSell>> {
  const params = new URLSearchParams({
        page: page.toString(),
  });

  if (nombre?.trim()) {
      params.set("nombre", nombre.trim());
  }

  return futbolApi.get<PageResponse<OrderSell>>(`/orders/for-sell?${params.toString()}`);
}

export async function buyListedTokens(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/buy', payload);
}