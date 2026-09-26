import type { Player } from "@/types/player"
import pelota from '@/assets/pelota.jpg'
import { cn } from "@/lib/utils"


export type MainPlayerInfoProps = {
    player: Player
} & React.ComponentProps<'div'>

export const MainPlayerInfo = ({player, className, ...props }: MainPlayerInfoProps) => {
    return(
        <div key={player.id} 
        className={cn("flex flex-wrap gap-8 text-2xl p-4 justify-center" , className)} {...props}>  
            <img src={pelota} alt={player.name} 
            className="w-full max-w-50 rounded-b-full" />
            <div className="flex flex-col gap-2 justify-around">
                <div className="flex flex-col">
                    <h2 className="text-4xl font-PlayerName">{player.name}</h2>
                    <span>{player.clubName}</span>
                </div>
                
                <span>Precio token: ${player.currentPrice}</span>
            </div>
            <p className="flex flex-col gap-1 items-center">
                Rating{" "}
                <span>{player.rating}</span>
            </p>
            
        </div>
    )
}