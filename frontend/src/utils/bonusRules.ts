import type { DailyBonusStatus, DiaDeLaSemana } from '@/types/bonus';

export type DayCardState = 'completed' | 'today' | 'locked';

export interface BonusDayItem {
  key: DiaDeLaSemana;
  label: string;
  short: string;
  dayNumber: number;
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
  key: DiaDeLaSemana;
  label: string;
  short: string;
  dayNumber: number;
}[] = [
  { key: 'LUNES', label: 'Día 1', short: 'Día 1', dayNumber: 1 },
  { key: 'MARTES', label: 'Día 2', short: 'Día 2', dayNumber: 2 },
  { key: 'MIERCOLES', label: 'Día 3', short: 'Día 3', dayNumber: 3 },
  { key: 'JUEVES', label: 'Día 4', short: 'Día 4', dayNumber: 4 },
  { key: 'VIERNES', label: 'Día 5', short: 'Día 5', dayNumber: 5 },
  { key: 'SABADO', label: 'Día 6', short: 'Día 6', dayNumber: 6 },
  { key: 'DOMINGO', label: 'Día 7', short: 'Día 7', dayNumber: 7 },
];

export const DEFAULT_REWARDS: Record<DiaDeLaSemana, number> = {
  LUNES: 1.0,
  MARTES: 2.0,
  MIERCOLES: 2.0,
  JUEVES: 5.0,
  VIERNES: 5.0,
  SABADO: 10.0,
  DOMINGO: 15.0,
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
    const reward = weeklyBonusRewards[day.dayNumber - 1] ?? DEFAULT_REWARDS[day.key];
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
