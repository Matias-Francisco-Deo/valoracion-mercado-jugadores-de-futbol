import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { CheckCircle2, Target, ExternalLink, ArrowRight } from 'lucide-react';
import type { MissionStatusResponse } from '@/types/bonus';
import { getPlayerByName } from '@/services/PlayerService';
import { cn } from '@/lib/utils';

export interface MissionLeafCardProps {
  mission: MissionStatusResponse;
  onClose?: () => void;
  compact?: boolean;
  isOptional?: boolean;
  className?: string;
}

export const MissionLeafCard: React.FC<MissionLeafCardProps> = ({
  mission,
  onClose,
  compact = false,
  isOptional = false,
  className,
}) => {
  const navigate = useNavigate();
  const [playerId, setPlayerId] = useState<number | null>(null);

  const isCompleted = mission.completed;
  const isOptionalPending = isOptional && !isCompleted;
  const isVisitPlayer = /^visitar perfil de\s+/i.test(mission.nombre);
  const isMarketMission = /comprar.*token|mercado/i.test(mission.nombre);
  const rawPlayerName = isVisitPlayer ? mission.nombre.replace(/^visitar perfil de\s+/i, '').trim() : '';

  useEffect(() => {
    if (!isVisitPlayer || !rawPlayerName) return;

    let isMounted = true;
    getPlayerByName(rawPlayerName).then((player) => {
      if (isMounted && player?.id) {
        setPlayerId(player.id);
      }
    });

    return () => {
      isMounted = false;
    };
  }, [rawPlayerName, isVisitPlayer]);

  const isNavigable = (isVisitPlayer && playerId !== null) || isMarketMission;

  const handleClick = () => {
    if (isVisitPlayer && playerId !== null) {
      onClose?.();
      navigate(`/p/${playerId}`);
    } else if (isMarketMission) {
      onClose?.();
      navigate('/market');
    }
  };

  const displayName = compact && isVisitPlayer && rawPlayerName ? rawPlayerName : mission.nombre;

  return (
    <div
      onClick={isNavigable ? handleClick : undefined}
      role={isNavigable ? 'button' : undefined}
      tabIndex={isNavigable ? 0 : undefined}
      onKeyDown={
        isNavigable
          ? (e) => {
              if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                handleClick();
              }
            }
          : undefined
      }
      className={cn(
        'group flex items-center justify-between gap-2.5 rounded-xl border transition-all text-left',
        compact ? 'p-2 sm:p-2.5 min-h-[48px]' : 'p-2.5 sm:p-3',
        isCompleted
          ? 'bg-emerald-50/70 border-emerald-200 text-emerald-950'
          : isOptionalPending
            ? 'bg-gray-50/70 border-dashed border-gray-200 text-gray-500 opacity-75'
            : 'bg-white border-gray-200 text-gray-800 hover:border-orange-300 shadow-xs',
        isNavigable &&
          (isOptionalPending
            ? 'cursor-pointer hover:opacity-100 hover:border-gray-300'
            : 'cursor-pointer hover:shadow-md hover:bg-orange-50/30'),
        className
      )}
    >
      <div className="flex items-center gap-2 min-w-0">
        <div
          className={cn(
            'flex shrink-0 items-center justify-center rounded-full transition-transform',
            compact ? 'h-6 w-6' : 'h-7 w-7',
            isCompleted
              ? 'bg-emerald-500 text-white'
              : isOptionalPending
                ? 'bg-gray-100 text-gray-400'
                : 'bg-orange-100 text-orange-600 group-hover:scale-110'
          )}
        >
          {isCompleted ? (
            <CheckCircle2 size={compact ? 14 : 16} />
          ) : (
            <Target size={compact ? 14 : 16} />
          )}
        </div>

        <div className="flex flex-col min-w-0">
          <span
            className={cn(
              'font-semibold truncate flex items-center gap-1.5',
              compact ? 'text-xs sm:text-xs' : 'text-xs sm:text-sm',
              isCompleted
                ? 'line-through text-emerald-900/70'
                : isOptionalPending
                  ? 'text-gray-600'
                  : 'text-gray-900 group-hover:text-orange-600'
            )}
            title={mission.nombre}
          >
            {displayName}
            {isNavigable && !isCompleted && (
              <ExternalLink
                size={11}
                className={cn(
                  'opacity-0 group-hover:opacity-100 transition-opacity shrink-0',
                  isOptionalPending ? 'text-gray-400' : 'text-orange-500'
                )}
              />
            )}
          </span>
          {!compact && (
            <span className="text-[11px] text-gray-500">
              {isCompleted
                ? 'Completada'
                : isOptionalPending
                  ? 'Opcional — reto ya cumplido'
                  : isNavigable
                    ? 'Clic para ir a cumplirla'
                    : 'Pendiente'}
            </span>
          )}
        </div>
      </div>

      <div className="flex items-center gap-1.5 shrink-0">
        {isCompleted ? (
          <span
            className={cn(
              'inline-flex items-center rounded-full font-bold bg-emerald-100 text-emerald-800',
              compact ? 'px-1.5 py-0.5 text-[9px]' : 'px-2 py-0.5 text-[10px]'
            )}
          >
            Listo
          </span>
        ) : isOptionalPending ? (
          <span
            className={cn(
              'inline-flex items-center rounded-full font-medium bg-gray-100 text-gray-500',
              compact ? 'px-1.5 py-0.5 text-[10px]' : 'px-2 py-0.5 text-[10px]'
            )}
          >
            Opcional
          </span>
        ) : (
          <span
            className={cn(
              'inline-flex items-center gap-1 rounded-full font-medium transition-colors',
              compact ? 'px-1.5 py-0.5 text-[10px]' : 'px-2 py-0.5 text-[10px]',
              isNavigable
                ? 'bg-orange-100 text-orange-800 group-hover:bg-orange-500 group-hover:text-white'
                : 'bg-gray-100 text-gray-600'
            )}
          >
            {isNavigable ? 'Ir' : 'Por hacer'}
            {isNavigable && <ArrowRight size={compact ? 9 : 10} />}
          </span>
        )}
      </div>
    </div>
  );
};

