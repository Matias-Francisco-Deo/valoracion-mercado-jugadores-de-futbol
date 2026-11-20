export type StreakDayNumber = 1 | 2 | 3 | 4 | 5 | 6 | 7;

export type TipoMision = 'AND' | 'OR' | 'COMPOSITE' | 'OBJECTIVE';

export interface MissionStatusResponse {
  id?: number;
  nombre: string;
  tipoMisionDTO: TipoMision;
  completed: boolean;
  misiones: MissionStatusResponse[];
}

export interface DailyBonusStatus {
  currentStreak: number;
  nextBonus: number;
  alreadyClaimedToday: boolean;
  weeklyBonusRewards: number[];
  puedeReclamar?: boolean;
  misiones?: MissionStatusResponse | null;
}

export interface ClaimBonusResponse {
  givenBonus: number;
  currentStreak: number;
  message: string;
}

