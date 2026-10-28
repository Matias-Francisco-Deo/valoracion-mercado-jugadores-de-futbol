export interface PortfolioKpiCardsProps {
  totalValue: number;
  totalInvested: number;
  totalProfitLoss: number;
  totalProfitLossPercentage: number;
}

export function PortfolioKpiCards({
  totalValue,
  totalInvested,
  totalProfitLoss,
  totalProfitLossPercentage,
}: PortfolioKpiCardsProps) {
  const isNetProfit = totalProfitLoss >= 0;

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
      <div className="flex flex-col rounded-xl border border-gray-300 bg-white p-4 shadow-xs">
        <span className="text-xs font-medium text-gray-500">Valor Total de Cartera</span>
        <span className="mt-1 text-2xl font-semibold tracking-tight text-black">
          ${totalValue.toLocaleString("es-AR", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
        </span>
        <span className="mt-1 text-xs text-gray-400">Valuación actual de mercado</span>
      </div>

      <div className="flex flex-col rounded-xl border border-gray-300 bg-white p-4 shadow-xs">
        <span className="text-xs font-medium text-gray-500">Capital Invertido</span>
        <span className="mt-1 text-2xl font-semibold tracking-tight text-black">
          ${totalInvested.toLocaleString("es-AR", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
        </span>
        <span className="mt-1 text-xs text-gray-400">Costo promedio de adquisición</span>
      </div>

      <div className="flex flex-col rounded-xl border border-gray-300 bg-white p-4 shadow-xs">
        <div className="flex items-center justify-between">
          <span className="text-xs font-medium text-gray-500">Rendimiento Neto (PnL)</span>
          <span
            className={`rounded-md px-2 py-0.5 text-xs font-semibold ${
              isNetProfit ? "bg-green-900/15 text-green-800" : "bg-red-900/15 text-red-800"
            }`}
          >
            {isNetProfit ? "+" : ""}
            {totalProfitLossPercentage.toFixed(2)}%
          </span>
        </div>
        <span
          className={`mt-1 text-2xl font-semibold tracking-tight ${
            isNetProfit ? "text-green-800" : "text-red-800"
          }`}
        >
          {isNetProfit ? "+" : ""}${totalProfitLoss.toLocaleString("es-AR", { minimumFractionDigits: 2, maximumFractionDigits: 2 })}
        </span>
        <span className="mt-1 text-xs text-gray-400">Ganancia/Pérdida acumulada</span>
      </div>
    </div>
  );
}
