export type DiaDeLaSemana =
  | 'LUNES'
  | 'MARTES'
  | 'MIERCOLES'
  | 'JUEVES'
  | 'VIERNES'
  | 'SABADO'
  | 'DOMINGO';

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
