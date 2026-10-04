import type { Player } from "@/types/player";

export const datosJugadores: Player[] = [
    {
        id: 1,
        currentPrice: 120000000,
        name: "Messi",
        team: {
            id: 1,
            name: "Inter Miami",
            league: "MLS"
        },
        playerGameData: {
            id: 1,
            playerId: 1,
            goals: 30,
            shotsOnTarget: 45,
            tackles: 12,
            rating: 9.8,
        },
        tokens: [],
    },
    {
        id: 2,
        currentPrice: 110000000,
        name: "De Bruyne",
        team: {
            id: 2,
            name: "Manchester City",
            league: "Premier League"
        },
        playerGameData: {
            id: 2,
            playerId: 2,
            goals: 12,
            shotsOnTarget: 28,
            tackles: 10,
            rating: 9.4
        },
        tokens: [],
    },
    {
        id: 3,
        currentPrice: 95000000,
        team: {
            id: 3,
            name: "Real Madrid",
            league: "La Liga"
        },
        name: "Bellingham",
        playerGameData: {
            id: 3,
            playerId: 3,
            goals: 18,
            shotsOnTarget: 32,
            tackles: 20,
            rating: 9.2
        },
        tokens: [],
    },
    {
        id: 4,
        currentPrice: 87000000,
        team: {
            id: 4,
            name: "Bayern Munich",
            league: "Bundesliga"
        },
        name: "Kane",
        playerGameData: {
            id: 4,
            playerId: 4,
            goals: 25,
            shotsOnTarget: 40,
            tackles: 8,
            rating: 9.1,
        },
        tokens: [],
    },
    {
        id: 5,
        currentPrice: 78000000,
        team: {
            id: 5,
            name: "Barcelona",
            league: "La Liga"
        },
        name: "Yamal",
        playerGameData: {
            id: 5,
            playerId: 5,
            goals: 10,
            shotsOnTarget: 22,
            tackles: 9,
            rating: 8.9,
        },
        tokens: [],
    },
];
