import { Loading } from "@/components/common/Loading";
import { TokenActionModal } from "@/components/inventory/TokenActionModal";
import TokenInventorySection from "@/components/inventory/TokenInventorySection";
import { TransactionHistorySection } from "@/components/inventory/TransactionHistorySection";
import { ServerErrorComponent } from "@/components/ServerErrorComponent";
import type { HttpError } from "@/lib/http-error";
import { listTokensForSale } from "@/services/ordersService";
import { getAviableTokens, getOrdersSell, getTransactionHistory, getUserPortfolio } from "@/services/userService";
import type { OrderSell, OrderTokensRequest, PageResponse, PositionToken, TokenSummary, Transaction } from "@/types/inventory";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

export default function InventoryPage() {
  const { userId } = useParams()
  const [selectedToken, setSelectedToken] = useState<TokenSummary | null>(null);

  const [transactionsError, setTransactionsError] = useState<HttpError | null>(null);
  const [transactionsPage,setTransactionsPage] = useState<PageResponse<Transaction>|null>(null);

  //paginacion de los tokens
  const [positions,setPositions] = useState<PageResponse<PositionToken>|null>(null);
  const [aviableTokens, setAviableTokens] = useState<PageResponse<TokenSummary>|null>(null);
  const [ordersSell, setOrdersSell] = useState<PageResponse<OrderSell>|null>(null);

  const [actionModalOpen, setActionModalOpen] = useState(false);
  const [modalError,setModalError] = useState<HttpError | null>(null);

  const [serverError, setServerError] = useState<HttpError | null>(null);
  const [loadingPage, setLoadingPage] = useState(true);

  const handleActionClick = (token: TokenSummary) => {
    setSelectedToken(token);
    setModalError(null);
    setActionModalOpen(true);
  }

  const sellTokens = async  (playerId:number,quantity:number): Promise<void> => {
    setModalError(null);
    if (!userId) return
    const request: OrderTokensRequest = {
      playerId,
      quantity,
      userId,
    }

    await listTokensForSale(request).then(()=>{
      setActionModalOpen(false);
      setSelectedToken(null);
    })
    .catch((error: HttpError) => {setModalError(error);});
  }

//funciones de cambio de pagina
  const handleAvailablePageChange = (page:number) => {
    if (!userId) return;

    getAviableTokens(userId, page)
    .then((data) => setAviableTokens(data))
    .catch((error: HttpError) => setServerError(error));
  }

  const handleOrderPageChange= (page:number) => {
    if (!userId) return;

    getOrdersSell(userId, page)
    .then((data) => setOrdersSell(data))
    .catch((error: HttpError) => setServerError(error));
  }

  const handleTransactionPageChange = (page: number) => {
    if (!userId) return;

    getTransactionHistory(userId, page)
    .then((data) => setTransactionsPage(data))
    .catch((error: HttpError) => setTransactionsError(error));
  };

  

  useEffect(() => {
    if (!userId) return

    getAviableTokens(userId).then((data)=>setAviableTokens(data))
    .catch((error)=>setServerError(error))

    getOrdersSell(userId).then((data)=>setOrdersSell(data))
    .catch((error)=>setServerError(error))

    getUserPortfolio(userId).then((data)=>setPositions(data))
    .catch((error)=>setServerError(error))

    getTransactionHistory(userId)
    .then((data) => setTransactionsPage(data))
    .catch((error)=> setServerError(error))
    .finally(()=>setLoadingPage(false))
    
  }, [userId]);

      if (loadingPage) return <Loading text="Cargando catálogo..." />
      if (serverError) return <ServerErrorComponent />

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
        <div className="flex w-full flex-col gap-4 rounded-2xl bg-[#f3f1f1] min-h-[470px] p-4 shadow-sm sm:p-6">
          <div className="flex justify-between px-2 text-black">
            <h3 className="text-xl font-medium">Inventario de Tokens</h3>
            <div className="flex items-baseline gap-1 ">
              <span className="text-xl text-hover-orange">{positions?.totalElements ?? 0}</span>
              <span className="text-xs text-gray-500">tokens</span>
            </div>
          </div>
          <TokenInventorySection
              aviableTokens={aviableTokens}
              ordersSell={ordersSell}
              onActionClick={handleActionClick}
              onAvailablePageChange={handleAvailablePageChange}
              onSellingPageChange={handleOrderPageChange}
            />
        </div>
        <TransactionHistorySection transactionsPage={transactionsPage} onPageChange={handleTransactionPageChange} transactionError={transactionsError}/>
      </div>
      {actionModalOpen && selectedToken && (
        <TokenActionModal token={selectedToken} actionType={'Vender'} title={'Vender token'} error={modalError?.message} onConfirm={sellTokens} 
        onClose={() => {setActionModalOpen(false);setSelectedToken(null)}} />
      )}
    </div>
  );
};