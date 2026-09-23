import { MainPlayerInfo } from "@/components/player/MainPlayerInfo";
import type { Player } from "@/types/player";
import { useState } from "react";
import { useParams } from "react-router-dom";
import {ServerErrorComponent} from "@/components/ServerErrorComponent";
import { Loading } from "@/components/common/Loagind";
import NotFoundPage from "./NotFoundPage";

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
    
    if (loading) return <Loading text="Cargando jugador..." className="flex-1" />
    //if (error?.status === 404) return <NotFoundPage />
    //if ((error?.status && error.status >= 500) || !player) return <ServerErrorComponent/>

    return(
        <div className="flex flex-col max-w-2/4 self-center justify-center bg-[#D9D9D9] p-4 rounded">
            
            <div className="self-left">
                <MainPlayerInfo player={player} />
                
            </div>
            <p>This is the Player page.</p>
        </div>
    )
}