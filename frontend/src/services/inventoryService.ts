import { futbolApi } from './api';
import type {
  UserInventoryResponse,
  OrderTokensRequest,
} from '@/types/inventory';

export async function getUserInventory(): Promise<UserInventoryResponse> {
  return futbolApi.get<UserInventoryResponse>('/inventory');
}

export async function listTokensForSale(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/sell', payload);
}

export async function cancelTokenListing(payload: OrderTokensRequest): Promise<void> {
  return futbolApi.post<void>('/orders/cancel', payload);
}
