import type { Player } from "@/types/player";
import { futbolApi } from "./api";
import type { PlayerFilter } from "@/types/PlayerFilter";

export async function getPlayerById(playerId: string): Promise<Player> {
    const response = futbolApi.get<Player>(`/players/${playerId}`);
    return response;
}

export async function getAllPlayers() {
    const response = futbolApi.get<Player[]>(`/players`);
    return response;
}

export async function getTopPlayers() {
    const response = futbolApi.get<Player[]>(`/players/top`);
    return response;
}

export async function getFiltredPlayers(filter:PlayerFilter) {
    const response = futbolApi.get<Player[]>(`/players`);
    return response;
}