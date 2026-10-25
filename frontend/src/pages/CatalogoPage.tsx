import { useState, useEffect, useRef } from 'react';
import { getFiltredPlayers, getRankingPage } from "@/services/PlayerService";
import { Loading } from "@/components/common/Loading";
import { ServerErrorComponent } from "@/components/ServerErrorComponent";
import type { HttpError } from "@/lib/http-error";
import type { PlayerFilter, PlayerPageResponse } from '@/types/PlayerFilter';
import { useSearchParams } from 'react-router-dom';
import type { Player } from '@/types/player';
import type { PageResponse } from '@/types/inventory';
import {RankingSection} from '@/components/catalogo/RankingSection';
import { CatalogoSection } from '@/components/catalogo/CatalogoSection';
import { Button } from '@/components/ui/Button';

const filtroPlaceholder = {
    clubName: "",
    league: "",
    position: "",
}

export default function CatalogoPage() {
    const topRef = useRef<HTMLDivElement>(null);
    const [filter,setFilter] = useState<PlayerFilter>(filtroPlaceholder);
    const [searchParams, setSearchParams] = useSearchParams();
    const [playerPage, setPlayerPage] = useState<PlayerPageResponse | null>(null);

    const [error, setError] = useState<HttpError | null>(null);

    const [initialLoading, setInitialLoading] = useState(true);
    const [searchLoading, setSearchLoading] = useState(false);

    const [rankingPage, setRankingPage] = useState<PageResponse<Player>|null>(null);
    const [rankingError, setRankingError] = useState<HttpError | null>(null);

    const [activeSection, setActiveSection] = useState<"catalogo"|"ranking">("catalogo");

    const handleSearch = (filtro:PlayerFilter) => {

        const params: Record<string, string> = {};

        if (filtro.clubName) {
            params.clubName = filtro.clubName;
        }

        if (filtro.league) {
            params.league = filtro.league;
        }

        if (filtro.position) {
            params.position = filtro.position;
        }
        params.page = "0";
        setSearchParams(params);
    };

    const handlePageChange = (page: number) => {
        const params = new URLSearchParams(searchParams);

        params.set("page", page.toString());

        setSearchParams(params);
    };

    const handleRankingPageChange= (page:number) => {
        setRankingError(null);

        getRankingPage(page)
        .then((data) => setRankingPage(data))
        .catch((error: HttpError) => setRankingError(error));
    }

    useEffect(() => {
        getRankingPage()
        .then((data) => setRankingPage(data))
        .catch((error: HttpError) => setRankingError(error));
    },[]);

    useEffect(() => {
        topRef.current?.scrollIntoView({
            behavior: "smooth",
            block: "start",
        });
        
        const filtro: PlayerFilter = {
            clubName: searchParams.get("clubName") || "",
            league: searchParams.get("league") || "",
            position: searchParams.get("position") || "",
        };
        const page = Number(searchParams.get("page")) || 0;
        setSearchLoading(true);

        getFiltredPlayers(filtro,page)
            .then((data) => {
                setPlayerPage(data)
            })
            .catch((error: HttpError) => setError(error))
            .finally(() => {
                setFilter(filtro);
                setSearchLoading(false);
                setInitialLoading(false);
            });
    }, [searchParams]);

    if (initialLoading) return <Loading text="Cargando catálogo..." />
    if (error) return <ServerErrorComponent />

    return (
        <div className='flex w-full justify-center px-4 py-8' ref={topRef}>
            <div className="flex flex-col max-w-[77vw] gap-5 w-full bg-[#f3f1f1] rounded-2xl md:p-6">
                <div className="flex gap-2 text-[12px] lg:hidden px-6 pt-6 md:p-0">
                    <Button onClick={() => setActiveSection("catalogo")}
                        className={`flex-1 rounded-lg px-4 py-2 font-semibold ${
                            activeSection === "catalogo"
                            ? "bg-hover-orange"
                            : "bg-[#faa42b]"
                        }`}
                        >
                        Catálogo
                    </Button>

                    <Button onClick={() => setActiveSection("ranking")}
                        className={`flex-1 rounded-lg px-4 py-2 font-semibold ${
                            activeSection === "ranking"
                            ? "bg-hover-orange"
                            : "bg-[#faa42b]"
                        }`}
                        >
                        Ranking
                    </Button>
                </div>
                <div className={`${activeSection === "ranking" ? "flex" : "hidden"} flex-col gap-5 lg:flex`}>
                    <RankingSection
                        playerPage={rankingPage}
                        handlePageChange={handleRankingPageChange}
                        error={rankingError}
                        />
                </div>

                <div className="border border-gray-200 hidden lg:block" />

                <div className={`${activeSection === "catalogo" ? "flex" : "hidden"} flex-col gap-5 lg:flex`}>
                    <CatalogoSection
                        playerPage={playerPage}
                        filter={filter}
                        onFilterChange={setFilter}
                        onSearch={handleSearch}
                        handlePageChange={handlePageChange}
                        searchLoading={searchLoading}
                        />
                </div>
            </div>
        </div>
    );
}
