import { Loading } from "@/components/common/Loading";
import { Pagination } from "@/components/common/Pagination";
import { TokenCard } from "@/components/inventory/TokenCard";
import { ServerErrorComponent } from "@/components/ServerErrorComponent";
import { Input } from "@/components/ui/Input";
import type { HttpError } from "@/lib/http-error";
import { getUserInventory } from "@/services/inventoryService";
import type { Token } from "@/types/inventory";
import { useEffect, useState } from "react";

const placeholderSellingTokens: Token[] = [
  {
    playerId: 0,
    playerName: "Player Name",
    price: 0,
    selling: false,
    quantity: 0
  },
  {
    playerId: 1,
    playerName: "Player Name",
    price: 0,
    selling: false,
    quantity: 0
  },
  {
    playerId: 2,
    playerName: "Player Name",
    price: 0,
    selling: false,
    quantity: 0
  }
]


export default function InventoryPage(): React.ReactNode {
  const [tokens, setTokens] = useState<Token[]>(placeholderSellingTokens);
  const [sellingTokens, setSellingTokens] = useState<Token[]>([]);
  const [inventoryTab, setInventoryTab] = useState<'available' | 'for_sale'>('available');
  const [error, setError] = useState<HttpError | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getUserInventory().then((data) => {
      setTokens(data.availableTokens);
      setSellingTokens(data.sellingTokens);
    }).catch((err: HttpError) => {setError(err);})
    .finally(() => {setLoading(false);})
  }, []);

      if (loading) return <Loading text="Cargando catálogo..." />
      //if (error) return <ServerErrorComponent />

  return (
    <div className="flex w-full justify-center px-4 py-8 sm:px-6 lg:px-8">
      <div className="flex w-full max-w-6xl flex-col gap-6">
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
            }`}
          >
            Disponibles
          </button>

          <button
            onClick={() => setInventoryTab("for_sale")}
            className={`px-4 py-2 text-sm font-medium transition-colors ${
              inventoryTab === "for_sale"
                ? "border-b-2 border-brand-orange text-brand-orange"
                : "text-gray-500 hover:text-gray-700"
            }`}
          >
            En venta
          </button>
        </div>
          <Input className="border-none bg-[#faa42b] shadow-xl" placeholder="Buscar tokens..." />
          
          <div className="grid min-h-40 grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {inventoryTab === 'available' ? (
              tokens.length > 0 ? (
                tokens.map((token) => (
                  <TokenCard key={token.playerId} token={token} />
                ))
              ) : (
                <p className="text-gray-500 col-span-full text-center">No tienes tokens disponibles.</p>
              )
            ):(
              sellingTokens.length > 0 ? (
                sellingTokens.map((token) => (
                  <TokenCard key={token.playerId} token={token} />
                ))
              ) : (
                <p className="text-gray-500 col-span-full text-center">No tienes tokens en venta.</p>
              )
            )}
          </div>
          <div className="flex w-full justify-center border-t border-gray-200 pt-4">
            <Pagination/>
          </div>
        </div>
      </div>
      
    </div>
  );
};