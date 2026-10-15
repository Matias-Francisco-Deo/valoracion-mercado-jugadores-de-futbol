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

export async function getTokensOnSale(): Promise<PageResponse<TokenSummary>> {
  return futbolApi.get<PageResponse<TokenSummary>>('/orders/for-sale');
}

export async function buyListedTokens(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/buy', payload);
}