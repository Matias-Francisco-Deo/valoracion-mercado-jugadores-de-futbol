import React from 'react';
import { Flame, Sparkles } from 'lucide-react';

interface BonusStreakHeaderProps {
  currentStreak: number;
}

export const BonusStreakHeader: React.FC<BonusStreakHeaderProps> = ({ currentStreak }) => {
  return (
    <div className="flex flex-col items-center text-center gap-1.5 pt-1 sm:pt-2">
      <div className="inline-flex items-center gap-1.5 rounded-full bg-orange-100 px-2.5 py-0.5 sm:px-3 sm:py-1 text-[11px] sm:text-xs font-semibold text-[#d66f00]">
        <Sparkles size={13} className="animate-pulse" />
        <span>RECOMPENSA DIARIA</span>
      </div>

      <h2 id="daily-bonus-title" className="text-xl sm:text-2xl md:text-3xl font-extrabold tracking-tight text-gray-900">
        Bonus de Racha
      </h2>

      <p className="max-w-md text-xs sm:text-sm text-gray-600 px-2">
        Iniciá sesión todos los días para acumular días de racha y desbloquear mayores recompensas en créditos.
      </p>

      {/* Streak indicator badge */}
      <div className="mt-1 sm:mt-2 inline-flex items-center gap-2 rounded-xl bg-gradient-to-r from-orange-500/10 via-amber-500/15 to-orange-500/10 px-3 py-1.5 sm:px-4 sm:py-2 border border-orange-200">
        <Flame className="text-orange-500 fill-orange-500" size={18} />
        <span className="text-xs sm:text-sm font-bold text-gray-800">
          Racha actual: <strong className="text-orange-600 text-sm sm:text-base">{currentStreak}</strong> {currentStreak === 1 ? 'día' : 'días'}
        </span>
      </div>
    </div>
  );
};
