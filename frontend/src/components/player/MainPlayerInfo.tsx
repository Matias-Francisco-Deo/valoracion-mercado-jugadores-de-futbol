import type { Player } from "@/types/player"
import pelota from '@/assets/pelota.jpg'
import { cn } from "@/lib/utils"
import { Star, Coins, Goal, Crosshair,ShieldAlert, Shield} from 'lucide-react'
import { formatPrice } from "@/lib/playerQuoteUtils"

export type MainPlayerInfoProps = {
    player: Player
} & React.ComponentProps<'div'>

export const MainPlayerInfo = ({player,className,...props}: MainPlayerInfoProps) => {
    const stats = [
        { label: "Goles", value: player.playerGameData.goals, icon: Goal },
        { label: "Tiros al arco", value: player.playerGameData.shotsOnTarget, icon: Crosshair },
        { label: "Entradas", value: player.playerGameData.tackles, icon: ShieldAlert },
    ];

    return (
        <div className={cn("w-full min-w-0 overflow-hidden rounded-2xl border border-gray-300 bg-white p-3 lg:p-5 text-gray-700",className)}{...props}>
            {/* Grid único: la distribución cambia según el breakpoint */}
            <div className="grid min-w-0 grid-cols-[auto_minmax(0,1fr)] gap-3 lg:gap-x-5">
                {/* Imagen */}
                <img src={pelota} alt={player.name} className="row-span-1 size-16 shrink-0 rounded-full border border-gray-200 object-cover min-[350px]:size-20 lg:row-span-3 lg:h-full lg:min-h-full lg:w-45 lg:self-stretch lg:rounded-xl"/>

                {/* Nombre, equipo, posición y rating */}
                <div className="flex min-w-0 items-start justify-between gap-2">
                    <div className="flex min-w-0 flex-col gap-1">
                        <h2 className="wrap-break-words text-base font-bold uppercase leading-tight text-gray-950 min-[350px]:text-lg lg:text-2xl">
                            {player.name}
                        </h2>

                        <div className="flex min-w-0 items-center gap-1.5">
                            <Shield className="size-4 sm:size-8 shrink-0 text-gray-400 fill-gray-200" />
                            <span className="wrap-break-words text-xs font-medium text-gray-600 min-[350px]:text-lg lg:text-base">
                                {player.team.name}
                            </span>
                        </div>

                        <span className="text-xs text-gray-500 lg:text-lg">
                            {player.playerGameData.position}
                        </span>
                    </div>

                    <div className="flex shrink-0 items-center gap-1">
                        <Star className="size-4 fill-orange-500 text-orange-500 lg:size-5" />
                        <span className="text-lg font-bold tabular-nums text-gray-950 min-[350px]:text-base lg:text-xl">
                            {player.playerGameData.rating}
                        </span>
                    </div>
                </div>

                {/* Estadísticas: un único bloque */}
                <div className="col-span-2 grid min-w-0 grid-cols-3 gap-2 lg:col-span-1 lg:col-start-2 lg:gap-3 border-t border-gray-200 pt-2 lg:pb-2">
                    {stats.map(({ label, value, icon: Icon }) => (
                        <div key={label} className="flex min-w-0 items-start gap-1">
                            <Icon className="size-6 shrink-0 text-gray-500" />
                            
                            <div className="flex min-w-0 flex-col gap-1 items-center">
                                <span className="wrap-break-words text-[10px] leading-tight text-gray-500 min-[350px]:text-sm">
                                    {label}
                                </span>
                                <span className="wrap-break-words text-lg font-bold leading-tight tabular-nums text-gray-950 lg:text-xl">
                                    {value}
                                </span>
                            </div>
                        </div>
                    ))}
                </div>

                {/* Precio: un único bloque */}
                <div className="col-span-2 flex min-w-0 items-center gap-2 lg:col-span-1 lg:col-start-2 pt-2 border-t border-gray-200">
                    <Coins className="size-5 shrink-0 text-orange-500" />
                    <div className="flex min-w-0 flex-col gap-1">
                        <span className="text-xs font-medium text-gray-500">
                            Precio
                        </span>
                        <span className="wrap-break-words text-lg font-bold leading-tight tabular-nums text-gray-950 lg:text-xl">
                            {formatPrice(player.currentPrice)}
                        </span>
                    </div>
                </div>
            </div>
        </div>
    );
};