import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { Button } from '@/components/ui/Button';
import { depositCredits } from '@/services/userService';
import { useAuth } from '@/hooks/useAuth';
import type { HttpError } from '@/lib/http-error';

interface DepositCreditsModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export const DepositCreditsModal: React.FC<DepositCreditsModalProps> = ({ isOpen, onClose }) => {
  const [amount, setAmount] = useState<string>('');
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const { refreshUser } = useAuth();

  if (!isOpen) return null;

  const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    const parsedAmount = Number(amount);

    if (isNaN(parsedAmount) || parsedAmount <= 0) {
      setError('El monto a depositar debe ser mayor a cero.');
      return;
    }

    if (parsedAmount >= 10000) {
      setError('El monto a depositar debe ser menor a diez mil.');
      return;
    }
    setIsLoading(true);
    setError(null);

    try {
      await depositCredits(parsedAmount);
      await refreshUser();
      toast.success(`Se acreditaron $${parsedAmount} a tu cuenta.`);
      setAmount('');
      onClose();
    } catch (err) {
      const httpErr = err as HttpError;
      const errorMessage = httpErr?.message || 'Ocurrió un error al procesar el depósito.';
      setError(errorMessage);
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  const handleClose = () => {
    if (!isLoading) {
      setAmount('');
      setError(null);
      onClose();
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-[rgba(12,29,11,0.7)] p-4 backdrop-blur-sm">
      <div className="flex w-full max-w-md flex-col gap-4 rounded-2xl bg-white p-6 text-black shadow-lg">
        <div className="flex flex-col gap-1">
          <h3 className="text-[11px] font-semibold tracking-wider text-[#d66f00] uppercase">
            Billetera
          </h3>
          <h2 className="text-2xl font-bold">Cargar Créditos</h2>
          <span className="text-sm text-gray-600">
            Ingresá el monto de créditos que deseás depositar en tu cuenta.
          </span>
        </div>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4">
          <div className="flex flex-col gap-2">
            <label htmlFor="deposit-amount" className="text-sm font-medium text-gray-700">
              Monto ($)
            </label>
            <input
              id="deposit-amount"
              type="number"
              min={1}
              max={10000}
              step="any"
              value={amount}
              onChange={(e) => {
                setAmount(e.target.value);
                setError(null);
              }}
              placeholder="Ej: 500"
              autoFocus
              disabled={isLoading}
              className="w-full rounded-xl border border-gray-300 px-4 py-2.5 text-lg outline-none transition focus:border-brand-orange focus:ring-2 focus:ring-brand-orange/20"
            />
            {error && <p className="text-xs text-red-500 font-medium">{error}</p>}
          </div>

          <div className="flex items-center justify-end gap-3 pt-2">
            <Button
              type="button"
              onClick={handleClose}
              disabled={isLoading}
              className="bg-transparent border border-gray-300 text-gray-700 hover:bg-gray-100 shadow-none font-medium"
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              disabled={isLoading || !amount}
              className="text-white hover:bg-[#d66f00] shadow-md font-medium"
            >
              {isLoading ? 'Cargando...' : 'Depositar'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
};
