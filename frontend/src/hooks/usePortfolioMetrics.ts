import { useMemo } from "react";
import type { PositionToken } from "@/types/inventory";

export interface AllocationMetricItem {
  playerId: number;
  name: string;
  value: number;
  quantity: number;
  percentage: number;
}

export interface PerformanceMetricItem {
  playerId: number;
  name: string;
  profitLoss: number;
  profitLossPercentage: number;
}

export interface PortfolioMetrics {
  totalValue: number;
  totalInvested: number;
  totalProfitLoss: number;
  totalProfitLossPercentage: number;
  allocationData: AllocationMetricItem[];
  performanceData: PerformanceMetricItem[];
  isEmpty: boolean;
}

export function usePortfolioMetrics(positions: PositionToken[]): PortfolioMetrics {
  return useMemo(() => {
    if (!positions || positions.length === 0) {
      return {
        totalValue: 0,
        totalInvested: 0,
        totalProfitLoss: 0,
        totalProfitLossPercentage: 0,
        allocationData: [],
        performanceData: [],
        isEmpty: true,
      };
    }

    let calculatedTotalValue = 0;
    let calculatedTotalInvested = 0;

    for (const position of positions) {
      const posValue = position.currentValue ?? (position.quantity * position.currentPrice);
      const posInvested = position.quantity * position.averagePurchasePrice;
      calculatedTotalValue += posValue;
      calculatedTotalInvested += posInvested;
    }

    const totalProfitLoss = calculatedTotalValue - calculatedTotalInvested;
    const totalProfitLossPercentage =
      calculatedTotalInvested > 0
        ? (totalProfitLoss / calculatedTotalInvested) * 100
        : 0;

    const allocationData: AllocationMetricItem[] = positions
      .map((position) => {
        const value = position.currentValue ?? (position.quantity * position.currentPrice);
        const percentage =
          calculatedTotalValue > 0 ? (value / calculatedTotalValue) * 100 : 0;
        return {
          playerId: position.playerId,
          name: position.playerName,
          value,
          quantity: position.quantity,
          percentage,
        };
      })
      .sort((a, b) => b.value - a.value);

    const performanceData: PerformanceMetricItem[] = positions
      .map((position) => {
        const invested = position.quantity * position.averagePurchasePrice;
        const profitLossPercentage =
          position.profitLossPercentage ??
          (invested > 0 ? (position.profitLoss / invested) * 100 : 0);
        return {
          playerId: position.playerId,
          name: position.playerName,
          profitLoss: position.profitLoss,
          profitLossPercentage,
        };
      })
      .sort((a, b) => b.profitLossPercentage - a.profitLossPercentage);

    return {
      totalValue: calculatedTotalValue,
      totalInvested: calculatedTotalInvested,
      totalProfitLoss,
      totalProfitLossPercentage,
      allocationData,
      performanceData,
      isEmpty: false,
    };
  }, [positions]);
}
