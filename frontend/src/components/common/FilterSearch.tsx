import { useState } from "react";
import type { ComponentProps } from "react";
import type { PlayerFilter } from "@/types/PlayerFilter";
import { cn } from "@/lib/utils";
import { Input } from "../ui/Input";
import { Button } from "../ui/Button";

type FilterOption = {
    key: keyof PlayerFilter;
    label: string;
};

type FilterSearchProps = ComponentProps<"div"> & {
    filter: PlayerFilter;
    filterOptions: FilterOption[];
    onChange: (filter: PlayerFilter) => void;
    onSearch: (filter: PlayerFilter) => void;
    position?: "left" | "right";
};

export const FilterSearch = ({filter,filterOptions,onChange,onSearch,position = "right",className,...props}: FilterSearchProps) => {
    const [selectedFilter, setSelectedFilter] = useState(filterOptions[0]);

    const [open, setOpen] = useState(false);
    const [value, setValue] = useState("");

    const handleChange = (value: string) => {
        setValue(value);
        onChange({ 
            ...filter,
            [selectedFilter.key]: value,
        });
    };

    const handleSearch = () => {
        onSearch(filter);
    };

    return (
        <div className={cn("flex max-w-md",position === "right" && "flex-row-reverse",className)}{...props}>
            <div className="relative shrink-0 min-w-21">
                <Button
                    type="button"
                    onClick={() => setOpen((current) => !current)}
                    className="w-full"
                >
                    {selectedFilter.label}
                </Button>
                {open && (
                    <div className={cn("absolute top-full z-50 min-w-full border rounded-b-md bg-brand-orange shadow-md",
                            position === "left"
                                ? "left-0"
                                : "right-0"
                        )}
                    >
                        {filterOptions.map((option) => (
                            <Button
                                key={option.key}
                                type="button"
                                onClick={() => {
                                    setSelectedFilter(option);
                                    setValue("");
                                    setOpen(false);
                                    onChange({clubName:"",league:""});
                                }}
                                className={cn("block w-full whitespace-nowrap px-3 py-2 text-center text-sm")
                                }>
                                {option.label}
                            </Button>
                        ))}
                    </div>
                )}
            </div>

            <Input value={value} placeholder={`Buscar por ${selectedFilter.label}`}
                onChange={(event) =>
                    handleChange(event.target.value)
                }
                onKeyDown={(event) => {
                    if (event.key === "Enter") {
                        handleSearch();
                    }
                }}
                className={cn("min-w-0 max-w-50 flex-1 bg-brand-orange",
                    position === "left"
                        ? "rounded-l-none"
                        : "rounded-r-none"
                )}
            />
        </div>
    );
};
