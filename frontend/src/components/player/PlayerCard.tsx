import { cn } from '@/lib/utils'
import React from "react";
import type {Player} from "@/types/player.ts";
import { Link } from 'react-router-dom';

export type PlayerCardProps = {
    player: Player
} & React.ComponentProps<'div'>

export const PlayerCard = ({player, className, ...props }: PlayerCardProps) => {
    return (
        <div
            className={cn(className,
                "bg-gray-400 aspect-poster flex-1 " +
                "text-lg min-w-40 max-w-80 text-start p-4 " +
                "flex flex-col gap-4 rounded-lg justify-between",)}
            {...props}
        >
            <div className="text-xl">
                {player.name} - {player.clubName}
                <Link to={`/p/${player.id}`} className="text-red-500 hover:underline">
                    Ver detalles
                </Link>
            </div>
            <div className="flex flex-col justify-between">
                <div>Goles: {player.goals}</div>
                <div>Pases: {player.passes}</div>
                <div>Rating: {player.rating}</div>
                <div>Precio actual: {player.currentPrice}</div>
            </div>
        </div>
    )
}