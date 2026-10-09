import type { UserInventoryResponse } from "@/types/inventory";
import { futbolApi } from "./api";

export async function getUserInventory(): Promise<UserInventoryResponse> {
  return futbolApi.get<UserInventoryResponse>('/inventory');
}
