import { cn } from '@/lib/utils';
import type { Player } from '@/types/player.ts';
import { Link, type LinkProps } from 'react-router-dom';
import { Goal, ArrowRightLeft, Star, DollarSign } from 'lucide-react';

interface PlayerCardProps extends Omit<LinkProps, 'to'> {
    player: Player;
}

export const PlayerCard = ({ player, className, ...props }: PlayerCardProps) => {
    const stats = [
        { label: 'Goles', value: player.goals, icon: Goal },
        { label: 'Pases', value: player.passes, icon: ArrowRightLeft },
        { label: 'Rating', value: player.rating, icon: Star },
        { label: 'Precio', value: player.currentPrice, icon: DollarSign },
    ];

    return (
        <Link
            to={`/p/${player.id}`}
            className={cn(
                "group relative flex flex-col w-full max-w-sm overflow-hidden",
                "bg-gray-400 rounded-2xl shadow-lg transition-transform hover:-translate-y-1 hover:shadow-xl",
                "border border-gray-500",
                className
            )}
            {...props}
        >
            {/* Cabecera (Top) */}
            <div className="flex flex-col items-center pt-6 pb-2 px-4 text-center z-10">
                <h2 className="text-2xl font-bold text-gray-900 uppercase tracking-wide">
                    {player.name}
                </h2>
                <span className="text-sm font-medium text-gray-700 mt-1">
                    {player.clubName}
                </span>
            </div>

            {/* Centro (Imagen con círculo) */}
            <div className="relative flex justify-center items-center py-6">
                <div className="absolute w-40 h-40 bg-gray-500/30 rounded-full blur-xl group-hover:bg-gray-500/50 transition-colors" />
                <div className="absolute w-32 h-32 bg-gray-500/40 rounded-full shadow-inner" />
                
                <img 
                    src="src/assets/pelota.jpg" 
                    alt="Pelota"
                    className="relative z-10 w-28 h-28 object-cover rounded-full shadow-md border-4 border-gray-400"
                />
            </div>

            {/* Estadísticas (Bottom) */}
            <div className="grid grid-cols-2 sm:grid-cols-4 bg-gray-500/20 mt-auto">
                {stats.map((stat, index) => (
                    <div 
                        key={stat.label} 
                        className="flex flex-col items-center justify-center py-4 px-2 gap-1"
                    >
                        <stat.icon className="w-5 h-5 text-gray-700 mb-1" />
                        <span className="text-xs text-gray-700 uppercase tracking-wider font-semibold">
                            {stat.label}
                        </span>
                        <span className="text-lg font-bold text-gray-900">
                            {stat.value}
                        </span>
                    </div>
                ))}
            </div>
        </Link>
    );
};