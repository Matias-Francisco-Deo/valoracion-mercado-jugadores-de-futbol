import { useState, useEffect } from 'react';
import { PlayerCard } from "@/components/player/PlayerCard.tsx";
import { getFiltredPlayers } from "@/services/PlayerService";
import { Loading } from "@/components/common/Loading";
import { ServerErrorComponent } from "@/components/ServerErrorComponent";
import type { HttpError } from "@/lib/http-error";
import { MessajeBox } from '@/components/common/MessageBox';
import { FilterSearch } from '@/components/common/FilterSearch';
import type { PlayerFilter, PlayerPageResponse } from '@/types/PlayerFilter';
import { useSearchParams } from 'react-router-dom';
import { Pagination } from '@/components/common/Pagination';

const filtroPlaceholder = {
    clubName: "",
    league: "",
    position: "",
}

export default function CatalogoPage() {
    const [filter,setFilter] = useState<PlayerFilter>(filtroPlaceholder);
    const [searchParams, setSearchParams] = useSearchParams();
    const [playerPage, setPlayerPage] = useState<PlayerPageResponse | null>(null);

    const [error, setError] = useState<HttpError | null>(null);

    const [initialLoading, setInitialLoading] = useState(true);
    const [searchLoading, setSearchLoading] = useState(false);

    const handleSearch = (filtro:PlayerFilter) => {

        const params: Record<string, string> = {};

        if (filtro.clubName) {
            params.clubName = filtro.clubName;
        }

        if (filtro.league) {
            params.league = filtro.league;
        }

        /*if (filtro.position) {
            params.position = filtro.position;
        }*/
        params.page = "0";
        setSearchParams(params);
    };

    const handlePageChange = (page: number) => {
        const params = new URLSearchParams(searchParams);

        params.set("page", page.toString());

        setSearchParams(params);
    };


    useEffect(() => {
        const filtro: PlayerFilter = {
            clubName: searchParams.get("clubName") || "",
            league: searchParams.get("league") || "",
            position: searchParams.get("position") || "",
        };
        const page = Number(searchParams.get("page")) || 0;
        setSearchLoading(true);

        getFiltredPlayers(filtro,page)
            .then((data) => {
                setPlayerPage(data)
            })
            .catch((error: HttpError) => setError(error))
            .finally(() => {
                setFilter(filtro);
                setSearchLoading(false);
                setInitialLoading(false);
            });
    }, [searchParams]);

    if (initialLoading) return <Loading text="Cargando catálogo..." />
    if (error) return <ServerErrorComponent />

    return (
        <div className="flex flex-col flex-1 gap-10 w-full px-4 py-8 relative">
            <FilterSearch filter={filter} onChange={setFilter} onSearch={handleSearch}
                    filterOptions={[
                        {key: "clubName",label: "Equipo",},
                        {key: "league",label: "Liga",},
                        {key: "position",label: "Posición",},
                    ]}
                    className='absolute z-50 right-0 -top-5'/>
            {searchLoading ? (
                <Loading text="Buscando jugadores..." />
            ):( playerPage && playerPage.content.length > 0 ? (
                <>
                    <div className="flex justify-center gap-8 flex-wrap">
                        {playerPage.content.map(player => (
                            <PlayerCard
                                key={player.id}
                                player={player}
                            />
                        ))}
                    </div>
                    <Pagination
                        currentPage={playerPage?.number ?? 0}
                        totalPages={playerPage?.totalPages ?? 0}
                        onPageChange={handlePageChange}/>
                </>
                ) : (
                    <MessajeBox title='Catálogo de Jugadores' text='No hay jugadores disponibles en el catálogo en este momento.'
                    className='items-center'/>
            ))}
            
        </div>
    );
}
