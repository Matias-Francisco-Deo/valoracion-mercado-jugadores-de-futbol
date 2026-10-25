import type { Player } from "@/types/player";
import { PlayerCard } from "../player/PlayerCard";
import { Pagination } from "../common/Pagination";
import type { PageResponse } from "@/types/inventory";
import type { HttpError } from "@/lib/http-error";

interface RankingSectionProps{
    playerPage: PageResponse<Player>|null;
    handlePageChange:(page: number) => void;
    error?: HttpError | null;
}

export const RankingSection = ({playerPage,handlePageChange,error}:RankingSectionProps) => {
    return(
        <div className="flex flex-col bg-[#f3f1f1] rounded-2xl gap-2">
            <div className="flex flex-col text-black gap-1 px-4">
                <h2 className="text-2xl font-titles tracking-tight sm:text-3xl">
                    Ranking
                </h2>

                <span className="text-sm sm:text-base">
                    Los mejores del fútbol
                </span>
            </div>
            <div className="border-t border-gray-300 min-h-42">
                {error ? (
                    <p className="py-6 text-center text-gray-500">
                        Hubo un error al obtener el ranking
                    </p>
                ) : playerPage && playerPage.content.length > 0 ? (
                    <>
                        <div className="p-4 grid w-full min-h-40 grid-cols-1 gap-4 self-center sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5">
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
                    <p className="py-6 text-center text-gray-500">
                        No hay jugadores rankeados en este momento
                    </p>
                )}
            </div>
        </div>
    )
}