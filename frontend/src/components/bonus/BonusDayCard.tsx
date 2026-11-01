import React from 'react';
import { Check, Gift, Flame, Lock } from 'lucide-react';
import type { BonusDayItem } from '@/utils/bonusRules';

interface BonusDayCardProps {
  day: BonusDayItem;
}

export const BonusDayCard: React.FC<BonusDayCardProps> = ({ day }) => {
  const isToday = day.state === 'today';
  const isCompleted = day.state === 'completed';
  const isDay7 = day.isDay7;

  return (
    <div
      className={`relative flex flex-col items-center justify-between rounded-xl sm:rounded-2xl p-2 sm:p-2.5 md:p-3 border transition-all duration-200 ${
        isToday
          ? 'border-brand-orange bg-gradient-to-b from-orange-50 to-amber-100/50 shadow-md ring-2 ring-brand-orange/40 scale-[1.02] sm:scale-105 z-10'
          : isCompleted
            ? 'border-emerald-200 bg-emerald-50/60 text-emerald-900'
            : 'border-gray-200 bg-gray-50/70 text-gray-400'
      } ${isDay7 ? 'col-span-2 sm:col-span-1 border-amber-300' : ''}`}
    >
      {/* Day label */}
      <span className="text-[10px] sm:text-[11px] font-bold tracking-tight text-gray-600 uppercase">
        {day.label}
      </span>

      <div className="my-1 sm:my-2 flex h-8 w-8 sm:h-9 sm:w-9 items-center justify-center rounded-full bg-white shadow-xs">
        {isCompleted ? (
          <Check className="text-emerald-600" size={16} strokeWidth={2.5} />
        ) : isToday ? (
          <Gift className="text-brand-orange animate-bounce" size={16} />
        ) : isDay7 ? (
          <Flame className="text-amber-500" size={16} />
        ) : (
          <Lock className="text-gray-300" size={14} />
        )}
      </div>

      {/* Amount */}
      <div className="flex flex-col items-center leading-tight">
        <span
          className={`text-xs sm:text-sm font-extrabold ${
            isToday
              ? 'text-brand-orange text-sm sm:text-base'
              : isCompleted
                ? 'text-emerald-700'
                : 'text-gray-500'
          }`}
        >
          +${day.reward}
        </span>
        <span className="text-[8px] sm:text-[9px] text-gray-400 font-medium">créditos</span>
      </div>

      {/* Badges */}
      {isToday && (
        <span className="mt-1 sm:mt-1.5 rounded-full bg-brand-orange px-1.5 sm:px-2 py-0.5 text-[8px] sm:text-[9px] font-bold text-white uppercase tracking-wider">
          Hoy
        </span>
      )}
      {isCompleted && (
        <span className="mt-1 sm:mt-1.5 rounded-full bg-emerald-100 px-1.5 sm:px-2 py-0.5 text-[8px] sm:text-[9px] font-semibold text-emerald-700">
          Listo
        </span>
      )}
      {!isToday && !isCompleted && isDay7 && (
        <span className="mt-1 sm:mt-1.5 rounded-full bg-amber-100 px-1 sm:px-1.5 py-0.5 text-[7px] sm:text-[8px] font-semibold text-amber-700">
          Premio
        </span>
      )}
    </div>
  );
};
