import { useEffect, useMemo, useState } from 'react';
import { DollarSign, Minus, Plus, X } from 'lucide-react';
import { Button } from '@/components/ui/Button';

interface TokenActionModalProps {
  isOpen: boolean;
  title: string;
  playerName: string;
  unitPrice: number;
  maxTokens: number;
  confirmText: string;
  actionType: 'list' | 'delist';
  onConfirm: (quantity: number) => Promise<void> | void;
  onClose: () => void;
  isLoading?: boolean;
}

export const TokenActionModal: React.FC<TokenActionModalProps> = ({
  isOpen,
  title,
  playerName,
  unitPrice,
  maxTokens,
  confirmText,
  actionType,
  onConfirm,
  onClose,
  isLoading = false,
}) => {
  const [quantity, setQuantity] = useState('1');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    if (!isOpen) return;

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') onClose();
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [isOpen, onClose]);

  const parsedQuantity = Number(quantity);
  const total = useMemo(
    () => (Number.isInteger(parsedQuantity) && parsedQuantity > 0 ? parsedQuantity * unitPrice : 0),
    [parsedQuantity, unitPrice],
  );
  const isInvalid = !Number.isInteger(parsedQuantity) || parsedQuantity < 1 || parsedQuantity > maxTokens;

  if (!isOpen) return null;

  const validateQuantity = () => {
    if (quantity.trim() === '') {
      setError('Debe ingresar al menos 1 token');
      return false;
    }
    if (!Number.isInteger(parsedQuantity)) {
      setError('Solo se permiten números enteros');
      return false;
    }
    if (parsedQuantity < 1) {
      setError('Debe ingresar al menos 1 token');
      return false;
    }
    if (parsedQuantity > maxTokens) {
      setError(`Debe ingresar una cantidad entre 1 y ${maxTokens}`);
      return false;
    }
    setError('');
    return true;
  };

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!validateQuantity()) return;

    setIsSubmitting(true);
    try {
      await onConfirm(parsedQuantity);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div
      className="fixed inset-0 z-50 flex min-w-0 items-center justify-center bg-black/70 p-2 sm:p-4"
      role="presentation"
      onMouseDown={(event) => {
        if (event.target === event.currentTarget) onClose();
      }}
    >
      <section
        role="dialog"
        aria-modal="true"
        aria-labelledby="token-action-modal-title"
        className="w-full max-w-md overflow-hidden rounded-2xl border border-gray-500 bg-gray-400 shadow-2xl"
      >
        <header className="flex items-start justify-between gap-3 border-b border-gray-500 bg-gray-500/30 px-4 py-4 sm:px-5">
          <div className="min-w-0">
            <p className="text-xs font-bold uppercase tracking-widest text-pitch-green">Inventario</p>
            <h2 id="token-action-modal-title" className="mt-1 break-words text-xl font-bold text-gray-900">
              {title}
            </h2>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="flex h-9 w-9 shrink-0 items-center justify-center rounded-lg text-gray-900 hover:bg-gray-500/50"
            aria-label="Cerrar"
          >
            <X className="h-5 w-5" aria-hidden="true" />
          </button>
        </header>

        <form onSubmit={handleSubmit} className="space-y-4 p-4 sm:p-5">
          <div className="grid grid-cols-2 gap-3 text-sm">
            <div className="rounded-lg bg-gray-500/25 p-3">
              <span className="block text-xs font-semibold uppercase text-gray-700">Jugador</span>
              <span className="mt-1 block break-words font-bold text-gray-900">{playerName}</span>
            </div>
            <div className="rounded-lg bg-gray-500/25 p-3">
              <span className="block text-xs font-semibold uppercase text-gray-700">Precio por token</span>
              <span className="mt-1 flex items-center font-bold text-gray-900">
                <DollarSign className="h-4 w-4" aria-hidden="true" />
                {unitPrice}
              </span>
            </div>
          </div>

          <div>
            <label htmlFor="token-quantity" className="mb-2 block text-sm font-semibold text-gray-900">
              Cantidad de tokens
            </label>
            <div className="flex items-stretch gap-2">
              <button
                type="button"
                onClick={() => setQuantity(String(Math.max(1, parsedQuantity - 1)))}
                className="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg border border-gray-500 bg-gray-500/40 text-gray-900 hover:bg-gray-500/70"
                aria-label="Disminuir cantidad"
                disabled={parsedQuantity <= 1}
              >
                <Minus className="h-4 w-4" aria-hidden="true" />
              </button>
              <input
                id="token-quantity"
                type="number"
                inputMode="numeric"
                min="1"
                max={maxTokens}
                step="1"
                value={quantity}
                onChange={(event) => {
                  setQuantity(event.target.value);
                  setError('');
                }}
                className="form-input-base min-w-0 flex-1"
                aria-describedby={error ? 'token-quantity-error' : undefined}
                aria-invalid={Boolean(error)}
              />
              <button
                type="button"
                onClick={() => setQuantity(String(Math.min(maxTokens, parsedQuantity + 1)))}
                className="flex h-11 w-11 shrink-0 items-center justify-center rounded-lg border border-gray-500 bg-gray-500/40 text-gray-900 hover:bg-gray-500/70"
                aria-label="Aumentar cantidad"
                disabled={parsedQuantity >= maxTokens}
              >
                <Plus className="h-4 w-4" aria-hidden="true" />
              </button>
            </div>
            <p className="mt-1 text-xs text-gray-700">Disponibilidad: {maxTokens} tokens</p>
            {error && (
              <p id="token-quantity-error" role="alert" className="mt-2 text-sm font-medium text-red-700">
                {error}
              </p>
            )}
          </div>

          <div className="rounded-lg border border-gray-500 bg-gray-500/20 p-3">
            <span className="text-xs font-semibold uppercase text-gray-700">Valor total</span>
            <p className="mt-1 flex items-center break-words text-2xl font-bold text-gray-900">
              <DollarSign className="mr-1 h-6 w-6" aria-hidden="true" />
              {total.toLocaleString('es-ES', { maximumFractionDigits: 0 })}
            </p>
            <p className="mt-1 text-xs text-gray-700">
              {actionType === 'list' ? 'Valor estimado de la publicación.' : 'Valor de los tokens retirados.'}
            </p>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <Button type="button" className="w-full" onClick={onClose} disabled={isSubmitting}>
              Cancelar
            </Button>
            <Button
              type="submit"
              className="w-full"
              disabled={isInvalid || isSubmitting || isLoading}
            >
              {isSubmitting || isLoading ? 'Procesando…' : confirmText}
            </Button>
          </div>
        </form>
      </section>
    </div>
  );
};
