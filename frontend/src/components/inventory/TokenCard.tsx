import { DollarSign, Shield } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { cn } from '@/lib/utils';
import type { ComponentProps } from 'react';
import type { Token } from '@/types/inventory';

type  TokenCardProps = ComponentProps<"div"> & {
    token:Token;
    onActionClick: () => void;
    actionType: 'Vender' | 'Cancelar venta' | 'Comprar';
}

export const TokenCard = ({ token, actionType, onActionClick, className, ...props }: TokenCardProps) => {
  return (
    <div className={cn(
        'flex min-w-0 flex-col max-w-67 rounded-2xl border border-gray-500 bg-gray-400 shadow-lg transition-transform hover:-translate-y-1 hover:shadow-xl text-black',
        className,)} {...props}>
      <div className="flex flex-col items-center px-4 py-4 text-center">
        <div className="flex h-12 w-12 items-center justify-center rounded-full bg-gray-500/40">
          <Shield className="h-6 w-6" aria-hidden="true" />
        </div>
        <h3 className="text-xl font-bold uppercase ">
          {token.playerName}
        </h3>
      </div>

      <div className="mt-auto grid grid-cols-1 min-[300px]:grid-cols-2 gap-2 border-t border-gray-500/60 bg-gray-500/20 p-3">
      
        <div className="flex min-w-0 flex-col items-center p-3 text-center">
          <span className="text-xs font-semibold uppercase text-gray-700">Tokens</span>
          <span className="mt-1 text-lg font-bold ">{token.quantity}</span>
          <span className="text-xs font-medium text-gray-700">{token.selling ? 'En venta' : 'Disponible'}</span>
        </div>

        <div className="flex min-w-0 flex-col items-center p-3 text-center">
          <span className="text-xs font-semibold uppercase text-gray-700">Precio</span>
          <span className="mt-1 flex items-center text-lg font-bold text-gray-900">
            <DollarSign className="h-4 w-4" aria-hidden="true" />
            {token.currentPrice}
          </span>
          <span className="text-xs font-medium text-gray-700">por token</span>
        </div>

      </div>

      <div className="p-3">
        <Button className="w-full py-2.5 text-sm text-white"
          onClick={onActionClick}
          aria-label={`${actionType}: ${token.playerName}`}>
          {actionType}
        </Button>
      </div>
    </div>
  );
};
