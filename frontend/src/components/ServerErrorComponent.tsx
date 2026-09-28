
export const ServerErrorComponent = () => {
  return (
    <div className="flex flex-1 w-full items-center justify-center">
        <div className="flex flex-col max-w-125 gap-4 bg-[#d9d9d9af] p-4 rounded">
          <div className="text-2xl font-bold text-center">
            Hubo un error inesperado
          </div>
          <div className="font-bold text-center">
            Estamos solucionando el problema.
            Estaremos de vuelta en breve.
          </div>
        </div>
    </div>
    
  )
}