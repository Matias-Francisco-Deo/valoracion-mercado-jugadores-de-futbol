import { PlayerCard } from "@/components/player/PlayerCard";
import { FilterSearch } from "@/components/common/FilterSearch";
import { Loading } from "@/components/common/Loading";
import { Pagination } from "@/components/common/Pagination";
import type { PlayerFilter, PlayerPageResponse } from "@/types/PlayerFilter";

interface CatalogoSectionProps {
    playerPage: PlayerPageResponse | null;
    filter: PlayerFilter;
    onFilterChange: (filter: PlayerFilter) => void;
    onSearch: (filter: PlayerFilter) => void;
    handlePageChange: (page: number) => void;
    searchLoading: boolean;
}

export const CatalogoSection = ({playerPage,filter,onFilterChange,onSearch,handlePageChange,searchLoading}: CatalogoSectionProps) => {
    return (
        <div className="flex flex-col border-b border-gray-300 bg-[#f3f1f1] rounded-2xl gap-3 px-4 pb-2">

            <div className="flex flex-col sm:px-4">
                <div className="flex justify-between gap-2 ">
                    <h2 className="text-2xl text-black font-titles tracking-tight sm:text-3xl">
                        Catálogo
                    </h2>

                    <FilterSearch
                    filter={filter}
                    onChange={onFilterChange}
                    onSearch={onSearch}
                    filterOptions={[
                        { key: "clubName", label: "Equipo" },
                        { key: "league", label: "Liga" },
                        { key: "position", label: "Posición" },
                    ]}
                    className="self-start text-sm"
                />
                </div>

                <span className="text-[13px] sm:text-base text-black">
                    Explorá los jugadores disponibles
                </span>
                

            </div>

            <div className="flex flex-col justify-center border-t border-gray-300 min-h-42">
                {searchLoading ? (
                    <Loading text="Buscando jugadores..." />
                ) : playerPage && playerPage.content.length > 0 ? (
                    <>
                        <div className="py-4 grid w-full min-h-40 grid-cols-1 gap-4 self-center sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
                            {playerPage.content.map((player) => (
                                <PlayerCard
                                    key={player.id}
                                    player={player}
                                />
                            ))}
                        </div>

                        <Pagination
                            currentPage={playerPage.number}
                            totalPages={playerPage.totalPages}
                            onPageChange={handlePageChange}
                        />
                    </>
                ) : (
                    <p className="py-6 text-center text-gray-500 text-[13px] sm:text-base">
                        No hay jugadores en el catalogo en este momento
                    </p>
                )}
            </div>
        </div>
    );
}