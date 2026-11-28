import React from 'react';
import { Check, Gift, Loader2, Lock } from 'lucide-react';
import { Button } from '@/components/ui/Button';

interface BonusActionFooterProps {
  alreadyClaimed: boolean;
  todayRewardAmount: number;
  isLoading: boolean;
  isClaiming: boolean;
  canClaim?: boolean;
  hasAlternativeMissions?: boolean;
  onClaim: () => Promise<unknown>;
  onClose: () => void;
}

export const BonusActionFooter: React.FC<BonusActionFooterProps> = ({
  alreadyClaimed,
  todayRewardAmount,
  isLoading,
  isClaiming,
  canClaim = true,
  hasAlternativeMissions = false,
  onClaim,
  onClose,
}) => {
  const isButtonDisabled = isClaiming || isLoading || !canClaim;

  return (
    <div className="flex flex-col items-center gap-3 pt-3">
      {alreadyClaimed ? (
        <div className="flex w-full flex-col sm:flex-row items-center justify-between gap-3 rounded-2xl bg-emerald-50/80 border border-emerald-200 p-4">
          <div className="flex items-center gap-2.5">
            <div className="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-emerald-500 text-white">
              <Check size={18} />
            </div>
            <div className="text-left">
              <h4 className="text-xs font-bold text-emerald-950 uppercase tracking-wide">
                ¡Bonus de hoy reclamado!
              </h4>
              <p className="text-xs text-emerald-800">
                Volvé mañana para sumar tu próximo día y no cortar la racha.
              </p>
            </div>
          </div>

          <Button
            type="button"
            onClick={onClose}
            className="w-full sm:w-auto bg-white text-gray-700 border border-gray-300 hover:bg-gray-100 shadow-xs font-semibold px-5 text-sm"
          >
            Cerrar
          </Button>
        </div>
      ) : (
        <div className="flex w-full flex-col items-center gap-2">
          <Button
            type="button"
            onClick={() => void onClaim()}
            disabled={isButtonDisabled}
            className={`w-full py-3 sm:py-3.5 text-sm sm:text-base font-bold text-white shadow-lg active:scale-[0.99] transition-all flex items-center justify-center gap-2 cursor-pointer disabled:opacity-60 disabled:cursor-not-allowed ${
              canClaim
                ? 'bg-brand-orange hover:bg-[#e58600] shadow-orange-500/20'
                : 'bg-gray-400 hover:bg-gray-400 shadow-none'
            }`}
          >
            {isClaiming ? (
              <>
                <Loader2 size={18} className="animate-spin" />
                Reclamando créditos...
              </>
            ) : !canClaim ? (
              <>
                <Lock size={18} />
                {hasAlternativeMissions
                  ? `Completá 1 opción para reclamar +$${todayRewardAmount}`
                  : `Completá la misión para reclamar +$${todayRewardAmount}`}
              </>
            ) : (
              <>
                <Gift size={20} />
                Reclamar +${todayRewardAmount} Créditos de Hoy
              </>
            )}
          </Button>
          <span className="text-[11px] text-gray-500">
            {!canClaim
              ? hasAlternativeMissions
                ? 'Alcanza con completar una de las opciones disponibles para habilitar el reclamo.'
                : 'Debés cumplir la misión requerida para habilitar el reclamo.'
              : 'La racha se reinicia si pasa más de 1 día calendario (UTC) sin reclamar.'}
          </span>
        </div>
      )}
    </div>
  );
};
