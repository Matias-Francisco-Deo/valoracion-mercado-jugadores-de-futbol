import { MainPlayerInfo } from "@/components/player/MainPlayerInfo";
import type { Player, RegistroCotizacion } from "@/types/player";
import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import {ServerErrorComponent} from "@/components/ServerErrorComponent";
import { Loading } from "@/components/common/Loading";
import NotFoundPage from "./NotFoundPage";
import type { HttpError } from "@/lib/http-error";
import { getPlayerById, getPlayerQuotes } from "@/services/PlayerService";
import { PlayerQuoteChart } from "@/components/player/PlayerQuoteChart";
import { PageLink } from "@/components/ui/PageLink";
import { TrendingUp } from "lucide-react";

export default function PlayerPage(){
    const { playerId } = useParams();
    const [player,setPlayer] = useState<Player|null>(null);
    const [quotes, setQuotes] = useState<RegistroCotizacion[]>([]);

    const [error,setError] = useState<HttpError  | null>(null);
    const [loading, setLoading] = useState(true)

    useEffect(() => {
            if (!playerId) return

            getPlayerById(playerId)
            .then(setPlayer)
            .catch((error: HttpError) => setError(error))

            getPlayerQuotes(playerId)
            .then(setQuotes)
            .catch((error: HttpError) => setError(error))
            .finally(() => setLoading(false))
        }, [playerId])

    if (loading) return <Loading text="Cargando jugador..." className="flex-1" />
    if (error?.status === 404) return <NotFoundPage />
    if ((error?.status && error.status >= 500) || !player) return <ServerErrorComponent/>

    return(
        <div className="w-full sm:max-w-[77vw] flex flex-col flex-1 justify-center gap-6 self-center bg-[#f3f1f1] py-2 sm:p-6 sm:rounded-3xl">
            <div className="flex flex-col md:flex-row gap-6">
                <MainPlayerInfo player={player} />
                <div className="border-t md:border-gray-300 md:py-2">
                    <div className="rounded-2xl border border-[#ec952377] overflow-hidden">
                        <div className="flex items-center gap-3 text-black text-sm bg-[#f09c2f3b] p-3">
                            <div className="flex size-9 shrink-0 items-center justify-center rounded-full bg-orange-500/15">
                                <TrendingUp className="size-5 text-orange-500" />
                            </div>

                            <p>
                                Explorá los tokens disponibles y elegí los tuyos.
                            </p>
                        </div>

                        <div className="flex p-2 justify-center bg-[#f09c2f3b] md:bg-transparent">
                            <PageLink className="w-full text-center bg-orange-500" to={"/market"}>
                                Ir al mercado
                            </PageLink>
                        </div>
                    </div>

                </div>
            </div>

            <PlayerQuoteChart quotes={quotes}/>
        </div>
    )
}