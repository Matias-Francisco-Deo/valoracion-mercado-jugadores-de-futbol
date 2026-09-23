
export default function ServerErrorComponent() {
  return (
    <div className="flex flex-col max-w-125 gap-4 self-center justify-center bg-[#d9d9d9af] p-4 rounded">
      <div className="text-2xl font-bold text-center">
        Hubo un error inesperado
      </div>
      <div className="font-bold text-center">
        Estamos solucionando el problema.
        Estaremos de vuelta en breve.
      </div>
    </div>
  )
}