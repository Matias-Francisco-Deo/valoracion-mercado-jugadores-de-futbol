import type { Player } from "@/types/player"
import pelota from '@/assets/pelota.jpg'
import { cn } from "@/lib/utils"
import { 
    Star, 
    Coins, 
    Goal, 
    Crosshair, 
    ArrowRightLeft, 
    ShieldAlert, 
    Radar 
} from 'lucide-react'

export type MainPlayerInfoProps = {
    player: Player
} & React.ComponentProps<'div'>

export const MainPlayerInfo = ({player, className, ...props }: MainPlayerInfoProps) => {
    const stats = [
        { label: 'Goles', value: player.goals, icon: Goal },
        { label: 'Tiros al arco', value: player.shotsOnTarget, icon: Crosshair },
        { label: 'Pases', value: player.passes, icon: ArrowRightLeft },
        { label: 'Entradas', value: player.tackles, icon: ShieldAlert },
        { label: 'Intercepciones', value: player.interceptions, icon: Radar },
        { label: 'Precio', value: `$${player.currentPrice}`, icon: Coins },
    ];

    return(
        <div 
            key={player.id}
            className={cn(
                "relative w-full flex flex-col gap-8 p-6 md:p-8",
                "bg-gray-400 rounded-2xl shadow-lg border border-gray-500",
                "text-gray-900 font-sans overflow-hidden",
                className
            )} 
            {...props}
        >
            <div className="flex flex-col md:flex-row items-center md:items-start gap-8 relative z-10">
                
                {/* Avatar (Izquierda) */}
                <div className="relative shrink-0 flex items-center justify-center">
                    <div className="absolute w-48 h-48 bg-gray-500/30 rounded-full blur-xl" />
                    <div className="absolute w-40 h-40 bg-gray-500/40 rounded-full shadow-inner" />
                    <img 
                        src={pelota} 
                        alt={player.name} 
                        className={cn(
                            "relative z-10 w-32 h-32 md:w-40 md:h-40 object-cover",
                            "rounded-full border-4 border-gray-400 shadow-md"
                        )} 
                    />
                </div>

                {/* Información (Centro) */}
                <div className="flex flex-col items-center md:items-start text-center md:text-left flex-1 mt-2 md:mt-4">
                    <h2 className="text-4xl md:text-5xl font-bold uppercase tracking-wide">
                        {player.name}
                    </h2>
                    <span className="text-lg font-medium text-gray-700 tracking-wide uppercase mt-1">
                        {player.clubName}
                    </span>
                </div>

                {/* Rating (Derecha) */}
                <div className="flex flex-col items-center justify-center w-24 h-24 bg-gray-500/20 rounded-full border border-gray-500/50 shrink-0 mt-4 md:mt-0 shadow-inner md:ml-auto">
                    <Star className="w-5 h-5 text-amber-500 mb-1 fill-amber-500/20" />
                    <span className="text-[10px] text-gray-700 uppercase tracking-wider font-bold leading-none">
                        Rating
                    </span>
                    <span className="text-2xl font-bold text-gray-900 mt-1 leading-none">
                        {player.rating}
                    </span>
                </div>
            </div>

            {/* Cuadrícula de Estadísticas (Grid Section 3x2) */}
            <div className="grid grid-cols-2 md:grid-cols-3 bg-gray-500/20 mt-4 -mx-6 md:-mx-8 -mb-6 md:-mb-8">
                {stats.map((stat) => (
                    <div 
                        key={stat.label}
                        className="flex flex-col items-center justify-center p-6 gap-1"
                    >
                        <stat.icon className="w-6 h-6 text-gray-700 mb-1" />
                        <span className="text-xs text-gray-700 uppercase tracking-wider font-semibold text-center">
                            {stat.label}
                        </span>
                        <span className="text-2xl font-bold text-gray-900 mt-1">
                            {stat.value}
                        </span>
                    </div>
                ))}
            </div>
        </div>
    )
}