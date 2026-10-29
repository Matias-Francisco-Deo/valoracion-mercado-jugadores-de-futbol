import React from 'react';
import { Flame } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import type { DailyBonusStatus } from '@/types/bonus';
import { cn } from '@/lib/utils';

export interface LoginStreakProps extends React.ComponentProps<"button"> {
  status?: DailyBonusStatus | null;
  className?: string;
}

export const LoginStreak: React.FC<LoginStreakProps> = ({status,onClick,className,...props}) => {
  const { isAuthenticated } = useAuth();

  if (!isAuthenticated) {
    return null;
  }

  return (
    <button type="button" onClick={onClick}
      className={cn(
        "relative flex items-center gap-1.5 rounded-lg bg-black/10 hover:bg-black/20 px-3 py-1 text-black transition-all cursor-pointer active:scale-95",
        className
      )}
      title="Ver progreso de racha diaria"
      {...props}>
      <Flame size={16} className={cn("shrink-0",
          status?.alreadyClaimedToday
            ? "text-orange-600 fill-orange-500"
            : "text-red-600 fill-red-500 animate-pulse"
        )}/>

      <div className="flex flex-col items-start leading-tight min-w-0">
        <span className="text-[10px] font-medium uppercase tracking-wider text-black/70">
          Racha
        </span>
        <span className="text-sm font-bold truncate">
          {status?.currentStreak ?? 0} {status?.currentStreak === 1 ? 'día' : 'días'}
        </span>
      </div>

      {!status?.alreadyClaimedToday && (
        <span className="absolute -top-1 -right-1 flex h-2.5 w-2.5">
          <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
          <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-red-500"></span>
        </span>
      )}
    </button>
  );
};
