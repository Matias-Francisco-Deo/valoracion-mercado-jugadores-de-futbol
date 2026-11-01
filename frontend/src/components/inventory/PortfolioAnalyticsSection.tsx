import { useState } from "react";
import type { PositionToken } from "@/types/inventory";
import { usePortfolioMetrics } from "@/hooks/usePortfolioMetrics";
import { PortfolioKpiCards } from "./analytics/PortfolioKpiCards";
import { PortfolioAllocationChart } from "./analytics/PortfolioAllocationChart";
import { PortfolioPerformanceChart } from "./analytics/PortfolioPerformanceChart";

interface PortfolioAnalyticsSectionProps {
  positions: PositionToken[];
  selectedPlayerId?: number | null;
}

export function PortfolioAnalyticsSection({
  positions,
  selectedPlayerId,
}: PortfolioAnalyticsSectionProps) {
  const { totalValue, totalInvested, totalProfitLoss, totalProfitLossPercentage, allocationData, performanceData, isEmpty } =
    usePortfolioMetrics(positions);
  const [activeTab, setActiveTab] = useState<"both" | "allocation" | "performance">("both");

  if (isEmpty) {
    return (
      <div className="flex w-full flex-col gap-4 rounded-2xl bg-[#f3f1f1] p-4 shadow-sm sm:p-6">
        <div className="flex items-center justify-between">
          <h3 className="text-xl font-medium text-black">Métricas del Portfolio</h3>
        </div>
        <p className="py-6 text-center text-sm text-gray-500">
          No tienes posiciones activas en tu cartera para generar métricas estadísticas.
        </p>
      </div>
    );
  }

  return (
    <div className="flex w-full flex-col gap-6 rounded-2xl bg-[#f3f1f1] p-4 shadow-sm sm:p-6">
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h3 className="text-xl font-medium text-black">Métricas del Portfolio</h3>
          <p className="text-xs text-gray-600 sm:text-sm">Visualización estadística de distribución y rendimiento</p>
        </div>
        <div className="inline-flex self-start rounded-xl bg-gray-200/80 p-1 text-xs font-medium sm:self-auto">
          {(["both", "allocation", "performance"] as const).map((tab) => (
            <button
              key={tab}
              type="button"
              onClick={() => setActiveTab(tab)}
              className={`rounded-lg px-3 py-1.5 transition-colors ${activeTab === tab ? "bg-white text-black shadow-xs" : "text-gray-600 hover:text-black"}`}
            >
              {tab === "both" ? "Completo" : tab === "allocation" ? "Distribución" : "Rendimiento"}
            </button>
          ))}
        </div>
      </div>

      <PortfolioKpiCards
        totalValue={totalValue}
        totalInvested={totalInvested}
        totalProfitLoss={totalProfitLoss}
        totalProfitLossPercentage={totalProfitLossPercentage}
      />

      <div className={`grid grid-cols-1 gap-6 ${activeTab === "both" ? "lg:grid-cols-2" : "grid-cols-1"}`}>
        {(activeTab === "both" || activeTab === "allocation") && (
          <PortfolioAllocationChart
            allocationData={allocationData}
            selectedPlayerId={selectedPlayerId}
          />
        )}
        {(activeTab === "both" || activeTab === "performance") && (
          <PortfolioPerformanceChart
            performanceData={performanceData}
            selectedPlayerId={selectedPlayerId}
          />
        )}
      </div>
    </div>
  );
}
