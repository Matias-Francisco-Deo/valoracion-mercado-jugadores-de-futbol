import {PlayerCard} from "@/components/player/PlayerCard.tsx";
import {PageLink} from "@/components/ui/PageLink.tsx";
import { useEffect, useState } from "react";
import type { HttpError } from "@/lib/http-error";
import { getRankingPage } from "@/services/PlayerService";
import { ServerErrorComponent } from "@/components/ServerErrorComponent";
import { MessajeBox } from "@/components/common/MessageBox";
import { Loading } from "@/components/common/Loading";
import type { PlayerPageResponse } from "@/types/PlayerFilter";

export default function HomePage() {
  const [players,setPlayers] = useState<PlayerPageResponse | null>(null);
  const [error,setError] = useState<HttpError  | null>(null);
  const [loading, setLoading] = useState(true)

  useEffect(()=>{
    getRankingPage()
    .then(setPlayers)
    .catch((e: HttpError) =>setError(e))
    .finally(() => setLoading(false))
    
  },[]);

  if (loading) return <Loading text="Cargando..." />

  if (error?.status === 500) return <ServerErrorComponent />

  return (
      <div className="flex flex-col gap-10 ">
        <p className="text-2xl">Top 5 Jugadores</p>
        <div className="text-center text-3xl flex flex-col gap-10 min-h-80 sm:min-h-[425px]">
          {players && players.content.length === 0 ? (
            <MessajeBox title="No hay jugadores disponibles en este momento."/>
          ) : (
        <div className="flex justify-center gap-8 flex-wrap">
          {players && players.content.map(player => (
            <PlayerCard key={player.id} player={player} />
          ))}
        </div>
        )}
        </div>
          <div>
            <PageLink className="bg-brand-orange text-lg" to="/catalogo"> Ver más </PageLink>
          </div>
      </div>

  );
};