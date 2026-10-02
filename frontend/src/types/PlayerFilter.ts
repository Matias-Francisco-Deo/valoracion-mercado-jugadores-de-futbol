import type { Player } from "./player";

export interface PlayerFilter {
    clubName?: string;
    league?: string;
    position?: string;
}

export interface PlayerPageResponse {
    content: Player[];
    number:number;
    size:number;
    totalPages:number;
    totalElements:number;
}