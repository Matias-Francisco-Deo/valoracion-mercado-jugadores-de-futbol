import { cn } from "@/lib/utils";
import { useState, type ComponentProps } from "react";

type FilterMenuProps = ComponentProps<'div'> & {
    filters: string[];
    selectedFilter:string;
    onFilterChange:(filtro:string) => void;
}

export const FilterMenu = ({filters, selectedFilter, onFilterChange , className, ...props}: FilterMenuProps) => {
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div className={cn("relative w-48", className)} {...props}>
            <button type="button"
                onClick={() => setIsOpen((prev) => !prev)}
                className="flex h-10 w-full items-center justify-between rounded-md border border-brand-orange bg-brand-orange px-3 text-sm">
                    {selectedFilter}
            </button>

        {isOpen && (
            <div className="absolute left-0 top-full z-50 w-full overflow-hidden rounded-b border border-y-0 bg-brand-orange shadow-lg">
            {filters
                .filter((filter) => filter !== selectedFilter)
                .map((filter) => (
                <button
                    key={filter}
                    type="button"
                    onClick={() => {
                    onFilterChange(filter);
                    setIsOpen(false);
                    }}
                    className="w-full px-3 py-2 text-left text-sm hover:bg-[#e08500] border-0 border-b"
                >
                    {filter}
                </button>
                ))}
            </div>
        )}
        </div>
    );
}