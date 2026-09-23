import {PlayerCard} from "@/components/player/PlayerCard.tsx";
import type {Player} from "@/types/player.ts";

export default function HomePage() {
  // const { user, isAuthenticated, logout } = useAuth();

    const datosJugadores: Player[] = [
        {
            id: 1,
            currentPrice: 120000000,
            clubName: "Inter Miami",
            name: "Messi",
            goals: 30,
            shotsOnTarget: 45,
            passes: 80,
            tackles: 12,
            rating: 9.8,
            interceptions: 6,
        },
        {
            id: 2,
            currentPrice: 110000000,
            clubName: "Manchester City",
            name: "De Bruyne",
            goals: 12,
            shotsOnTarget: 28,
            passes: 92,
            tackles: 10,
            rating: 9.4,
            interceptions: 14,
        },
        {
            id: 3,
            currentPrice: 95000000,
            clubName: "Real Madrid",
            name: "Bellingham",
            goals: 18,
            shotsOnTarget: 32,
            passes: 85,
            tackles: 20,
            rating: 9.2,
            interceptions: 11,
        },
        {
            id: 4,
            currentPrice: 87000000,
            clubName: "Bayern Munich",
            name: "Kane",
            goals: 25,
            shotsOnTarget: 40,
            passes: 70,
            tackles: 8,
            rating: 9.1,
            interceptions: 5,
        },
        {
            id: 5,
            currentPrice: 78000000,
            clubName: "Barcelona",
            name: "Yamal",
            goals: 10,
            shotsOnTarget: 22,
            passes: 78,
            tackles: 9,
            rating: 8.9,
            interceptions: 7,
        },
    ];

  return (
      <div className="flex flex-col gap-10">
        <p className="text-2xl" >Top 5 Jugadores</p>
        <div className="text-center text-3xl flex flex-col gap-10">
          <div className="flex justify-center gap-8 flex-wrap">
          {datosJugadores.map(player =>
            <PlayerCard player={player}>

            </PlayerCard>
          )}
          </div>
          <p>¡Pronto abriremos las puertas al tradeo de tokens!</p>
        </div>
      </div>

  );
};
