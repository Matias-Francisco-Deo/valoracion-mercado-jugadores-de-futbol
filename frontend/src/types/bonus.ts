export type StreakDayNumber = 1 | 2 | 3 | 4 | 5 | 6 | 7;

export interface DailyBonusStatus {
  currentStreak: number;
  nextBonus: number;
  alreadyClaimedToday: boolean;
  weeklyBonusRewards: number[];
}

export interface ClaimBonusResponse {
  givenBonus: number;
  currentStreak: number;
  message: string;
}
