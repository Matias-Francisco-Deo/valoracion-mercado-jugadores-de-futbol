import { cn } from "@/lib/utils"

interface MetricBoxProps extends React.ComponentProps<'div'> {
    title: string;
    value: string | number;
}

export const MetricBox = ({title, value, className, ...props}:MetricBoxProps) => {
    return (
        <div className={cn("flex flex-col gap-2 rounded-4xl items-center justify-center", className)} {...props}>
            <span className="text-lg font-medium">
                {title}
            </span>
            <span className="text-2xl font-bold">
                {value}
            </span>
        </div>
    )
}