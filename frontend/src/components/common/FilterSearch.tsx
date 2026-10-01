import { cn } from "@/lib/utils";
import type { PlayerFilter } from "@/types/PlayerFilter";
import { useState, type ComponentProps } from "react";
import { Button } from "../ui/Button";
import { Menu } from "lucide-react";
import { Field } from "../ui/Field";

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
    const [open, setOpen] = useState(false);
const [values, setValues] = useState<PlayerFilter>(filter);

    const handleChange = (key: keyof PlayerFilter,value: string) => {
        setValues((current) => ({
            ...current,
            [key]: value,
        }));
    };

    const handleSearch = () => {
        onChange(values);
        onSearch(values);
        setOpen(false);
    };

    const clearFiltersAndSearch = () => {
        const emptyFilter: PlayerFilter = {
            clubName: "",
            league: "",
        };

        setValues(emptyFilter);
        onChange(emptyFilter);
        onSearch(emptyFilter);
        setOpen(false);
    };

    return (
        <div className={cn( "relative flex",
                position === "left"
                    ? "justify-start"
                    : "justify-end",
                className
            )} {...props}>
            <Button onClick={() => setOpen((current) => !current)}
                className={cn("flex h-10 items-center gap-2 bg-brand-orange px-3",
                    position === "left"
                        ? "rounded-r-md"
                        : "flex-row-reverse rounded-l-md"
                )}>
                <Menu className="h-5 w-5" />
                <span>Filtros</span>
            </Button>

            {open && (
                <div className={cn("flex flex-col gap-2 absolute top-full z-50 w-72 border bg-brand-orange p-4 shadow-md",
                        position === "left"
                            ? "left-0 rounded-b-md rounded-r-md"
                            : "right-0 rounded-b-md rounded-l-md"
                    )}>
                    {filterOptions.map((option) => (
                        <Field  key={option.key}
                            label={option.label}
                            value={values[option.key] ?? ""}
                            onChange={(event) =>
                                handleChange(
                                    option.key,
                                    event.target.value
                                )
                            }
                            onKeyDown={(event) => {
                                if (event.key === "Enter") {
                                    handleSearch();
                                }
                            }}
                            inputClassName="bg-[#faa42b]"/>
                    ))}

                    <div className="flex gap-2">
                        <Button onClick={clearFiltersAndSearch} className="flex-1 bg-[#faa42b]">
                            Limpiar
                        </Button>

                        <Button onClick={handleSearch} className="flex-1 bg-[#faa42b]">
                            Buscar
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
};
