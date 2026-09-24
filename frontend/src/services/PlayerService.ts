import type { Player } from "@/types/player";
import { futbolApi } from "./api";

export async function getPlayerById(playerId: string): Promise<Player> {
    const response = futbolApi.get<Player>(`/players/${playerId}`);
    return response;

}