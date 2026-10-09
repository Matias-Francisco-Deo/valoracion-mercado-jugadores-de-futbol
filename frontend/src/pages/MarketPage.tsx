import { Loading } from "@/components/common/Loading";
import { Pagination } from "@/components/common/Pagination";
import { TokenActionModal } from "@/components/inventory/TokenActionModal";
import { TokenCard } from "@/components/inventory/TokenCard";
import { ServerErrorComponent } from "@/components/ServerErrorComponent";
import { Input } from "@/components/ui/Input";
import type { HttpError } from "@/lib/http-error";
import { buyTokens, getTokensOnSale } from "@/services/ordersService";
import type { Token, TokenPageResponse } from "@/types/inventory";
import { useEffect, useState } from "react";
import { useSearchParams } from "react-router-dom";

export default function MarketPage(){
    const [searchParams, setSearchParams] = useSearchParams();
    const [selectedToken,setSelectedToken] = useState<Token|null>(null);
    const [actionModalOpen, setActionModalOpen] = useState(false);
    const [tokenPage, setTokenPage] = useState<TokenPageResponse | null>(null);

    const [error, setError] = useState<HttpError | null>(null);
    const [loading, setLoading] = useState(true);

    const handleActionClick = (token: Token) => {
        setSelectedToken(token);
        setActionModalOpen(true);
    }

    const handlePageChange = (page: number) => {
        const params = new URLSearchParams(searchParams);

        params.set("page", page.toString());

        setSearchParams(params);
    };

    useEffect(() => {
        getTokensOnSale().then((data) => {
            setTokenPage(data);
        }).catch((err: HttpError) => {setError(err);})
        .finally(() => {setLoading(false);})
    }, [searchParams]);

    if (loading) return <Loading text="Cargando catálogo..." />
    //if (error) return <ServerErrorComponent />

    return(
        <div className="flex w-full justify-center px-4 py-8">
            <div className="flex w-full max-w-[77vw] flex-col gap-6">
                <div className="flex flex-col gap-2">
                    <h2 className="text-4xl font-semibold tracking-tight sm:text-5xl">
                        Marketplace
                    </h2>

                    <span className="text-sm sm:text-base">
                        Descubre ofertas de tokens de coleccionistas de todo el mundo. 
                    </span>
                </div>

                <div className="flex w-full flex-col gap-6 rounded-2xl bg-[#f3f1f1] p-4 shadow-sm sm:p-6">
                    <Input className="border-none bg-[#faa42b] shadow-xl max-w-xl" placeholder="Buscar tokens..." />
                    <div className="self-center grid min-h-40 grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                        {tokenPage && tokenPage.content.length > 0 ?(
                            tokenPage.content.map((token)=>(
                                <TokenCard key={token.playerId} token={token} onActionClick={()=>handleActionClick(token)} actionType="Comprar"/>
                            ))
                        ) : (
                            <p className="text-gray-500 col-span-full text-center">No hay tokens en venta</p>
                        )}
                    </div>
                    <div className="flex w-full justify-center border-t border-gray-200 pt-4">
                        <Pagination
                        currentPage={tokenPage?.number ?? 0}
                        totalPages={tokenPage?.totalPages ?? 0}
                        onPageChange={handlePageChange}/>
                    </div>
                </div>
            </div>

            {actionModalOpen && selectedToken && (
                <TokenActionModal token={selectedToken} actionType={'Comprar'} title={"Comprar tokens"} onConfirm={buyTokens} 
                    onClose={() => {setActionModalOpen(false);setSelectedToken(null)}} />
            )}
        </div>
    )
}