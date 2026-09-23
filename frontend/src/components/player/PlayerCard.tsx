import { cn } from '@/lib/utils'
import React from "react";
import type {Player} from "@/types/player.ts";

export type PlayerCardProps = {
    player: Player
} & React.ComponentProps<'div'>

export const PlayerCard = ({player, className, ...props }: PlayerCardProps) => {
    return (
        <div
            className={cn(className,
                "bg-gray-400 aspect-poster flex flex-col flex-1" +
                "text-lg w-60 text-start p-4 " +
                " gap-4 rounded-lg justify-between",)}
            {...props}
        >
            <div className="text-xl">
                {player.name} - {player.clubName}
            </div>
            <div className="flex flex-col justify-between text-lg">
                <div>Goles: {player.goals}</div>
                <div>Pases: {player.passes}</div>
                <div>Rating: {player.rating}</div>
                <div>Precio actual: {player.currentPrice}</div>
            </div>
        </div>
    )
}