import { Loading } from "@/components/common/Loading";
import { Pagination } from "@/components/common/Pagination";
import { TokenActionModal } from "@/components/inventory/TokenActionModal";
import { TokenSummaryCard } from "@/components/inventory/TokenSummaryCard";
import { ServerErrorComponent } from "@/components/ServerErrorComponent";
import { Button } from "@/components/ui/Button";
import { Input } from "@/components/ui/Input";
import { useAuth } from "@/hooks/useAuth";
import type { HttpError } from "@/lib/http-error";
import { buyListedTokens, getTokensOnSale } from "@/services/ordersService";
import type { TokenSummary, PageResponse, OrderTokensRequest, OrderSell } from "@/types/inventory";
import { useCallback, useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { toast } from "react-toastify";

export default function MarketPage(){
    const { user, refreshUser } = useAuth()
    const navigate = useNavigate();
    const [searchParams, setSearchParams] = useSearchParams();

    const [selectedToken,setSelectedToken] = useState<TokenSummary|null>(null);
    const [actionModalOpen, setActionModalOpen] = useState(false);
    const [modalError,setModalError] = useState<HttpError | null>(null);

    const [tokenPage, setTokenPage] = useState<PageResponse<OrderSell> | null>(null);
    const [nombre, setNombre] = useState(searchParams.get("nombre") ?? "");

    const [error, setError] = useState<HttpError | null>(null);
    const [loading, setLoading] = useState(true);

    const handleActionClick = (token: TokenSummary) => {
        if(!user){
            navigate("/login");
            return;
        }
        setSelectedToken(token);
        setActionModalOpen(true);
    }

    const handleSearch = (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();

        const params = new URLSearchParams(searchParams);
        const nombreNormalizado = nombre.trim();

        if (nombreNormalizado) {
            params.set("nombre", nombreNormalizado);
        } else {
            params.delete("nombre");
        }

        params.set("page", "0");

        setSearchParams(params);
    };

    const buyTokens = async  (playerId:number,quantity:number): Promise<void> => {
        setModalError(null);
        if (!user) return
        const request: OrderTokensRequest = {
            playerId,
            quantity,
        }
        await buyListedTokens(request).then(()=>{
            void refreshUser().catch(console.error);

            setActionModalOpen(false);
            setSelectedToken(null);
            toast.success(`¡Compra exitosa! Compraste ${quantity} token(s).`);

            void refreshOrders();
        })
        .catch((error: HttpError) => {setModalError(error);});
    }

    const handlePageChange = (page: number) => {
        const params = new URLSearchParams(searchParams);

        params.set("page", page.toString());

        setSearchParams(params);
    };

    const refreshOrders = useCallback((): Promise<void> => {
        const page = Number(searchParams.get("page") ?? "0");
        const nombre = searchParams.get("nombre") ?? undefined;

        return getTokensOnSale(page, nombre)
            .then((data) => {
                setTokenPage(data);
            })
            .catch((err: HttpError) => {
                setError(err);
            });
    }, [searchParams]);

    useEffect(() => {
        setLoading(true);
        setError(null);

        void refreshOrders().finally(() => {setLoading(false);});
        
    }, [refreshOrders]);

    if (loading) return <Loading text="Cargando catálogo..." />
    if (error) return <ServerErrorComponent />

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

                    <form onSubmit={handleSearch} className="flex w-full max-w-xl gap-2">
                        <Input className="border-none bg-[#faa42b] shadow-xl max-w-xl" placeholder="Buscar tokens..." value={nombre}
                    onChange={(e) => setNombre(e.target.value)}/>
                        <Button type="submit" className="cursor-pointer">
                            Buscar
                        </Button>
                    </form>

                    <div className="self-center grid w-full min-h-40 grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                        {tokenPage && tokenPage.content.length > 0 ?(
                            tokenPage.content.map((token)=>(
                                <TokenSummaryCard key={token.playerId} token={token} onActionClick={()=>handleActionClick(token)} actionType="Comprar"/>
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
                <TokenActionModal token={selectedToken} actionType={'Comprar'} title={"Comprar tokens"} onConfirm={buyTokens} error={modalError?.message}
                    onClose={() => {setActionModalOpen(false);setSelectedToken(null)}} />
            )}
        </div>
    )
}