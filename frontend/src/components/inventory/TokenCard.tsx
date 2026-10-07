import { DollarSign, Shield } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { cn } from '@/lib/utils';

interface TokenCardProps {
  playerName: string;
  tokens: number;
  tokensLabel: string;
  pricePerToken: number;
  actionButtonText: string;
  onActionClick: () => void;
  className?: string;
}

export const TokenCard: React.FC<TokenCardProps> = ({
  playerName,
  tokens,
  tokensLabel,
  pricePerToken,
  actionButtonText,
  onActionClick,
  className,
}) => {
  return (
    <article
      className={cn(
        'flex min-w-0 flex-col rounded-2xl border border-gray-500 bg-gray-400 shadow-lg transition-transform hover:-translate-y-1 hover:shadow-xl',
        className,
      )}
    >
      <div className="flex flex-col items-center gap-1 px-4 pb-4 pt-5 text-center">
        <div className="flex h-12 w-12 items-center justify-center rounded-full bg-gray-500/40 text-gray-900">
          <Shield className="h-6 w-6" aria-hidden="true" />
        </div>
        <h3 className="break-words text-xl font-bold uppercase tracking-wide text-gray-900">
          {playerName}
        </h3>
      </div>

      <div className="mt-auto grid grid-cols-2 gap-2 border-t border-gray-500/60 bg-gray-500/20 p-3">
        <div className="flex min-w-0 flex-col items-center rounded-lg px-2 py-3 text-center">
          <span className="text-xs font-semibold uppercase text-gray-700">Tokens</span>
          <span className="mt-1 break-words text-lg font-bold text-gray-900">{tokens}</span>
          <span className="text-xs font-medium text-gray-700">{tokensLabel}</span>
        </div>
        <div className="flex min-w-0 flex-col items-center rounded-lg px-2 py-3 text-center">
          <span className="text-xs font-semibold uppercase text-gray-700">Precio</span>
          <span className="mt-1 flex items-center text-lg font-bold text-gray-900">
            <DollarSign className="h-4 w-4" aria-hidden="true" />
            {pricePerToken}
          </span>
          <span className="text-xs font-medium text-gray-700">por token</span>
        </div>
      </div>

      <div className="p-3">
        <Button
          className="w-full break-words px-2 py-2.5 text-sm"
          onClick={onActionClick}
          aria-label={`${actionButtonText}: ${playerName}`}
        >
          {actionButtonText}
        </Button>
      </div>
    </article>
  );
};
