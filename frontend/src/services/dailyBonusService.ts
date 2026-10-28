import { futbolApi } from './api';
import type { DailyBonusStatus, ClaimBonusResponse } from '@/types/bonus';

/**
 * Service to interact with the daily login streak bonus API.
 */
export async function getDailyBonusStatus(): Promise<DailyBonusStatus> {
  return futbolApi.get<DailyBonusStatus>('/api/daily-bonus');
}

export async function claimDailyBonus(): Promise<ClaimBonusResponse> {
  return futbolApi.post<ClaimBonusResponse>('/api/daily-bonus/claim', {});
}
