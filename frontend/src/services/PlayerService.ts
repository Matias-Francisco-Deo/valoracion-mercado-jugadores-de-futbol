import type { Player } from "@/types/player";
import { futbolApi } from "./api";
import type { PlayerFilter, PlayerPageResponse } from "@/types/PlayerFilter";

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

export async function getFiltredPlayers(filter:PlayerFilter,page=0,size=20) {
    const params = new URLSearchParams();
    if (filter.clubName) {
        params.set("clubName", filter.clubName);
    }

    if (filter.league) {
        params.set("league", filter.league);
    }

    /*if (filter.position) {
        params.set("position", filter.position);
    }*/


    params.set("page", page.toString());
    params.set("size", size.toString());

    const query = params.toString();

    const response = futbolApi.get<PlayerPageResponse>(`/players${query ? `?${query}` : ""}`);
    return response;
}