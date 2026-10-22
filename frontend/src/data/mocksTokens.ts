import type { OrderSell, PositionToken, TokenSummary } from "@/types/inventory";

export const placeholderSellingTokens: TokenSummary[] = [
  {
    playerId: 0,
    playerName: "Player Name",
    unitPrice : 0,
    quantity: 0
  },
  {
    playerId: 1,
    playerName: "Leonel Messi",
    unitPrice : 10000000,
    quantity: 100
  },
  {
    playerId: 2,
    playerName: "Player Name",
    unitPrice : 0,
    quantity: 0
  },
    {
    playerId: 3,
    playerName: "Player Name",
    unitPrice : 0,
    quantity: 0
  },
    {
    playerId: 4,
    playerName: "Player Name",
    unitPrice : 0,
    quantity: 0
  }
]

export const placeholderOrderSell: OrderSell[] = [
  {
    playerId: 0,
    playerName: "Player Name",
    unitPrice : 0,
    quantity: 0
  },
  {
    playerId: 1,
    playerName: "Leonel Messi",
    unitPrice : 10000000,
    quantity: 100
  },
  {
    playerId: 2,
    playerName: "Player Name",
    unitPrice : 0,
    quantity: 0
  },
  {
    playerId: 3,
    playerName: "Player Name",
    unitPrice : 0,
    quantity: 0
  },
  {
    playerId: 4,
    playerName: "Player Name",
    unitPrice : 0,
    quantity: 0
  }
]

export const placeholderPositions: PositionToken[] = [
  {
    playerId: 1,
    playerName: "Lionel Messi",
    quantity: 100,
    averagePurchasePrice: 1500,
    currentPrice: 1800,
    profitLoss: 3000,
  },
  {
    playerId: 2,
    playerName: "Cristiano Ronaldo",
    quantity: 5,
    averagePurchasePrice: 2000,
    currentPrice: 1850,
    profitLoss: -750,
  },
  {
    playerId: 3,
    playerName: "Kylian Mbappé",
    quantity: 8,
    averagePurchasePrice: 1200,
    currentPrice: 1350,
    profitLoss: 1200,
  },
  {
    playerId: 4,
    playerName: "Erling Haaland",
    quantity: 12,
    averagePurchasePrice: 950,
    currentPrice: 1100,
    profitLoss: 1800,
  },
  {
    playerId: 5,
    playerName: "Neymar Jr.",
    quantity: 3,
    averagePurchasePrice: 2500,
    currentPrice: 2200,
    profitLoss: -900,
  },
];