export interface Player {
    id: number;
    name: string;
    currentPrice: number;
    clubName: string;
    goals: number;
    shotsOnTarget: number;
    passes: number;
    tackles: number;
    rating: number;
    interceptions: number;

    // tokens:number[];
}
// Long id,
//     String name,
//     Integer currentPrice,
//     String clubName,
//     Integer goals,
//     Integer shotsOnTarget,
//     Integer passes,
//     Integer interceptions,
//     Integer tackles,
//     Double rating,
// List<TokenResponseDTO> tokens