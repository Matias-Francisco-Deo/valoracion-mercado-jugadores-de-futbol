import { Link } from 'react-router-dom'



export default function NotFoundPage() {
  return (
    <div className="flex flex-col max-w-125 self-center gap-4 items-center justify-center bg-[#d9d9d9af] p-4 rounded">
      <div className="text-2xl font-bold text-center">
        Esta página no está disponible.
      </div>
      <div className="text-center">
        Es posible que el enlace sea incorrecto o que se haya eliminado la página.{" "}
        <Link to="/" className="link text-center text-[#f0a337] font-bold transition-colors hover:text-brand-orange">
        Volver a Instagram
      </Link>
      </div>
    </div>
  )
}