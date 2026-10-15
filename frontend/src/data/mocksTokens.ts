import type { OrderSell, TokenSummary } from "@/types/inventory";

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
    id:1,
    playerId: 0,
    playerName: "Player Name",
    unitPrice : 0,
    totalPrice:0,
    quantity: 0
  },
  {
    id:1,
    playerId: 1,
    playerName: "Leonel Messi",
    unitPrice : 10000000,
    totalPrice:10000000*100,
    quantity: 100
  },
  {
    id:1,
    playerId: 2,
    playerName: "Player Name",
    unitPrice : 0,
    totalPrice:0,
    quantity: 0
  },
  {
    id:1,
    playerId: 3,
    playerName: "Player Name",
    unitPrice : 0,
    totalPrice:0,
    quantity: 0
  },
  {
    id:1,
    playerId: 4,
    playerName: "Player Name",
    unitPrice : 0,
    totalPrice:0,
    quantity: 0
  }
]