import { useState, useEffect } from 'react';
import { PlayerCard } from "@/components/player/PlayerCard.tsx";
import type { Player } from "@/types/player";
import { getAllPlayers, getFiltredPlayers } from "@/services/PlayerService";
import { Loading } from "@/components/common/Loading";
import { ServerErrorComponent } from "@/components/ServerErrorComponent";
import type { HttpError } from "@/lib/http-error";
import { MessajeBox } from '@/components/common/MessageBox';
import { FilterSearch } from '@/components/common/FilterSearch';
import type { PlayerFilter } from '@/types/PlayerFilter';

const filtroPlaceholder = {
    clubName: "",
    league: "",
}

export default function CatalogoPage() {
    const [players, setPlayers] = useState<Player[]>([]);
    const [error, setError] = useState<HttpError | null>(null);
    const [loading, setLoading] = useState(true);

    const [filter,setFilter] = useState<PlayerFilter>(filtroPlaceholder);

    const handleSearch = (filtro:PlayerFilter) => {
        setLoading(true)

        getFiltredPlayers(filtro)
        .then(setPlayers)
        .catch((error: HttpError) => setError(error))
        .finally(()=>setLoading(false));
    };

    useEffect(() => {
        getAllPlayers()
            .then((data) => setPlayers(data))
            .catch((error: HttpError) => setError(error))
            .finally(() => setLoading(false));
    }, []);

    if (loading) return <Loading text="Cargando catálogo..." />
    if (error) return <ServerErrorComponent />

    return (
        <div className="flex flex-col flex-1 gap-10 w-full px-4 py-8 relative">
            {players.length > 0 ? (
                <>
                    <FilterSearch filter={filter} onChange={setFilter} onSearch={handleSearch}
                        filterOptions={[
                            {
                                key: "clubName",
                                label: "Equipo",
                            },
                            {
                                key: "league",
                                label: "Liga",
                            },
                        ]}
                        className='absolute z-10 right-0 -top-5'/>
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
