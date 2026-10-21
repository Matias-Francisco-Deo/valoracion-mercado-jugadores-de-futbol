import { cn } from "@/lib/utils";
import type { PositionToken } from "@/types/inventory";

type  PositionCardProps =  {
    position:PositionToken;
    isSelected?: boolean;
    onSelect:(position: PositionToken) => void;
    className?:string;
}

export const PositionCard= ({ position , isSelected, onSelect, className, ...props }: PositionCardProps) =>{
    const isProfit = position.profitLoss >= 0;
    const invested = position.quantity * position.averagePurchasePrice;
    const currentValue = position.quantity * position.currentPrice;

    const profitPercentage = invested !== 0 ? (position.profitLoss / invested) * 100 : 0;

    return(
        <button type="button" aria-pressed={isSelected} onClick={() => onSelect(position)}
        className={cn('flex min-w-0 flex-col max-w-67 gap-2 p-4 rounded-2xl border border-gray-500 bg-gray-400 shadow-lg transition-transform hover:-translate-y-1 hover:shadow-xl text-black',isSelected
        ? "border-hover-orange ring-2 ring-hover-orange"
        : "border-gray-300"
        , className)} {...props}>

            <header className="flex items-center gap-3">
                <div className="min-w-0 flex-1">
                    <h3 className="truncate font-titles text-start font-semibold  text-wrap">
                        {position.playerName}
                    </h3>
                </div>

                <div className="flex flex-col gap-1 text-[11px]">
                    <span className={`shrink-0 rounded-lg px-2.5 py-1.5  font-semibold ${
                            isProfit
                            ? "bg-green-900/15 text-green-800"
                            : "bg-red-900/15 text-red-800"
                        }`}
                        >
                        {isProfit ? "+" : ""}
                        {profitPercentage.toFixed(2)}%
                    </span>
                    <span className={`shrink-0 rounded-lg px-2.5 py-1.5 text-[12px] font-semibold ${
                            isProfit
                            ? "bg-green-900/15 text-green-800"
                            : "bg-red-900/15 text-red-800"
                        }`}
                        >
                        {isProfit ? "+" : ""}
                        ${position.profitLoss}
                    </span>
                </div>
            </header>

            <div className="flex items-center justify-between gap-2">
                <p className="text-sm text-start text-gray-700">
                    Valor actual: 
                </p>

                <p className="text-md sm:text-xl  font-semibold tracking-tight">
                    {currentValue}
                </p>
            </div>

            <div className=" border-t border-gray-500/60" />

            {/* Position details */}
            <section className="grid grid-cols-2 gap-3">
                <div className="text-[12px] sm:text-sm rounded-xl flex flex-col justify-center items-center bg-gray-500/20 p-2">
                    <p className=" text-gray-700">Cantidad</p>
                    <p className="font-semibold">
                        {position.quantity}
                    </p>
                </div>

                <div className="rounded-xl text-[12px] sm:text-sm flex flex-col justify-center items-center bg-gray-500/20 p-2">
                    <p className="text-gray-700">
                        Precio promedio
                    </p>
                    <p className="wrap-break-words font-semibold">
                        {position.averagePurchasePrice}
                    </p>
                </div>
            </section>
            <div className="col-span-2 flex items-center justify-between wrap-break-words gap-3 pt-1">
                <p className="text-[11px] sm:text-sm text-start font-medium text-gray-700">
                    Precio actual por unidad
                </p>
                <p className="text-sm font-semibold">
                    ${position.currentPrice}
                </p>
            </div>

        </button>
    )
}