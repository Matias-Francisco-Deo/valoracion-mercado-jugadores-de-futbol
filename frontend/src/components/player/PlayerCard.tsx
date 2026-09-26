import { cn } from '@/lib/utils'
import type {Player} from "@/types/player.ts";
import { Link, type LinkProps } from 'react-router-dom';

interface PlayerCardProps extends Omit<LinkProps, 'to'> {
    player: Player
}

export const PlayerCard = ({player, className, ...props }: PlayerCardProps) => {
    return (
        <Link
            key={player.id}
            to={`/p/${player.id}`}
            className={cn(className,
                "bg-gray-400 aspect-poster flex flex-col flex-1" +
                "text-lg w-60 min-w-40 text-start p-4 " +
                " gap-4 rounded-lg justify-between",)}
            {...props}
        >
            <div>
                <div className="text-xl">
                    {player.name}
                </div>
                <div className="text-sm">
                    {player.clubName}
                </div>
            </div>
            <img src="src/assets/pelota.jpg"
                className="rounded-b-full min-w-30 max-w-50" alt="Pelota"/>
            <div className="flex flex-col justify-between text-lg">
                <div>Goles: {player.goals}</div>
                <div>Pases: {player.passes}</div>
                <div>Rating: {player.rating}</div>
                <div>Precio actual: {player.currentPrice}</div>
            </div>
        </Link>
    )
}