
import { useEffect, useState } from "react";
import { cn } from "@/lib/utils";

type NumericInputProps = Omit<React.ComponentProps<"input">,"type" | "value" | "defaultValue" | "onChange"> & {
    value: number;
    onChange: (value: number) => void;
    min?: number;
    max?: number;
    integer?: boolean;
    fallbackValue?: number;
};

export const NumericInput = ({className,value,onChange,min = 0,max,integer = true,fallbackValue,onBlur,...props}: NumericInputProps) => {

    const fallback = fallbackValue ?? min;//valor que se establece si el input queda vacío o inválido

    const [inputValue, setInputValue] = useState(String(value));

  // Sincronizar cuando el valor cambia desde el padre
    useEffect(() => {
        setInputValue(String(value));
    }, [value]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = e.target.value;

        // Permitir únicamente dígitos enteros
        const pattern = integer ? /^\d*$/ : /^\d*\.?\d*$/;

        if (!pattern.test(newValue)) return;

        setInputValue(newValue);

        // No actualizar el padre mientras esté vacío
        if (newValue === "") return;

        const number = Number(newValue);

        if (!Number.isFinite(number)) return;

        // No permitir superar el máximo
        if (max !== undefined && number > max) {
            setInputValue(String(max));
            onChange(max);
            return;
        }

        onChange(number);
    };

    const handleBlur = (e: React.FocusEvent<HTMLInputElement>) => {
        const number = Number(inputValue);

        let finalValue = number;

        // Recuperar el valor si está vacío o es inválido
        if (inputValue === "" ||!Number.isFinite(number) ||(integer && !Number.isInteger(number))) {
            finalValue = fallback;
        }

        // Aplicar límites
        finalValue = Math.max(min, finalValue);

        if (max !== undefined) {
        finalValue = Math.min(max, finalValue);
        }

        setInputValue(String(finalValue));

        if (finalValue !== value) {
            onChange(finalValue);
        }

        onBlur?.(e);
    };

    return (
        <input {...props}
        type="text"
        inputMode={integer ? "numeric" : "decimal"}
        value={inputValue}
        onChange={handleChange}
        onBlur={handleBlur}
        className={cn("w-full rounded-lg border border-gray-300 px-3 py-2 outline-none transition focus:border-brand-orange",
            "disabled:cursor-not-allowed disabled:opacity-70",className)}
        />
    );
};
