import { useState, useEffect } from 'react';
import { PlayerCard } from "@/components/player/PlayerCard.tsx";
import type { Player } from "@/types/player";
import { getAllPlayers } from "@/services/PlayerService";
import { Loading } from "@/components/common/Loading";
import { ServerErrorComponent } from "@/components/ServerErrorComponent";
import type { HttpError } from "@/lib/http-error";
import { FilterMenu } from '@/components/common/FilterMenu';
import { MessajeBox } from '@/components/common/MessageBox';

export default function CatalogoPage() {
    const [players, setPlayers] = useState<Player[]>([]);
    const [selectedFilter, setSelectedFilter] = useState("");
    const [error, setError] = useState<HttpError | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        getAllPlayers()
            .then((data) => setPlayers(data))
            .catch((error: HttpError) => setError(error))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <Loading text="Cargando catálogo..." />
    //if (error) return <ServerErrorComponent />

    return (
        <div className="flex flex-col flex-1 gap-10 w-full p-4 relative">
            
                {players.length > 0 ? (
                    <>
                        <FilterMenu
                            filters={["Rating", "largo", "goles"]}
                            selectedFilter={selectedFilter}
                            onFilterChange={setSelectedFilter}
                            className="absolute right-0"
                        />

                        <div className="flex justify-center gap-8 flex-wrap">
                            {players.map(player => (
                                <PlayerCard
                                    key={player.id}
                                    player={player}
                                />
                            ))}
                        </div>
                    </>
                ) : (
                    <MessajeBox title='Catálogo de Jugadores' text='No hay jugadores disponibles en el catálogo en este momento.'
                    className='items-center'/>
                )}
        </div>
    );
}
