import { useState } from "react";
import type { PositionToken } from "@/types/inventory";
import { usePortfolioMetrics } from "@/hooks/usePortfolioMetrics";
import {
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Tooltip as RechartsTooltip,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  ReferenceLine,
} from "recharts";

const PALETTE = [
  "#FF9500", // Brand orange
  "#0284C7", // Sky blue
  "#10B981", // Emerald green
  "#8B5CF6", // Purple
  "#F59E0B", // Amber
  "#EC4899", // Pink
  "#06B6D4", // Cyan
  "#64748B", // Slate
];

interface PortfolioAnalyticsSectionProps {
  positions: PositionToken[];
}

export function PortfolioAnalyticsSection({ positions }: PortfolioAnalyticsSectionProps) {
  const {
    totalValue,
    totalInvested,
    totalProfitLoss,
    totalProfitLossPercentage,
    allocationData,
    performanceData,
    isEmpty,
  } = usePortfolioMetrics(positions);

  const [activeTab, setActiveTab] = useState<"both" | "allocation" | "performance">("both");

  const isNetProfit = totalProfitLoss >= 0;

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
      {/* Header and View Selector */}
      <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <h3 className="text-xl font-medium text-black">Métricas del Portfolio</h3>
          <p className="text-xs text-gray-600 sm:text-sm">
            Visualización estadística de distribución y rendimiento
          </p>
        </div>

        <div className="inline-flex self-start rounded-xl bg-gray-200/80 p-1 text-xs font-medium sm:self-auto">
          <button
            type="button"
            onClick={() => setActiveTab("both")}
            className={`rounded-lg px-3 py-1.5 transition-colors ${
              activeTab === "both"
                ? "bg-white text-black shadow-xs"
                : "text-gray-600 hover:text-black"
            }`}
          >
            Completo
          </button>
          <button
            type="button"
            onClick={() => setActiveTab("allocation")}
            className={`rounded-lg px-3 py-1.5 transition-colors ${
              activeTab === "allocation"
                ? "bg-white text-black shadow-xs"
                : "text-gray-600 hover:text-black"
            }`}
          >
            Distribución
          </button>
          <button
            type="button"
            onClick={() => setActiveTab("performance")}
            className={`rounded-lg px-3 py-1.5 transition-colors ${
              activeTab === "performance"
                ? "bg-white text-black shadow-xs"
                : "text-gray-600 hover:text-black"
            }`}
          >
            Rendimiento
          </button>
        </div>
      </div>

      {/* KPI Cards Row */}
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

      {/* Charts Grid */}
      <div
        className={`grid grid-cols-1 gap-6 ${
          activeTab === "both" ? "lg:grid-cols-2" : "grid-cols-1"
        }`}
      >
        {/* Panel 1: Donut Chart - Asset Allocation */}
        {(activeTab === "both" || activeTab === "allocation") && (
          <div className="flex flex-col rounded-xl border border-gray-300 bg-white p-4 shadow-xs">
            <div className="mb-2 flex items-center justify-between border-b border-gray-200 pb-2">
              <span className="text-sm font-semibold text-gray-800">
                Distribución de Cartera
              </span>
              <span className="text-xs text-gray-500">Por valor actual ($)</span>
            </div>

            <div className="h-64 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <PieChart>
                  <Pie
                    data={allocationData}
                    dataKey="value"
                    nameKey="name"
                    cx="50%"
                    cy="50%"
                    innerRadius={55}
                    outerRadius={85}
                    paddingAngle={3}
                  >
                    {allocationData.map((entry, index) => (
                      <Cell
                        key={`cell-${entry.playerId}`}
                        fill={PALETTE[index % PALETTE.length]}
                      />
                    ))}
                  </Pie>
                  <RechartsTooltip
                    formatter={(val: unknown) => [
                      `$${Number(val).toLocaleString("es-AR", { minimumFractionDigits: 2 })}`,
                      "Valor",
                    ]}
                  />
                </PieChart>
              </ResponsiveContainer>
            </div>

            {/* Custom Legend with percentages */}
            <div className="mt-2 flex max-h-28 flex-wrap gap-2 overflow-y-auto pt-2">
              {allocationData.map((item, index) => (
                <div
                  key={item.playerId}
                  className="flex items-center gap-1.5 rounded-md bg-gray-100 px-2 py-1 text-xs"
                >
                  <span
                    className="h-2.5 w-2.5 shrink-0 rounded-full"
                    style={{ backgroundColor: PALETTE[index % PALETTE.length] }}
                  />
                  <span className="max-w-[110px] truncate font-medium text-gray-700">
                    {item.name}
                  </span>
                  <span className="font-semibold text-gray-900">
                    {item.percentage.toFixed(1)}%
                  </span>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Panel 2: Bar Chart - Player Performance (PnL %) */}
        {(activeTab === "both" || activeTab === "performance") && (
          <div className="flex flex-col rounded-xl border border-gray-300 bg-white p-4 shadow-xs">
            <div className="mb-2 flex items-center justify-between border-b border-gray-200 pb-2">
              <span className="text-sm font-semibold text-gray-800">
                Rendimiento por Jugador
              </span>
              <span className="text-xs text-gray-500">Ganancia / Pérdida (%)</span>
            </div>

            <div className="h-64 w-full">
              <ResponsiveContainer width="100%" height="100%">
                <BarChart
                  data={performanceData}
                  margin={{ top: 12, right: 12, left: -10, bottom: 25 }}
                >
                  <XAxis
                    dataKey="name"
                    interval={0}
                    tick={{ fontSize: 11 }}
                    angle={-20}
                    textAnchor="end"
                  />
                  <YAxis
                    tick={{ fontSize: 11 }}
                    unit="%"
                  />
                  <ReferenceLine y={0} stroke="#9ca3af" strokeDasharray="3 3" />
                  <RechartsTooltip
                    formatter={(val: unknown) => [
                      `${Number(val).toFixed(2)}%`,
                      "Rendimiento",
                    ]}
                  />
                  <Bar dataKey="profitLossPercentage" radius={[4, 4, 0, 0]}>
                    {performanceData.map((entry) => (
                      <Cell
                        key={`bar-${entry.playerId}`}
                        fill={entry.profitLossPercentage >= 0 ? "#15803d" : "#b91c1c"}
                      />
                    ))}
                  </Bar>
                </BarChart>
              </ResponsiveContainer>
            </div>

            <div className="mt-2 flex items-center justify-center gap-4 text-xs text-gray-600">
              <div className="flex items-center gap-1.5">
                <span className="h-2.5 w-2.5 rounded-xs bg-[#15803d]" />
                <span>Ganancia</span>
              </div>
              <div className="flex items-center gap-1.5">
                <span className="h-2.5 w-2.5 rounded-xs bg-[#b91c1c]" />
                <span>Pérdida</span>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
