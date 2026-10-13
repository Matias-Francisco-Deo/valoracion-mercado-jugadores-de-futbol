import { futbolApi } from './api';
import type {
  OrderTokensRequest,
  Token,
  TokenPageResponse,
} from '@/types/inventory';

export async function listTokensForSale(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/sell', payload);
}

export async function cancelTokenListing(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/cancel', payload);
}

export async function getTokensOnSale(): Promise<TokenPageResponse<Token>> {
  return futbolApi.get<TokenPageResponse<Token>>('/orders/for-sale');
}

export async function buyTokens(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/buy', payload);
}