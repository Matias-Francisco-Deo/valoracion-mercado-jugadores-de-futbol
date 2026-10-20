import type { PositionToken } from "@/types/inventory";
import { PositionCard } from "./PositionCard";
import { useState } from "react";

interface PortfolioPositionsSectionProps{
    positions: PositionToken[];

}

export default function PortfolioPositionsSection({positions}:PortfolioPositionsSectionProps){
    const [selectedPosition,setSelectedPosition] = useState<PositionToken|null>(null);

    const handleSelectPosition = (position: PositionToken) => {
        setSelectedPosition((current) =>
            current?.playerId === position.playerId ? null : position
        );
    };

    return(
        <div className="flex w-full flex-col gap-4">

            <div className="flex w-full flex-col bg-[#f3f1f1] gap-2 rounded-2xl p-4 sm:p-6">
                <span className="text-black text-xl font-medium">Posiciones del porfolio</span>
                <div className="grid w-full min-h-40 grid-cols-1 gap-4 self-center sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 border-t border-gray-200 pt-4">
                    {positions.length > 0 ? (
                        positions.map((position) => (
                        <PositionCard  key={position.playerId} position={position} onSelect={handleSelectPosition} 
                        isSelected={selectedPosition?.playerId === position.playerId}/>
                            ))
                    ) : (
                        <p className="col-span-full text-center text-gray-500 py-6">
                            No tienes tokens para calcular la posicion.
                        </p>
                    )}
                </div>
            </div>
            
        </div>
    )
}