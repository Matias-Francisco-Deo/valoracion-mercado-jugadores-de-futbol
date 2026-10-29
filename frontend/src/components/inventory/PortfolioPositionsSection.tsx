import type { PositionToken } from "@/types/inventory";
import { PositionCard } from "./PositionCard";
import { useState } from "react";
import { usePagination } from "@/hooks/usePagination";
import { Pagination } from "../common/Pagination";

interface PortfolioPositionsSectionProps {
    positions: PositionToken[];
    selectedPlayerId?: number | null;
    onSelectPlayerId?: (playerId: number) => void;
}

export default function PortfolioPositionsSection({
    positions,
    selectedPlayerId,
    onSelectPlayerId,
}: PortfolioPositionsSectionProps) {
    const [selectedPosition,setSelectedPosition] = useState<PositionToken|null>(null);

    const {currentPage,totalPages,paginatedItems,goToPage} = usePagination({
        items: positions,
    });

    const handleSelectPosition = (position: PositionToken) => {
        setSelectedPosition((current) =>
            current?.playerId === position.playerId ? null : position
        );
    };

    return(
        <div className="flex w-full flex-col gap-4">

            <div className="flex w-full flex-col bg-[#f3f1f1] gap-2 rounded-2xl p-4 sm:p-6">
                <span className="text-black text-xl font-medium">Posiciones del portfolio</span>

                <div className="w-full border-t border-gray-200 pt-4">
                    {paginatedItems.length > 0 ? (
                        <div className="grid w-full grid-cols-1 gap-4 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 p-1">
                            {paginatedItems.map((position) => (
                                <PositionCard
                                    key={position.playerId}
                                    position={position}
                                    onSelect={() => (onSelectPlayerId ? onSelectPlayerId(position.playerId) : handleSelectPosition(position))}
                                    isSelected={
                                        selectedPlayerId !== undefined
                                            ? selectedPlayerId === position.playerId
                                            : selectedPosition?.playerId === position.playerId
                                    }
                                />
                            ))}
                        </div>
                    ) : (
                        <p className="py-6 text-center text-gray-500">
                            No tienes tokens para calcular la posición.
                        </p>
                    )}
                </div>

                <Pagination
                    currentPage={currentPage}
                    totalPages={totalPages}
                    onPageChange={goToPage}
                />
            </div>
            
        </div>
    )
}