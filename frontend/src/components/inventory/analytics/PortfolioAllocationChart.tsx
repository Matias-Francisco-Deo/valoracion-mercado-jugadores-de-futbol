import type { AllocationMetricItem } from "@/hooks/usePortfolioMetrics";
import {
  ResponsiveContainer,
  PieChart,
  Pie,
  Cell,
  Tooltip as RechartsTooltip,
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

export interface PortfolioAllocationChartProps {
  allocationData: AllocationMetricItem[];
  selectedPlayerId?: number | null;
}

export function PortfolioAllocationChart({
  allocationData,
  selectedPlayerId,
}: PortfolioAllocationChartProps) {
  const hasSelection = selectedPlayerId !== null && selectedPlayerId !== undefined;
  const isDirectlySelected = allocationData.some(
    (entry) => entry.playerId === selectedPlayerId
  );
  const hasOther = allocationData.some(
    (entry) => entry.isOther || entry.playerId === -1
  );

  const hasActiveItem = hasSelection && (isDirectlySelected || hasOther);

  const isItemActive = (item: AllocationMetricItem) => {
    if (!hasActiveItem) return false;
    if (isDirectlySelected) {
      return item.playerId === selectedPlayerId;
    }
    return Boolean(item.isOther || item.playerId === -1);
  };

  return (
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
              {allocationData.map((entry, index) => {
                const isSelected = isItemActive(entry);
                return (
                  <Cell
                    key={`cell-${entry.playerId}`}
                    fill={entry.playerId === -1 ? "#94A3B8" : PALETTE[index % PALETTE.length]}
                    opacity={hasActiveItem ? (isSelected ? 1 : 0.3) : 1}
                    stroke={isSelected ? "#EA580C" : undefined}
                    strokeWidth={isSelected ? 1.5 : undefined}
                  />
                );
              })}
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
      <div className="mt-2 flex max-h-32 w-full flex-wrap items-center justify-center gap-1.5 overflow-y-auto pt-2">
        {allocationData.map((item, index) => {
          const isSelected = isItemActive(item);
          return (
            <div
              key={item.playerId}
              className={`flex items-center gap-1 rounded-md px-1.5 py-0.5 text-[11px] sm:text-xs transition-all ${
                hasActiveItem && isSelected
                  ? "ring-1 ring-orange-500 bg-orange-50 font-bold shadow-xs scale-105 text-orange-950"
                  : hasActiveItem && !isSelected
                    ? "bg-gray-100 opacity-40"
                    : "bg-gray-100"
              }`}
            >
              <span
                className="h-2 w-2 shrink-0 rounded-full"
                style={{
                  backgroundColor:
                    item.playerId === -1
                      ? "#94A3B8"
                      : PALETTE[index % PALETTE.length],
                }}
              />
              <span className="max-w-[85px] truncate font-medium text-gray-700 sm:max-w-[130px]">
                {item.name}
              </span>
              <span className="shrink-0 font-semibold text-gray-900">
                {item.percentage.toFixed(1)}%
              </span>
            </div>
          );
        })}
      </div>
    </div>
  );
}
