import { useCallback, useEffect, useMemo, useState } from 'react';
import { Search, Sparkles } from 'lucide-react';
import { AlertBanner } from '@/components/ui/AlertBanner';
import { Loading } from '@/components/common/Loading';
import { Pagination } from '@/components/common/Pagination';
import { ServerErrorComponent } from '@/components/ServerErrorComponent';
import { TokenCard } from '@/components/inventory/TokenCard';
import { TokenActionModal } from '@/components/inventory/TokenActionModal';
import {
  cancelTokenListing,
  getUserInventory,
  listTokensForSale,
} from '@/services/inventoryService';
import type { InventoryTab, PlayerTokenHolding, TokenActionModalState } from '@/types/inventory';
import { HttpError } from '@/lib/http-error';

const PAGE_SIZE = 6;

export const InventoryPage: React.FC = () => {
  const [holdings, setHoldings] = useState<PlayerTokenHolding[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [activeTab, setActiveTab] = useState<InventoryTab>('available');
  const [searchQuery, setSearchQuery] = useState('');
  const [activePage, setActivePage] = useState(0);
  const [error, setError] = useState('');
  const [successMessage, setSuccessMessage] = useState('');
  const [modalState, setModalState] = useState<TokenActionModalState | null>(null);

  const loadInventory = useCallback(async () => {
    try {
      const response = await getUserInventory();
      setHoldings(response.holdings);
    } catch (caughtError) {
      setError(caughtError instanceof HttpError ? caughtError.message : 'No se pudo cargar el inventario.');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadInventory();
  }, [loadInventory]);

  const handleAction = useCallback(async (quantity: number) => {
    if (!modalState) return;
    setIsSubmitting(true);
    setError('');
    try {
      if (modalState.actionType === 'list') {
        await listTokensForSale({ playerId: modalState.playerId, quantity });
        setSuccessMessage('Tokens publicados para la venta');
      } else {
        await cancelTokenListing({ playerId: modalState.playerId, quantity });
        setSuccessMessage('Publicación cancelada exitosamente');
      }

      setHoldings((current) => current.map((holding) => {
        if (holding.playerId !== modalState.playerId) return holding;
        return {
          ...holding,
          unlistedTokens: modalState.actionType === 'list'
            ? holding.unlistedTokens - quantity
            : holding.unlistedTokens + quantity,
          listedTokens: modalState.actionType === 'list'
            ? holding.listedTokens + quantity
            : holding.listedTokens - quantity,
        };
      }));
      setModalState(null);
    } catch (caughtError) {
      setError(caughtError instanceof HttpError ? caughtError.message : 'No se pudo procesar la operación.');
    } finally {
      setIsSubmitting(false);
    }
  }, [modalState]);

  const availableHoldings = useMemo(
    () => holdings.filter((holding) => holding.unlistedTokens > 0),
    [holdings],
  );
  const listedHoldings = useMemo(
    () => holdings.filter((holding) => holding.listedTokens > 0),
    [holdings],
  );
  const filteredHoldings = useMemo(
    () => activeTab === 'available' ? availableHoldings : listedHoldings,
    [activeTab, availableHoldings, listedHoldings],
  );
  const visibleHoldings = useMemo(() => {
    const normalizedQuery = searchQuery.trim().toLocaleLowerCase('es');
    return filteredHoldings.filter((holding) => holding.playerName.toLocaleLowerCase('es').includes(normalizedQuery));
  }, [filteredHoldings, searchQuery]);
  const totalPages = Math.max(1, Math.ceil(visibleHoldings.length / PAGE_SIZE));
  const currentHoldings = visibleHoldings.slice(activePage * PAGE_SIZE, activePage * PAGE_SIZE + PAGE_SIZE);

  const selectTab = (tab: InventoryTab) => {
    setActiveTab(tab);
    setActivePage(0);
  };

  const openAction = (holding: PlayerTokenHolding, actionType: 'list' | 'delist') => {
    setModalState({
      isOpen: true,
      actionType,
      playerId: holding.playerId,
      playerName: holding.playerName,
      unitPrice: holding.pricePerToken,
      maxTokens: actionType === 'list' ? holding.unlistedTokens : holding.listedTokens,
    });
  };
  const closeModal = useCallback(() => setModalState(null), []);

  const totalTokens = holdings.reduce((sum, holding) => sum + holding.unlistedTokens + holding.listedTokens, 0);
  const totalPlayers = holdings.length;

  if (isLoading) {
    return <Loading text="Cargando inventario…" className="min-h-72 rounded-2xl bg-white/90 shadow-xl" />;
  }

  if (error) {
    return <ServerErrorComponent />;
  }

  return (
    <section className="w-full max-w-6xl">
      <header className="mb-4 rounded-2xl border border-white/20 bg-black/35 px-4 py-5 text-white shadow-xl backdrop-blur-sm sm:px-6">
        <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
          <div>
            <p className="text-xs font-bold uppercase tracking-[0.25em] text-brand-orange">COLECCIÓN PERSONAL</p>
            <h1 className="mt-1 text-3xl font-black sm:text-4xl">Inventario</h1>
            <p className="mt-2 max-w-2xl text-sm leading-relaxed text-white/90 sm:text-base">
              Revisa los jugadores que posees y gestiona tus tokens en venta.
            </p>
          </div>
          <div className="grid grid-cols-2 gap-2 sm:min-w-64">
            <div className="rounded-xl border border-white/20 bg-white/10 p-3">
              <span className="block text-xs uppercase text-white/70">Tokens</span>
              <strong className="mt-1 block text-xl">{totalTokens}</strong>
            </div>
            <div className="rounded-xl border border-white/20 bg-white/10 p-3">
              <span className="block text-xs uppercase text-white/70">Jugadores</span>
              <strong className="mt-1 block text-xl">{totalPlayers}</strong>
            </div>
          </div>
        </div>
      </header>

      {successMessage && (
        <AlertBanner
          type="success"
          message={successMessage}
          onClose={() => setSuccessMessage('')}
          className="mb-4"
        />
      )}

      <div className="overflow-hidden rounded-2xl border border-white/20 bg-white/95 shadow-2xl backdrop-blur-sm">
        <div className="border-b border-gray-200 p-3 sm:p-4">
          <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
            <div className="flex min-w-0 gap-1 overflow-x-auto rounded-xl bg-gray-200 p-1" role="tablist" aria-label="Secciones del inventario">
              <button
                type="button"
                role="tab"
                aria-selected={activeTab === 'available'}
                onClick={() => selectTab('available')}
                className={`min-w-max rounded-lg px-3 py-2 text-sm font-bold transition sm:px-4 ${
                  activeTab === 'available' ? 'bg-brand-orange text-black shadow-sm' : 'text-gray-700 hover:bg-gray-300'
                }`}
              >
                Tokens Disponibles <span className="ml-1">({availableHoldings.length})</span>
              </button>
              <button
                type="button"
                role="tab"
                aria-selected={activeTab === 'for_sale'}
                onClick={() => selectTab('for_sale')}
                className={`min-w-max rounded-lg px-3 py-2 text-sm font-bold transition sm:px-4 ${
                  activeTab === 'for_sale' ? 'bg-brand-orange text-black shadow-sm' : 'text-gray-700 hover:bg-gray-300'
                }`}
              >
                Tokens en Venta <span className="ml-1">({listedHoldings.length})</span>
              </button>
            </div>

            <label className="relative block min-w-0 w-full md:w-80">
              <span className="sr-only">Buscar jugadores en tu inventario</span>
              <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-gray-500" aria-hidden="true" />
              <input
                type="search"
                value={searchQuery}
                onChange={(event) => {
                  setSearchQuery(event.target.value);
                  setActivePage(0);
                }}
                placeholder="Buscar jugadores en tu inventario"
                className="form-input-base h-11 w-full rounded-xl border border-gray-300 bg-white pl-10 text-gray-900 placeholder:text-gray-500"
              />
            </label>
          </div>
        </div>

        <div className="min-h-72 p-3 sm:p-5" role="tabpanel">
          {visibleHoldings.length === 0 ? (
            <div className="flex min-h-64 flex-col items-center justify-center rounded-xl border border-dashed border-gray-300 bg-gray-50 px-4 py-10 text-center text-gray-700">
              <Sparkles className="mb-3 h-8 w-8 text-brand-orange" aria-hidden="true" />
              <h2 className="text-xl font-bold text-gray-900">
                {searchQuery.trim() ? 'No encontramos jugadores' : activeTab === 'available' ? 'No tienes tokens disponibles' : 'No tienes tokens en venta'}
              </h2>
              <p className="mt-2 max-w-sm text-sm">
                {searchQuery.trim() ? 'Intenta con otro nombre para buscar en tu inventario.' : activeTab === 'available' ? 'Publica tokens para que aparezcan en esta sección.' : 'Los tokens que publiques aparecerán aquí.'}
              </p>
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
              {currentHoldings.map((holding) => (
                <TokenCard
                  key={holding.playerId}
                  playerName={holding.playerName}
                  tokens={activeTab === 'available' ? holding.unlistedTokens : holding.listedTokens}
                  tokensLabel={activeTab === 'available' ? 'disponibles' : 'en venta'}
                  pricePerToken={holding.pricePerToken}
                  actionButtonText={activeTab === 'available' ? 'Poner a la venta' : 'Cancelar venta'}
                  onActionClick={() => openAction(holding, activeTab === 'available' ? 'list' : 'delist')}
                />
              ))}
            </div>
          )}

          <div className="mt-5 flex items-center justify-between gap-3 border-t border-gray-200 pt-4 text-sm text-gray-700">
            <span>
              {visibleHoldings.length === 0 ? 0 : activePage * PAGE_SIZE + 1}–{Math.min((activePage + 1) * PAGE_SIZE, visibleHoldings.length)} de {visibleHoldings.length}
            </span>
            <Pagination
              currentPage={activePage}
              totalPages={totalPages}
              onPageChange={setActivePage}
            />
          </div>
        </div>
      </div>

      {modalState && (
        <TokenActionModal
          isOpen={modalState.isOpen}
          title={modalState.actionType === 'list' ? 'Publicar tokens para la venta' : 'Cancelar publicación de venta'}
          playerName={modalState.playerName}
          unitPrice={modalState.unitPrice}
          maxTokens={modalState.maxTokens}
          confirmText={modalState.actionType === 'list' ? 'Confirmar publicación' : 'Confirmar cancelación'}
          actionType={modalState.actionType}
          onConfirm={handleAction}
          onClose={closeModal}
          isLoading={isSubmitting}
        />
      )}
    </section>
  );
};

export default InventoryPage;
