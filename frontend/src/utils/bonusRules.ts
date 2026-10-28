import type { DailyBonusStatus, StreakDayNumber } from '@/types/bonus';

export type DayCardState = 'completed' | 'today' | 'locked';

export interface BonusDayItem {
  key: StreakDayNumber;
  label: string;
  short: string;
  dayNumber: StreakDayNumber;
  reward: number;
  state: DayCardState;
  isDay7: boolean;
}

export interface BonusProgressCalculation {
  currentStreak: number;
  alreadyClaimed: boolean;
  targetDay: number;
  todayRewardAmount: number;
  days: BonusDayItem[];
}

export const DAYS_CONFIG: readonly {
  key: StreakDayNumber;
  label: string;
  short: string;
  dayNumber: StreakDayNumber;
}[] = [
  { key: 1, label: 'Día 1', short: 'Día 1', dayNumber: 1 },
  { key: 2, label: 'Día 2', short: 'Día 2', dayNumber: 2 },
  { key: 3, label: 'Día 3', short: 'Día 3', dayNumber: 3 },
  { key: 4, label: 'Día 4', short: 'Día 4', dayNumber: 4 },
  { key: 5, label: 'Día 5', short: 'Día 5', dayNumber: 5 },
  { key: 6, label: 'Día 6', short: 'Día 6', dayNumber: 6 },
  { key: 7, label: 'Día 7', short: 'Día 7', dayNumber: 7 },
];

export const DEFAULT_REWARDS: Record<StreakDayNumber, number> = {
  1: 1.0,
  2: 2.0,
  3: 2.0,
  4: 5.0,
  5: 5.0,
  6: 10.0,
  7: 15.0,
};

export function calculateBonusProgress(
  status: DailyBonusStatus | null,
): BonusProgressCalculation {
  const currentStreak = status?.currentStreak ?? 0;
  const alreadyClaimed = status?.alreadyClaimedToday ?? false;
  const weeklyBonusRewards = status?.weeklyBonusRewards ?? [1.0, 2.0, 2.0, 5.0, 5.0, 10.0, 15.0];

  // Calculate target day for today's action (1-7)
  const targetDay = alreadyClaimed
    ? currentStreak
    : currentStreak >= 7 || currentStreak === 0
      ? 1
      : currentStreak + 1;

  const todayRewardAmount =
    status?.nextBonus ?? weeklyBonusRewards[targetDay - 1] ?? 1.0;

  const days: BonusDayItem[] = DAYS_CONFIG.map((day) => {
    const reward = weeklyBonusRewards[day.dayNumber - 1] ?? DEFAULT_REWARDS[day.dayNumber];
    const isCompleted = alreadyClaimed
      ? day.dayNumber <= currentStreak
      : day.dayNumber < targetDay;
    const isToday = !alreadyClaimed && day.dayNumber === targetDay;
    const state: DayCardState = isToday
      ? 'today'
      : isCompleted
        ? 'completed'
        : 'locked';

    return {
      key: day.key,
      label: day.label,
      short: day.short,
      dayNumber: day.dayNumber,
      reward,
      state,
      isDay7: day.dayNumber === 7,
    };
  });

  return {
    currentStreak,
    alreadyClaimed,
    targetDay,
    todayRewardAmount,
    days,
  };
}
