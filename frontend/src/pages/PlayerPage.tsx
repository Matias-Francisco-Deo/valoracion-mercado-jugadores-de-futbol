import { MainPlayerInfo } from "@/components/player/MainPlayerInfo";
import type { Player } from "@/types/player";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {ServerErrorComponent} from "@/components/ServerErrorComponent";
import { Loading } from "@/components/common/Loading";
import NotFoundPage from "./NotFoundPage";
import { MetricBox } from "@/components/player/MetricBox";
import type { HttpError } from "@/lib/http-error";
import { getPlayerById } from "@/services/PlayerService";

//TODO borrar placeholder
    const placeholder = {
            id: 1,
            currentPrice: 120000000,
            clubName: "Inter Miami",
            name: "Leonel Messi",
            goals: 30,
            shotsOnTarget: 45,
            passes: 80,
            tackles: 12,
            rating: 9.8,
            interceptions: 6,
        }

export default function PlayerPage(){
    const { playerId } = useParams();
    const [player,setPlayer] = useState<Player|null>(null);
    const [error,setError] = useState<HttpError  | null>(null);
    const [loading, setLoading] = useState(true)

    useEffect(() => {
            if (!playerId) return
            getPlayerById(playerId)
            .then(setPlayer)
            .catch((error: HttpError) => setError(error))
            .finally(() => setLoading(false))
        }, [playerId])

    if (loading) return <Loading text="Cargando jugador..." className="flex-1" />
    if (error?.status === 404) return <NotFoundPage />
    if ((error?.status && error.status >= 500) || !player) return <ServerErrorComponent/>

    const metrics = [
        { title: "Goles", value: player.goals },
        { title: "Tiros al arco", value: player.shotsOnTarget },
        { title: "Pases", value: player.passes },
        { title: "Entradas", value: player.tackles },
        { title: "Intercepciones", value: player.interceptions },
    ];

    return(
        <div className="w-full max-w-[700px] flex flex-col self-center bg-gray-400 p-4 rounded-3xl">

            <MainPlayerInfo player={player} />
            <div className="flex flex-wrap justify-center gap-4">
            {metrics.map((metric) => (
                <MetricBox
                    key={metric.title}
                    title={metric.title}
                    value={metric.value}
                    className="w-full max-w-[180px] aspect-square bg-gray-500"
                />
            ))}
        </div>

        </div>
    )
}