import type { RegistroCotizacion } from "@/types/player";

export type Period = "7d" | "30d" | "90d" | "1y" | "all";

const currency = new Intl.NumberFormat("es-AR", {
    minimumFractionDigits: 2,
    maximumFractionDigits: 2,
});

export function formatPrice(value: number): string {
    return `$ ${currency.format(value)}`;
}

export function formatDate(value: string): string {
    return new Date(value).toLocaleDateString("es-AR", {
        day: "2-digit",
        month: "short",
        year: "numeric",
    });
}

export function formatAxisDate(value: string): string {
    return new Date(value).toLocaleDateString("es-AR", {
        day: "2-digit",
        month: "2-digit",
    });
}

export function filterQuotesByPeriod(quotes: RegistroCotizacion[],period: Period): RegistroCotizacion[] {
    const sorted = quotes
        .filter((quote) =>
                Number.isFinite(quote.cotizacion) &&
                Number.isFinite(Date.parse(quote.createdAt))
        )
        .sort((a, b) =>Date.parse(a.createdAt) - Date.parse(b.createdAt));

    if (period === "all" || sorted.length === 0) {
        return sorted;
    }

    const days: Record<Exclude<Period, "all">, number> = {
        "7d": 7,
        "30d": 30,
        "90d": 90,
        "1y": 365,
    };

    const lastDate = Date.parse(sorted[sorted.length - 1].createdAt);

    const cutoff = lastDate - days[period] * 24 * 60 * 60 * 1000;

    return sorted.filter((quote) => Date.parse(quote.createdAt) >= cutoff);
}

export interface QuoteStats {
    first: number;
    last: number;
    min: number;
    max: number;
    variation: number | null;
}

export function calculateQuoteStats(quotes: RegistroCotizacion[]): QuoteStats | null {
    if (quotes.length === 0) return null;

    const prices = quotes.map((quote) => quote.cotizacion);
    const first = prices[0];
    const last = prices[prices.length - 1];

    return {
        first,
        last,
        min: Math.min(...prices),
        max: Math.max(...prices),
        variation:
            first !== 0
                ? ((last - first) / first) * 100
                : null,
    };
}
