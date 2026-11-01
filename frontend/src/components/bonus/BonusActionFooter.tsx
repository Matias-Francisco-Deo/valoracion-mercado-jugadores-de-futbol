import React from 'react';
import { Check, Gift, Loader2 } from 'lucide-react';
import { Button } from '@/components/ui/Button';

interface BonusActionFooterProps {
  alreadyClaimed: boolean;
  todayRewardAmount: number;
  isLoading: boolean;
  isClaiming: boolean;
  onClaim: () => Promise<unknown>;
  onClose: () => void;
}

export const BonusActionFooter: React.FC<BonusActionFooterProps> = ({
  alreadyClaimed,
  todayRewardAmount,
  isLoading,
  isClaiming,
  onClaim,
  onClose,
}) => {
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
            disabled={isClaiming || isLoading}
            className="w-full py-3 sm:py-3.5 text-sm sm:text-base font-bold bg-brand-orange hover:bg-[#e58600] text-white shadow-lg shadow-orange-500/20 active:scale-[0.99] transition-all flex items-center justify-center gap-2 cursor-pointer disabled:opacity-50"
          >
            {isClaiming ? (
              <>
                <Loader2 size={18} className="animate-spin" />
                Reclamando créditos...
              </>
            ) : (
              <>
                <Gift size={20} />
                Reclamar +${todayRewardAmount} Créditos de Hoy
              </>
            )}
          </Button>
          <span className="text-[11px] text-gray-500">
            La racha se reinicia si pasa más de 1 día calendario (UTC) sin reclamar.
          </span>
        </div>
      )}
    </div>
  );
};
