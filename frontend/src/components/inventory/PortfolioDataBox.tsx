import { cn } from "@/lib/utils";
import type { ComponentProps } from "react";

type  PortfolioDataBoxrops = ComponentProps<"div"> & {
    title:string;
    value:string;
    message?:string;
}

export const PortfolioDataBox = ({ title, value, message, className, ...props }: PortfolioDataBoxrops) => {
    return(
        <section className={(cn("flex flex-col bg-[#f3f1f1] rounded-2xl p-4 gap-3 overflow-x-auto",className))} {...props}>
            <h3 className="text-gray-600 text-[9px] uppercase">{title}</h3>
            <span className="text-3xl font-medium text-black">{value}</span>
            <span className="text-gray-400 text-[10px]">{message}</span>
        </section>
    )
}