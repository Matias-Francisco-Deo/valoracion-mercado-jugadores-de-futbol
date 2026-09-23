import type { Player } from "@/types/player"

export type MainPlayerInfoProps = {
player: Player
} & React.ComponentProps<'div'>

export const MainPlayerInfo = ({player, className, ...props }: MainPlayerInfoProps) => {
    return(
        <div className={className} {...props}>                    
            <h2>{player?.name}</h2>
            <p>{player?.clubName}</p>
            <p>$ {player?.currentPrice}</p>
            <p>Rating: {player?.rating}</p>
        </div>
    )
}