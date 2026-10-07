import { futbolApi } from './api';
import type {
  UserInventoryResponse,
  ListTokensRequest,
  CancelListingRequest,
} from '@/types/inventory';

export async function getUserInventory(): Promise<UserInventoryResponse> {
  return futbolApi.get<UserInventoryResponse>('/inventory');
}

export async function listTokensForSale(payload: ListTokensRequest): Promise<void> {
  return futbolApi.post<void>('/inventory/listings', payload);
}

export async function cancelTokenListing(payload: CancelListingRequest): Promise<void> {
  return futbolApi.post<void>('/inventory/listings/cancel', payload);
}
