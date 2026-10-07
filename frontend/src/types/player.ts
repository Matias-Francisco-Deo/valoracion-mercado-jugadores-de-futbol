export interface Player {
    id: number;
    name: string;
    currentPrice: number;
    team: Team;
    playerGameData: PlayerGameData;
    tokens: Tokens[];
}

export interface PlayerGameData{
    id: number;
    playerId: number;
    goals: number;
    shotsOnTarget: number;
    tackles: number;
    rating: number;
}

export interface Team{
    id: number;
    name: string;
    league: string;
}
export interface Tokens{
    tokenId: number;
    owner: string|null;//quitar null porque por defecto estara el superUser
    playerId: number;
}