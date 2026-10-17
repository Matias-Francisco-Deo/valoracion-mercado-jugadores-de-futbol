import { useState, type ComponentProps } from 'react';
import { Minus, Plus } from 'lucide-react';
import { Button } from '@/components/ui/Button';
import type { TokenSummary } from '@/types/inventory';

type TokenActionModalProps = ComponentProps<"div"> &{
  token:TokenSummary;
  onConfirm: (playerId:number,quantity:number) => Promise<void>;
  onClose: () => void;
  actionType?: 'Vender' | 'Retirar' | 'Comprar';
  title: string;
  error?:string;
}

export const TokenActionModal= ({ token, onConfirm, onClose, actionType, error,...props}: TokenActionModalProps) => {
  const [quantity, setQuantity] = useState(1);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = () =>  {
    setIsSubmitting(true);
    onConfirm( token.playerId, quantity )
    .finally(()=>{
      setIsSubmitting(false);
    }
    );
  }


  return (
    <div className="fixed p-6 inset-0 z-50 flex items-center justify-center bg-[rgba(12,29,11,0.7)] backdrop-blur-sm" {...props}>
      <div className="flex flex-col bg-white text-black rounded-2xl p-6 shadow-lg gap-4 md:min-w-100">
        <h3 className="text-[#d66f00] text-[10px] uppercase">{props.title}</h3>
        <h2 className="text-2xl">{actionType} {token.playerName} token</h2>
        <span className='text-sm'>Tokens disponibles {quantity}</span>
        <div className='flex items-center justify-around p-4 border border-gray-300 rounded-2xl '>
          <Button className='rounded-full p-4 border-hover-orange bg-[#ffc977]' disabled={quantity==1} onClick={()=>setQuantity(quantity-1)}>
            <Minus size={10}/>
          </Button>
          <span className='text-4xl'>{quantity}</span>
          <Button className='rounded-full p-4 border border-hover-orange bg-[#ffc977]' disabled={quantity==token.quantity} onClick={()=>setQuantity(quantity+1)}>
            <Plus size={10}/>
          </Button>
        </div>
        <p className='self-center text-destructive'>{error ?? ""}</p>
        <div className='flex justify-between'>
          <span className="text-sm">Precio: </span>
          <span> ${token.unitPrice .toFixed(2)}</span>
        </div>
        <div className='flex justify-between border-b pb-1 border-gray-300'>
          <span >Precio total: </span>
          <span className="text-lg text-[#d66f00] ">${(token.unitPrice  * quantity).toFixed(2)}</span>
        </div>

        <div className="flex items-center gap-4 justify-around">
          <Button onClick={onClose} className="bg-transparent border border-gray-200 text-gray-500 hover:bg-gray-700 hover:text-white shadow-md">
            Cancelar
          </Button>
          <Button onClick={handleSubmit} disabled={isSubmitting}
          className="text-white hover:bg-[#d66f00] shadow-md">
            {actionType}
          </Button>
        </div>
      </div>
      


    </div>
  );
};
