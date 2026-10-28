import { useMemo } from "react";
import type { PerformanceMetricItem } from "@/hooks/usePortfolioMetrics";
import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  ReferenceLine,
  Cell,
  Tooltip as RechartsTooltip,
} from "recharts";

export interface PortfolioPerformanceChartProps {
  performanceData: PerformanceMetricItem[];
  selectedPlayerId?: number | null;
}

function getTopPerformanceItems(
  items: PerformanceMetricItem[],
  selectedPlayerId?: number | null,
  limit = 7,
): PerformanceMetricItem[] {
  if (items.length <= limit) return items;

  const selectedItem =
    selectedPlayerId != null
      ? items.find((item) => item.playerId === selectedPlayerId)
      : null;

  const positive = items.filter((item) => item.profitLossPercentage >= 0);
  const negative = items.filter((item) => item.profitLossPercentage < 0);

  let result: PerformanceMetricItem[] = [];
  if (positive.length > 0 && negative.length > 0) {
    const positiveCount = Math.min(positive.length, 4);
    const negativeCount = Math.min(negative.length, limit - positiveCount);
    result = [
      ...positive.slice(0, positiveCount),
      ...negative.slice(-negativeCount),
    ];
  } else {
    result = items.slice(0, limit);
  }

  // If the user selected a player that didn't make the top cutoff, swap them into the last slot
  if (selectedItem && !result.some((it) => it.playerId === selectedItem.playerId)) {
    result[result.length - 1] = selectedItem;
  }

  return result;
}

export function PortfolioPerformanceChart({
  performanceData,
  selectedPlayerId,
}: PortfolioPerformanceChartProps) {
  const hasSelection = selectedPlayerId !== null && selectedPlayerId !== undefined;
  const isPerformanceFiltered = performanceData.length > 7;

  const displayPerformanceData = useMemo(
    () => getTopPerformanceItems(performanceData, selectedPlayerId, 7),
    [performanceData, selectedPlayerId],
  );

  return (
    <div className="flex flex-col rounded-xl border border-gray-300 bg-white p-3.5 shadow-xs sm:p-4">
      <div className="mb-2 flex flex-wrap items-center justify-between gap-1 border-b border-gray-200 pb-2">
        <span className="text-sm font-semibold text-gray-800">
          Rendimiento por Jugador
        </span>
        <span className="text-xs text-gray-500">
          {isPerformanceFiltered ? "Top rendimientos (%)" : "Ganancia / Pérdida (%)"}
        </span>
      </div>

      <div className="h-64 w-full sm:h-72">
        <ResponsiveContainer width="100%" height="100%">
          <BarChart
            data={displayPerformanceData}
            margin={{ top: 10, right: 8, left: -16, bottom: 15 }}
          >
            <defs>
              <linearGradient id="performanceGainGradient" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="#22c55e" stopOpacity={1} />
                <stop offset="100%" stopColor="#15803d" stopOpacity={1} />
              </linearGradient>
            </defs>
            <XAxis
              dataKey="name"
              interval={0}
              height={46}
              tick={{ fontSize: 10, fill: "#4B5563" }}
              angle={-32}
              textAnchor="end"
              tickFormatter={(name: string) =>
                name.length > 10 ? `${name.slice(0, 9)}…` : name
              }
            />
            <YAxis
              tick={{ fontSize: 10, fill: "#6B7280" }}
              unit="%"
              width={34}
            />
            <ReferenceLine y={0} stroke="#9ca3af" strokeDasharray="3 3" />
            <RechartsTooltip
              labelStyle={{ fontWeight: 600, color: "#111827", marginBottom: "2px" }}
              formatter={(val: unknown) => [
                `${Number(val).toFixed(2)}%`,
                "Rendimiento",
              ]}
            />
            <Bar dataKey="profitLossPercentage" radius={[4, 4, 0, 0]} maxBarSize={32}>
              {displayPerformanceData.map((entry) => {
                const isSelected = entry.playerId === selectedPlayerId;
                return (
                  <Cell
                    key={`bar-${entry.playerId}`}
                    fill={entry.profitLossPercentage >= 0 ? "url(#performanceGainGradient)" : "#b91c1c"}
                    opacity={hasSelection ? (isSelected ? 1 : 0.3) : 1}
                    stroke={isSelected ? "#EA580C" : undefined}
                    strokeWidth={isSelected ? 2.5 : undefined}
                  />
                );
              })}
            </Bar>
          </BarChart>
        </ResponsiveContainer>
      </div>

      <div className="mt-2 flex items-center justify-center gap-4 text-xs text-gray-600">
        <div className="flex items-center gap-1.5">
          <span className="h-2.5 w-2.5 rounded-xs bg-gradient-to-b from-[#22c55e] to-[#15803d]" />
          <span>Ganancia</span>
        </div>
        <div className="flex items-center gap-1.5">
          <span className="h-2.5 w-2.5 rounded-xs bg-[#b91c1c]" />
          <span>Pérdida</span>
        </div>
      </div>
    </div>
  );
}
