import { useState, useMemo } from 'react';
import { PlayerCard } from "@/components/player/PlayerCard.tsx";
import { datosJugadores } from "@/data/mockJugadores.ts";
import { Input } from "@/components/ui/Input.tsx";

export default function CatalogoPage() {
    const [leagueFilter, setLeagueFilter] = useState<string>('');
    const [teamFilter, setTeamFilter] = useState<string>('');
    const filteredPlayers = useMemo(() => {
        return datosJugadores.filter(player => {
            const matchLeague = leagueFilter ? true : true; 
            
            const matchTeam = teamFilter 
                ? player.clubName.toLowerCase().includes(teamFilter.toLowerCase())
                : true;
                
            return matchLeague && matchTeam;
        });
    }, [leagueFilter, teamFilter]);

    return (
        <div className="flex flex-col gap-10 w-full">
            <h1 className="text-3xl font-bold text-center">Catálogo de Jugadores</h1>
            <div className="flex flex-col md:flex-row gap-6 justify-center items-end bg-gray-100 text-black p-6 rounded-lg">
                <div className="flex flex-col gap-2 w-full md:w-1/3">
                    <label htmlFor="league" className="text-sm font-semibold">Liga</label>
                    <select
                        id="league"
                        className="border-border w-full rounded border px-4 py-2 focus:outline-none"
                        value={leagueFilter}
                        onChange={(e) => setLeagueFilter(e.target.value)}
                    >
                        <option value="">Todas las ligas</option>
                        <option value="premier">Premier League</option>
                        <option value="laliga">LaLiga</option>
                        <option value="mls">MLS</option>
                    </select>
                </div>
                
                <div className="flex flex-col gap-2 w-full md:w-1/3">
                    <label htmlFor="team" className="text-sm font-semibold">Equipo</label>
                    <Input
                        id="team"
                        type="text"
                        placeholder="Buscar por equipo..."
                        value={teamFilter}
                        onChange={(e) => setTeamFilter(e.target.value)}
                    />
                </div>
            </div>

            <div className="flex justify-center gap-8 flex-wrap">
                {filteredPlayers.length > 0 ? (
                    filteredPlayers.map(player => (
                        <PlayerCard key={player.id} player={player} />
                    ))
                ) : (
                    <div className="text-center text-xl text-gray-500 py-10">
                        No se encontraron jugadores con esos filtros.
                    </div>
                )}
            </div>
        </div>
    );
}
