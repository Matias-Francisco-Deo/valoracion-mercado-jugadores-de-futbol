import { Loading } from "@/components/common/Loading";
import { Pagination } from "@/components/common/Pagination";
import { TokenActionModal } from "@/components/inventory/TokenActionModal";
import { TokenCard } from "@/components/inventory/TokenCard";
import { ServerErrorComponent } from "@/components/ServerErrorComponent";
import { Input } from "@/components/ui/Input";
import type { HttpError } from "@/lib/http-error";
import { cancelTokenListing, listTokensForSale } from "@/services/ordersService";
import type { Token } from "@/types/inventory";
import { useEffect, useState } from "react";
import {placeholderSellingTokens} from "@/data/mockTokens";//quitar cuando exista endpoint de data
import { getUserInventory } from "@/services/userService";
import { useParams } from "react-router-dom";

export default function InventoryPage() {
  const { userId } = useParams()
  const [tokens, setTokens] = useState<Token[]>(placeholderSellingTokens);
  const [sellingTokens, setSellingTokens] = useState<Token[]>([]);
  const [inventoryTab, setInventoryTab] = useState<'available' | 'for_sale'>('available');
  const [actionModalOpen, setActionModalOpen] = useState(false);
  const [selectedToken, setSelectedToken] = useState<Token | null>(null);
  const [error, setError] = useState<HttpError | null>(null);
  const [loading, setLoading] = useState(true);

  const handleActionClick = (token: Token) => {
    setSelectedToken(token);
    setActionModalOpen(true);
  }

  useEffect(() => {
    if (!userId) return

    setError(null)

    getUserInventory(userId).then((data) => {
      setTokens(data.availableTokens);
      setSellingTokens(data.sellingTokens);
    }).catch((err: HttpError) => {setError(err);})
    .finally(() => {setLoading(false);})
  }, [userId]);

      if (loading) return <Loading text="Cargando catálogo..." />
      //if (error) return <ServerErrorComponent />

  return (
    <div className="flex w-full justify-center px-4 py-8">
      <div className="flex w-full max-w-[77vw] flex-col gap-6">
        <div className="flex flex-col gap-2">
          <h2 className="text-4xl font-semibold tracking-tight sm:text-5xl">
            Inventario
          </h2>

          <span className="text-sm sm:text-base">
            Revisa y gestiona tus tokens
          </span>
        </div>

        <div className="flex w-full flex-col gap-6 rounded-2xl bg-[#f3f1f1] p-4 shadow-sm sm:p-6">
          <div className="flex w-full border-b border-gray-200">
          <button onClick={() => setInventoryTab("available")}
            className={`px-4 py-2 text-sm font-medium transition-colors ${
              inventoryTab === "available"
                ? "border-b-2 border-brand-orange text-brand-orange "
                : "text-gray-500 hover:text-gray-700"
            }`}>
            Disponibles
          </button>

          <button
            onClick={() => setInventoryTab("for_sale")}
            className={`px-4 py-2 text-sm font-medium transition-colors ${
              inventoryTab === "for_sale"
                ? "border-b-2 border-brand-orange text-brand-orange"
                : "text-gray-500 hover:text-gray-700"
            }`}>
            En venta
          </button>
        </div>
          <Input className="border-none bg-[#faa42b] shadow-xl" placeholder="Buscar tokens..." />
          
          <div className="self-center grid min-h-40 grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
            {inventoryTab === 'available' ? (
              tokens.length > 0 ? (
                tokens.map((token) => (
                  <TokenCard key={token.playerId} token={token} onActionClick={()=>handleActionClick(token)} 
                  actionType={token.selling ? 'Cancelar venta' : 'Vender'}/>
                ))
              ) : (
                <p className="text-gray-500 col-span-full text-center">No tienes tokens disponibles.</p>
              )
            ):(
              sellingTokens.length > 0 ? (
                sellingTokens.map((token) => (
                  <TokenCard key={token.playerId} token={token} onActionClick={() => handleActionClick(token)} 
                  actionType={token.selling ? 'Cancelar venta' : 'Vender'}/>
                ))
              ) : (
                <p className="text-gray-500 col-span-full text-center">No tienes tokens en venta.</p>
              )
            )}
          </div>
          <div className="flex w-full justify-center border-t border-gray-200 pt-4">
            {/*<Pagination/>*/}
          </div>
        </div>
      </div>
      {actionModalOpen && selectedToken && (
        <TokenActionModal token={selectedToken} actionType={selectedToken.selling ? 'Retirar' : 'Vender'} title={selectedToken.selling ? 'Retirar de la venta' : 'Vender token'} onConfirm={selectedToken.selling? cancelTokenListing: listTokensForSale}
        onClose={() => {setActionModalOpen(false);setSelectedToken(null)}} />
      )}
    </div>
  );
};