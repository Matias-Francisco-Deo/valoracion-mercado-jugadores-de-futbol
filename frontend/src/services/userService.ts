import type { UserInventoryResponse } from "@/types/inventory";
import { futbolApi } from "./api";

export async function getUserPortfolio(userId: string): Promise<UserInventoryResponse> {
  return futbolApi.get<UserInventoryResponse>(`/${userId}/portfolio`);
}