import type { UserInventoryResponse } from "@/types/inventory";
import { futbolApi } from "./api";

export async function getUserInventory(userId:string): Promise<UserInventoryResponse> {
  return futbolApi.get<UserInventoryResponse>(`/users/${userId}/portfolio`);
}
