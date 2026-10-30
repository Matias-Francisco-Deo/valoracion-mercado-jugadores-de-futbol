import React from 'react';
import { Plus } from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import type { UserProfile } from '@/types/auth';
import { cn } from '@/lib/utils';

export interface CreditsSectionProps extends React.ComponentProps<"div"> {
  user?: UserProfile | null;
  onDepositClick?: () => void;
  className?: string;
}

export const CreditsSection: React.FC<CreditsSectionProps> = ({
  user: propUser,
  onDepositClick,
  className,
  ...props
}) => {
  const { user: authUser, isAuthenticated } = useAuth();
  const user = propUser ?? authUser;

  if (!isAuthenticated || !user) {
    return null;
  }

  return (
    <div  className={cn( "flex items-center gap-2 rounded-lg bg-black/10 px-3 py-1 text-black",className)} {...props}>
      <div className="flex flex-col items-start leading-tight min-w-0">
        <span className="text-[10px] font-medium uppercase tracking-wider text-black/70">
          Créditos
        </span>
        <span className="text-sm font-bold truncate max-w-20">
          ${user.creditBalance}
        </span>
      </div>
      <button type="button" onClick={onDepositClick}
        className="flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-black text-white hover:bg-black/80 transition-transform active:scale-95 cursor-pointer"
        title="Cargar créditos">
        <Plus size={14} />
      </button>
    </div>
  );
};