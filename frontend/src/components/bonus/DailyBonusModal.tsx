import React, { useEffect } from 'react';
import { X } from 'lucide-react';
import type { DailyBonusStatus } from '@/types/bonus';
import { calculateBonusProgress } from '@/utils/bonusRules';
import { BonusStreakHeader } from './BonusStreakHeader';
import { BonusDayCard } from './BonusDayCard';
import { BonusActionFooter } from './BonusActionFooter';

export interface DailyBonusModalProps {
  isOpen: boolean;
  onClose: () => void;
  status: DailyBonusStatus | null;
  isLoading: boolean;
  isClaiming: boolean;
  onClaim: () => Promise<unknown>;
}

export const DailyBonusModal: React.FC<DailyBonusModalProps> = ({
  isOpen,
  onClose,
  status,
  isLoading,
  isClaiming,
  onClaim,
}) => {
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && !isClaiming) {
        onClose();
      }
    };
    if (isOpen) {
      window.addEventListener('keydown', handleKeyDown);
    }
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, isClaiming, onClose]);

  if (!isOpen) return null;

  const { currentStreak, alreadyClaimed, todayRewardAmount, days } =
    calculateBonusProgress(status);

  return (
    <div
      role="dialog"
      aria-modal="true"
      aria-labelledby="daily-bonus-title"
      className="fixed inset-0 z-50 flex items-center justify-center bg-[rgba(12,29,11,0.7)] p-4 backdrop-blur-sm animate-in fade-in duration-200"
      onClick={(e) => {
        if (e.target === e.currentTarget && !isClaiming) {
          onClose();
        }
      }}
    >
      <div className="relative flex w-full max-w-2xl max-h-[92vh] overflow-y-auto flex-col gap-4 sm:gap-5 rounded-2xl sm:rounded-3xl bg-white p-4 sm:p-6 md:p-8 text-black shadow-2xl border border-orange-100">
        {/* Close Button */}
        <button
          type="button"
          onClick={onClose}
          disabled={isClaiming}
          className="absolute right-3 top-3 sm:right-4 sm:top-4 rounded-full p-2 text-gray-400 hover:bg-gray-100 hover:text-gray-700 transition cursor-pointer disabled:opacity-50 z-20"
          aria-label="Cerrar modal"
        >
          <X size={20} />
        </button>

        {/* Header */}
        <BonusStreakHeader currentStreak={currentStreak} />

        {/* Daily Progress Cards */}
        <div className="grid grid-cols-4 sm:grid-cols-7 gap-1.5 sm:gap-2 md:gap-2.5 pt-1 sm:pt-2">
          {days.map((day) => (
            <BonusDayCard key={day.key} day={day} />
          ))}
        </div>

        {/* Footer / Call To Action */}
        <BonusActionFooter
          alreadyClaimed={alreadyClaimed}
          todayRewardAmount={todayRewardAmount}
          isLoading={isLoading}
          isClaiming={isClaiming}
          onClaim={onClaim}
          onClose={onClose}
        />
      </div>
    </div>
  );
};
