import type { Player, RegistroCotizacion } from "@/types/player";
import { futbolApi } from "./api";
import type { PlayerFilter, PlayerPageResponse } from "@/types/PlayerFilter";
import type { PageResponse } from "@/types/inventory";

export async function getPlayerById(playerId: string): Promise<Player> {
    const response = futbolApi.get<Player>(`/players/${playerId}`);
    return response;
}

export async function getAllPlayers() {
    const response = futbolApi.get<Player[]>(`/players`);
    return response;
}

export async function getRankingPage(page:number = 0,size:number = 5):Promise<PageResponse<Player>> {
    const response = futbolApi.get<PageResponse<Player>>(`/players/ranking?page=${page}&size=${size}`);
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

    if (filter.position) {
        params.set("position", filter.position);
    }


    params.set("page", page.toString());
    params.set("size", size.toString());

    const query = params.toString();

    const response = futbolApi.get<PlayerPageResponse>(`/players${query ? `?${query}` : ""}`);
    return response;
}

export async function getPlayerQuotes(playerId: string): Promise<RegistroCotizacion[]> {
    return futbolApi.get<RegistroCotizacion[]>(`/players/${playerId}/quotes`);
}

let cachedPlayers: Player[] | null = null;

export async function getPlayerByName(name: string): Promise<Player | null> {
    const cleanName = name.trim().toLowerCase();
    if (cachedPlayers) {
        const found = cachedPlayers.find((p) => p.name.trim().toLowerCase() === cleanName);
        if (found) return found;
    }

    try {
        const response = await futbolApi.get<PlayerPageResponse>(`/players?size=100`);
        cachedPlayers = response.content ?? [];
        return cachedPlayers.find((p) => p.name.trim().toLowerCase() === cleanName) ?? null;
    } catch {
        return null;
    }
}
