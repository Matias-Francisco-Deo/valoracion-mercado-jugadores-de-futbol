
export default function HomePage() {
  // const { user, isAuthenticated, logout } = useAuth();

  const datosJugadores = [
    {name: "Messi"}, {name: "Messi"}, {name: "Messi"}, {name: "Messi"}, {name: "Messi"}
  ]

  return (
      <div className="flex flex-col gap-10">
        <p className="text-2xl" >Top Jugadores</p>
        <div className="text-center text-3xl flex flex-col gap-10">
          <div className="flex gap-4">
          {datosJugadores.map(jugador =>
            <div>
              {jugador.name}
            </div>
          )}
          </div>
          <p>¡Pronto abriremos las puertas al tradeo de tokens!</p>
        </div>
      </div>

  );
};
