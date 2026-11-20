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
}

export const MissionTreeNode: React.FC<MissionTreeNodeProps> = ({
  mission,
  depth = 0,
  onClose,
  compact = false,
}) => {
  if (mission.tipoMisionDTO === 'OBJECTIVE' || !mission.misiones || mission.misiones.length === 0) {
    return <MissionLeafCard mission={mission} onClose={onClose} compact={compact} />;
  }

  const isOr = mission.tipoMisionDTO === 'OR';
  const isCompleted = mission.completed;
  const areAllLeaves =
    mission.misiones.length > 1 &&
    mission.misiones.every(
      (sub) => sub.tipoMisionDTO === 'OBJECTIVE' || !sub.misiones || sub.misiones.length === 0
    );

  return (
    <div
      className={cn(
        'flex flex-col gap-2 rounded-2xl p-2.5 sm:p-3 border transition-all',
        isCompleted
          ? 'bg-emerald-50/40 border-emerald-200/80'
          : 'bg-gray-50/70 border-gray-200',
        depth > 0 && 'ml-1 sm:ml-2 border-l-2'
      )}
    >
      <div className="flex items-center justify-between gap-2">
        <div className="flex items-center gap-2 min-w-0">
          {isOr ? (
            <Split size={15} className={isCompleted ? 'text-emerald-600' : 'text-amber-500'} />
          ) : (
            <Layers size={15} className={isCompleted ? 'text-emerald-600' : 'text-blue-500'} />
          )}
          <span className="text-xs sm:text-sm font-bold text-gray-800 truncate">
            {mission.nombre}
          </span>
        </div>

        <span
          className={cn(
            'text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider shrink-0',
            isOr
              ? 'bg-amber-100 text-amber-800 border border-amber-200'
              : 'bg-blue-100 text-blue-800 border border-blue-200'
          )}
        >
          {isOr ? 'Al menos una' : 'Todas requeridas'}
        </span>
      </div>

      <div
        className={cn(
          'pt-1',
          areAllLeaves
            ? 'grid grid-cols-1 sm:grid-cols-3 gap-2'
            : 'flex flex-col gap-2'
        )}
      >
        {mission.misiones.map((subMission, index) => (
          <MissionTreeNode
            key={subMission.id ?? `${depth}-${index}`}
            mission={subMission}
            depth={depth + 1}
            onClose={onClose}
            compact={areAllLeaves}
          />
        ))}
      </div>
    </div>
  );
};
