import { cn } from "@/lib/utils"

interface MessajeBoxProps extends React.ComponentProps<'div'> {
    title:string,
    text?: string
}

export const MessajeBox = ({title , text, className, ...props }: MessajeBoxProps) => {
    return(
        <div className={cn("flex flex-col flex-1 justify-center", className)} {...props}>
            <h2 className="text-3xl font-bold">{title}</h2>
            <p className="text-xl py-10">{text}</p>
        </div>
    )
}