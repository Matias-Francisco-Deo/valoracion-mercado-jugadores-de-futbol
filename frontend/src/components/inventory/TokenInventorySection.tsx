import type { OrderSell, PageResponse, TokenSummary } from "@/types/inventory";
import { Input } from "../ui/Input";
import { TokenSummaryCard } from "./TokenSummaryCard";
import { useState } from "react";
import { Pagination } from "../common/Pagination";

interface TokenInventorySectionProps {
    aviableTokens: PageResponse<TokenSummary> |null;
    ordersSell: PageResponse<OrderSell> |null;
    onActionClick: (token: TokenSummary) => void;

    onAvailablePageChange?: (page: number) => void;
    onSellingPageChange?: (page: number) => void;
}

export default function TokenInventorySection({aviableTokens,ordersSell,onActionClick,onAvailablePageChange,onSellingPageChange}: TokenInventorySectionProps) {
    const [inventoryTab, setInventoryTab] = useState<'available' | 'for_sale'>('available');
    const selectedPage = inventoryTab === "available" ? aviableTokens : ordersSell;
    const onPageChange = inventoryTab === "available" ? onAvailablePageChange : onSellingPageChange;

    return (
        <div className="flex w-full flex-col gap-4 rounded-2xl">

            <div className="flex w-full border-b border-gray-200">
                <button
                onClick={() => setInventoryTab('available')}
                className={`px-4 py-2 text-sm font-medium transition-colors ${
                    inventoryTab === 'available'
                    ? 'border-b-2 border-brand-orange text-brand-orange'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
                >
                Disponibles {aviableTokens?.totalElements}
                </button>

                <button
                onClick={() => setInventoryTab('for_sale')}
                className={`px-4 py-2 text-sm font-medium transition-colors ${
                    inventoryTab === 'for_sale'
                    ? 'border-b-2 border-brand-orange text-brand-orange'
                    : 'text-gray-500 hover:text-gray-700'
                }`}
                >
                En venta {ordersSell?.totalElements}
                </button>
            </div>

            <Input
                className="border-none bg-[#faa42b] shadow-xl"
                placeholder="Buscar tokens..."
            />

            <div className="grid min-h-40 grid-cols-1 gap-4 self-center sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                {inventoryTab === 'available' ? (
                aviableTokens && aviableTokens?.content.length > 0 ? (
                    aviableTokens?.content.map((token) => (
                    <TokenSummaryCard
                        key={token.playerId}
                        token={token}
                        onActionClick={() => onActionClick(token)}
                        actionType="Vender"
                    />
                    ))
                ) : (
                    <p className="col-span-full text-center text-gray-500 py-6">
                    No tienes tokens disponibles.
                    </p>
                )
                ) : ordersSell && ordersSell?.content.length > 0 ? (
                ordersSell?.content.map((token) => (
                    <TokenSummaryCard
                    key={token.id}
                    token={token}
                    onActionClick={() => onActionClick(token)}
                    actionType="Cancelar venta"
                    />
                ))
                ) : (
                <p className="col-span-full text-center text-gray-500">
                    No tienes tokens en venta.
                </p>
                )}
            </div>

            {selectedPage && selectedPage.totalPages > 1 && onPageChange && (
                <div className="flex w-full justify-center border-t border-gray-200 pt-4">
                    <Pagination
                        currentPage={selectedPage.number}
                        totalPages={selectedPage.totalPages}
                        onPageChange={onPageChange}
                    />
                </div>
                )
            }
            
        </div>
    );
}