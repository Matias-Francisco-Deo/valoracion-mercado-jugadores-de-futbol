import {cn} from "@/lib/utils.ts";

export const PageWindow = ({ className, children }: React.ComponentProps<'div'>) => {
    return (
        <div
            className={cn("w-full flex-1 flex flex-col bg-white/30 p-8 rounded-lg shadow-md", className)}
        >
            {children}
            {/*<h1 className="text-3xl font-bold text-gray-900 mb-4">*/}
            {/*    Valoración de Mercado de Jugadores de Fútbol*/}
            {/*</h1>*/}
            {/*{(*/}
            {/*    <div className="space-y-4">*/}
            {/*        <p className="text-green-700 font-medium">*/}
            {/*            ¡Bienvenido, <span className="font-semibold">{"user.username"}</span>!*/}
            {/*        </p>*/}
            {/*        <p className="text-sm text-gray-600">*/}
            {/*            Sesión iniciada con: <span className="font-mono">{"user.email"}</span>*/}
            {/*        </p>*/}
            {/*        <button*/}
            {/*            onClick={() => {}}*/}
            {/*            className="mt-4 px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-md text-sm font-medium transition-colors cursor-pointer"*/}
            {/*        >*/}
            {/*            Cerrar Sesión*/}
            {/*        </button>*/}
            {/*    </div>*/}
            {/*)}*/}
        </div>
    )
}

