import {calculateQuoteStats,filterQuotesByPeriod,formatAxisDate,formatDate,formatPrice,type Period,} from "@/lib/playerQuoteUtils";
import type { RegistroCotizacion } from "@/types/player";
import { TrendingUp } from "lucide-react";
import { useMemo, useState } from "react";
import {Area,AreaChart,CartesianGrid,ResponsiveContainer,Tooltip,XAxis,YAxis,} from "recharts";

interface Props {
    quotes: RegistroCotizacion[];
    loading?: boolean;
}

const PERIODS: { label: string; value: Period }[] = [
    { label: "1S", value: "7d" },
    { label: "1M", value: "30d" },
    { label: "3M", value: "90d" },
    { label: "1A", value: "1y" },
    { label: "Todo", value: "all" },
];

const currency = new Intl.NumberFormat("es-AR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
});

export function PlayerQuoteChart({
    quotes,
    loading = false,
}: Props) {
    const [period, setPeriod] = useState<Period>("30d");

    const data = useMemo(
        () => filterQuotesByPeriod(quotes, period),
        [quotes, period]
    );

    const stats = useMemo(
        () => calculateQuoteStats(data),
        [data]
    );

    if (loading) {
        return (
            <div className="flex min-w-0 flex-col gap-4 rounded-2xl bg-white p-3 shadow-sm sm:p-6">
                <h2 className="text-base font-semibold text-gray-900 sm:text-lg">
                    Evolución de cotización
                </h2>

                <p className="text-sm text-gray-500">
                    Cargando historial...
                </p>
            </div>
        );
    }

    return (
        <section className="min-w-0 rounded-2xl bg-white p-3 shadow-sm sm:p-6">
            <div className="flex min-w-0 flex-col gap-3 md:flex-row md:items-end md:justify-between md:gap-4">
                <div className="flex min-w-0 flex-col gap-2">
                    <h2 className="flex gap-4 text-base font-semibold text-gray-900 sm:text-lg">
                        <TrendingUp className="text-orange-500 sm:size-8"/>
                        Evolución de cotización
                    </h2>

                    {stats && (
                        <div className="flex min-w-0 flex-wrap items-center gap-x-2 gap-y-1">
                            <span className="text-xl font-bold text-gray-900 wrap-anywhere sm:text-2xl">
                                {formatPrice(stats.last)}
                            </span>

                            {stats.variation !== null && (
                                <span className={`shrink-0 rounded-full px-2 py-1 text-xs font-semibold sm:text-sm ${
                                        stats.variation < 0
                                            ? "bg-red-50 text-red-600"
                                            : "bg-orange-50 text-orange-600"
                                    }`}>
                                    {stats.variation > 0 ? "+" : ""}
                                    {stats.variation.toFixed(2)}%
                                </span>
                            )}
                        </div>
                    )}

                    <p className="text-xs text-gray-500 sm:text-sm">
                        Variación del período seleccionado
                    </p>
                </div>

                <div className="flex flex-wrap gap-1 md:shrink-0 md:justify-end">
                    {PERIODS.map((item) => (
                        <button
                            key={item.value}
                            type="button"
                            onClick={() => setPeriod(item.value)}
                            aria-pressed={period === item.value}
                            className={`rounded-lg px-2.5 py-1.5 text-xs font-medium transition sm:px-3 sm:py-2 sm:text-sm ${
                                period === item.value
                                    ? "bg-orange-500 text-white"
                                    : "bg-gray-100 text-gray-600 hover:bg-orange-50 hover:text-orange-700"
                            }`}>
                            {item.label}
                        </button>
                    ))}
                </div>
            </div>

            {!stats ? (
                <div className="flex min-h-48 items-center justify-center px-2 pt-4 text-center text-sm text-gray-500">
                    No hay cotizaciones disponibles para este período.
                </div>
            ) : (
                <>
                    {/* Gráfico*/}
                    <div className="h-56 min-w-0 pt-4 max-[349px]:h-48 sm:h-80 sm:pt-6">
                        <ResponsiveContainer width="100%" height="100%">
                            <AreaChart data={data} margin={{top: 8,right: 4,bottom: 0,left: 0,}}>

                                <defs>
                                    <linearGradient id="quoteGradient" x1="0" y1="0" x2="0" y2="1">
                                        <stop offset="0%" stopColor="#f97316" stopOpacity={0.30}/>
                                        <stop offset="95%" stopColor="#f97316" stopOpacity={0.02}/>
                                    </linearGradient>
                                </defs>

                                <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#e5e7eb"/>

                                <XAxis dataKey="createdAt" tickFormatter={formatAxisDate} tick={{fontSize: 10,fill: "#6b7280"}} tickLine={false}
                                    axisLine={false} minTickGap={24} tickMargin={8}/>

                                <YAxis domain={["auto", "auto"]}
                                    tickFormatter={(value: number) =>
                                        currency.format(value)
                                    }
                                    tick={{fontSize: 10,fill: "#6b7280"}} tickLine={false} axisLine={false} width={48} tickMargin={4}/>

                                <Tooltip
                                    labelFormatter={(label) =>
                                        formatDate(String(label))
                                    }
                                    formatter={(value) => [
                                        formatPrice(Number(value)),
                                        "Cotización",
                                    ]}
                                    contentStyle={{
                                        borderRadius: 10,
                                        border: "1px solid #fed7aa",
                                        fontSize: 12,
                                        padding: "8px 10px",
                                    }}
                                    labelStyle={{
                                        color: "#374151",
                                        fontWeight: 600,
                                    }}
                                />

                                <Area  type="monotone" dataKey="cotizacion"  name="Cotización" stroke="#f97316" strokeWidth={2.5} fill="url(#quoteGradient)"
                                    dot={data.length <= 30
                                            ? {
                                                r: 3,
                                                fill: "#f97316",
                                                stroke: "#ffffff",
                                                strokeWidth: 1.5,
                                            } : false
                                    }
                                    activeDot={{
                                        r: 5,
                                        fill: "#f97316",
                                        stroke: "#ffffff",
                                        strokeWidth: 2,
                                    }}
                                    isAnimationActive={false}
                                />
                            </AreaChart>
                        </ResponsiveContainer>
                    </div>

                    <div className="grid grid-cols-3 gap-1 border-t border-gray-100 pt-3 sm:gap-3 sm:pt-4">
                        <div className="flex min-w-0 flex-col gap-1">
                            <p className="text-[10px] text-gray-500 sm:text-xs">
                                Inicial
                            </p>
                            <p className="wrap-break-word text-xs font-semibold text-gray-900 sm:text-sm">
                                {formatPrice(stats.first)}
                            </p>
                        </div>

                        <div className="flex min-w-0 flex-col gap-1">
                            <p className="text-[10px] text-gray-500 sm:text-xs">
                                Mínimo
                            </p>
                            <p className="wrap-break-word text-xs font-semibold text-gray-900 sm:text-sm">
                                {formatPrice(stats.min)}
                            </p>
                        </div>

                        <div className="flex min-w-0 flex-col gap-1">
                            <p className="text-[10px] text-gray-500 sm:text-xs">
                                Máximo
                            </p>
                            <p className="wrap-break-word text-xs font-semibold text-gray-900 sm:text-sm">
                                {formatPrice(stats.max)}
                            </p>
                        </div>
                    </div>
                </>
            )}
        </section>
    );
}