import React from 'react';
import { Split, Layers } from 'lucide-react';
import type { MissionStatusResponse } from '@/types/bonus';
import { MissionLeafCard } from './MissionLeafCard';
import { cn } from '@/lib/utils';

export interface MissionTreeNodeProps {
  mission: MissionStatusResponse;
  depth?: number;
  onClose?: () => void;
  compact?: boolean;
  isOptional?: boolean;
}

const OrDivider: React.FC = () => (
  <div className="relative flex items-center justify-center py-1" aria-hidden="true">
    <div className="w-full border-t border-dashed border-amber-300" />
    <span className="absolute bg-white px-2.5 py-0.5 text-[10px] font-extrabold uppercase tracking-widest text-amber-700 border border-amber-200 rounded-full shadow-2xs">
      O
    </span>
  </div>
);

export const MissionTreeNode: React.FC<MissionTreeNodeProps> = ({
  mission,
  depth = 0,
  onClose,
  compact = false,
  isOptional = false,
}) => {
  if (mission.tipoMisionDTO === 'OBJECTIVE' || !mission.misiones || mission.misiones.length === 0) {
    return (
      <MissionLeafCard
        mission={mission}
        onClose={onClose}
        compact={compact}
        isOptional={isOptional}
      />
    );
  }

  const isOr = mission.tipoMisionDTO === 'OR';
  const isCompleted = mission.completed;
  const isOptionalBranch = isOptional && !isCompleted;
  const completedCount = mission.misiones.filter((sub) => sub.completed).length;
  const totalCount = mission.misiones.length;
  const areAllLeaves =
    !isOr &&
    totalCount > 1 &&
    mission.misiones.every(
      (sub) => sub.tipoMisionDTO === 'OBJECTIVE' || !sub.misiones || sub.misiones.length === 0
    );

  const orBadgeLabel = isCompleted
    ? 'Reto cumplido'
    : totalCount === 2
      ? 'Elegí 1 de 2'
      : 'Elegí 1 opción';

  const andBadgeLabel = isOptionalBranch
    ? `Opcional (${completedCount}/${totalCount})`
    : isCompleted
      ? `Completada (${completedCount}/${totalCount})`
      : `Todas (${completedCount}/${totalCount})`;

  return (
    <div
      className={cn(
        'flex flex-col gap-2.5 rounded-2xl p-3 border transition-all',
        isCompleted
          ? 'bg-emerald-50/40 border-emerald-200/80'
          : isOptionalBranch
            ? 'bg-gray-50/50 border-dashed border-gray-200 opacity-75'
            : isOr
              ? 'bg-amber-50/20 border-amber-200/70'
              : 'bg-gray-50/70 border-gray-200',
        depth > 0 && 'border-l-2'
      )}
    >
      <div className="flex items-center justify-between gap-2">
        <div className="flex items-center gap-2 min-w-0">
          {isOr ? (
            <Split size={15} className={isCompleted ? 'text-emerald-600' : 'text-amber-500'} />
          ) : (
            <Layers
              size={15}
              className={
                isCompleted
                  ? 'text-emerald-600'
                  : isOptionalBranch
                    ? 'text-gray-400'
                    : 'text-blue-500'
              }
            />
          )}
          <span
            className={cn(
              'text-xs sm:text-sm font-bold truncate',
              isOptionalBranch ? 'text-gray-500' : 'text-gray-800'
            )}
          >
            {mission.nombre}
          </span>
        </div>

        <span
          className={cn(
            'text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider shrink-0 border',
            isCompleted
              ? 'bg-emerald-100 text-emerald-800 border-emerald-200'
              : isOptionalBranch
                ? 'bg-gray-100 text-gray-500 border-gray-200'
                : isOr
                  ? 'bg-amber-100 text-amber-800 border-amber-200'
                  : 'bg-blue-100 text-blue-800 border-blue-200'
          )}
        >
          {isOr ? orBadgeLabel : andBadgeLabel}
        </span>
      </div>

      {isOr ? (
        <div className="flex flex-col gap-2 pt-0.5">
          {mission.misiones.map((subMission, index) => {
            const isChildOptional = isOptionalBranch || (isCompleted && !subMission.completed);

            return (
              <React.Fragment key={subMission.id ?? `${depth}-${index}`}>
                {index > 0 && <OrDivider />}
                <div className="flex flex-col gap-1.5">
                  <div className="flex items-center justify-between px-1">
                    <span className="text-[11px] font-bold uppercase tracking-wider text-gray-500">
                      Opción {index + 1}
                    </span>
                    {subMission.completed ? (
                      <span className="text-[10px] font-bold text-emerald-700">
                        Reto completado
                      </span>
                    ) : isCompleted ? (
                      <span className="text-[10px] font-medium text-gray-400">
                        Opcional (reto ya cumplido)
                      </span>
                    ) : null}
                  </div>
                  <MissionTreeNode
                    mission={subMission}
                    depth={0}
                    onClose={onClose}
                    isOptional={isChildOptional}
                  />
                </div>
              </React.Fragment>
            );
          })}
        </div>
      ) : (
        <div
          className={cn(
            'pt-0.5',
            areAllLeaves ? 'grid grid-cols-1 sm:grid-cols-3 gap-2' : 'flex flex-col gap-2'
          )}
        >
          {mission.misiones.map((subMission, index) => (
            <MissionTreeNode
              key={subMission.id ?? `${depth}-${index}`}
              mission={subMission}
              depth={depth + 1}
              onClose={onClose}
              compact={areAllLeaves}
              isOptional={isOptionalBranch}
            />
          ))}
        </div>
      )}
    </div>
  );
};
