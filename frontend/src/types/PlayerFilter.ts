import type { Player } from "./player";

export interface PlayerFilter {
    clubName?: string;
    league?: string;
}

export interface PlayerPageResponse {
    content: Player[];
    number:number;
    size:number;
    totalPages:number;
    totalElements:number;
}