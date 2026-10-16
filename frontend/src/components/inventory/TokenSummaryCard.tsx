import { Shield } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import { cn } from '@/lib/utils';
import type { ComponentProps } from 'react';
import type {InventoryToken, OrderSell, TokenSummary } from '@/types/inventory';

type  TokenSummaryCardProps = ComponentProps<"div"> & {
    token:TokenSummary | OrderSell;
    onActionClick?: (data: InventoryToken) => void;
    actionType: 'Vender' | 'Cancelar venta' | 'Comprar';
}

export const TokenSummaryCard = ({ token, actionType, onActionClick, className, ...props }: TokenSummaryCardProps) => {
  const isOrderSell = "id" in token;

  return (
    <div className={cn(
        'flex min-w-0 flex-col max-w-67 rounded-2xl border border-gray-500 bg-gray-400 shadow-lg transition-transform hover:-translate-y-1 hover:shadow-xl text-black',
        className)} {...props}>
      <div className="flex flex-col items-center px-4 py-4 text-center">
        <div className="flex h-12 w-12 items-center justify-center rounded-full bg-gray-500/40">
          <Shield className="h-6 w-6" aria-hidden="true" />
        </div>
        <h3 className="text-xl font-titles font-bold uppercase ">
          {token.playerName}
        </h3>
      </div>

      <div className='border-t border-gray-500/60 bg-gray-500/20 p-3'>
        <div className="grid grid-cols-2 gap-2 border-b border-gray-500/60">

          <div className="flex min-w-0 flex-col text-start ">
            <span className="text-[10px] font-semibold uppercase text-gray-700">Precio</span>
            <span className="flex items-center text-md sm:text-lg font-bold text-gray-900">
              ${token.unitPrice }
            </span>
            <span className="text-[10px] font-medium text-gray-700">por token</span>
          </div>
      
          <div className="flex min-w-0 flex-col items-center ">
            <span className="text-[10px] font-semibold uppercase text-gray-700">Tokens</span>
            <span className=" text-md sm:text-lg font-bold ">{token.quantity}</span>
          </div>
        </div>
        
        {isOrderSell && (
            <span className="mt-2 text-sm font-bold text-gray-900">
              Total: ${token.totalPrice}
            </span>
        )}
      </div>

      {actionType!=='Cancelar venta' && onActionClick &&(
        <div className="p-3">
          <Button className="w-full py-2.5 text-sm text-white"
            onClick={()=>onActionClick(token)}
            aria-label={`${actionType}: ${token.playerName}`}>
            {actionType}
          </Button>
        </div>
      )
        
      }
      
    </div>
  );
};
