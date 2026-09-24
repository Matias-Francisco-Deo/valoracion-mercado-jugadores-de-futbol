import { MainPlayerInfo } from "@/components/player/MainPlayerInfo";
import type { Player } from "@/types/player";
import { useState } from "react";
import { useParams } from "react-router-dom";
import {ServerErrorComponent} from "@/components/ServerErrorComponent";
import { Loading } from "@/components/common/Loagind";
import NotFoundPage from "./NotFoundPage";
import { MetricBox } from "@/components/player/MetricBox";

//TODO borrar placeholder
    const placeholder = {
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
        }

export default function PlayerPage(){
    const { playerId } = useParams();
    const [player,setPlayer] = useState<Player>(placeholder);//agregar null al type y cambiar el placeholder por null
    const [error,setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false)

    const metrics = [
        { title: "Goles", value: player.goals },
        { title: "Tiros al arco", value: player.shotsOnTarget },
        { title: "Pases", value: player.passes },
        { title: "Entradas", value: player.tackles },
        { title: "Intercepciones", value: player.interceptions },
    ];
    
    if (loading) return <Loading text="Cargando jugador..." className="flex-1" />
    //if (error?.status === 404) return <NotFoundPage />
    //if ((error?.status && error.status >= 500) || !player) return <ServerErrorComponent/>

    return(
        <div className="flex flex-col self-center bg-gray-400 p-4 rounded-3xl">

            <MainPlayerInfo player={player} />
            <div className="flex flex-wrap max-w-[650px] justify-center gap-4">{/*cambiar por grid? */}
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