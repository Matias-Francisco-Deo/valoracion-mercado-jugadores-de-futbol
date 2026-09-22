import { Link } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

/**
 * Placeholder Home page view ('/') acting as the primary index route of the application.
 */
export default function HomePage() {
  const { user, isAuthenticated, logout } = useAuth();

  return (
    <div className="flex-1 flex flex-col items-center justify-center p-6 text-center bg-gray-50">
      <div className="max-w-md w-full bg-white p-8 rounded-lg shadow-md">
        <h1 className="text-3xl font-bold text-gray-900 mb-4">
          Valoración de Mercado de Jugadores de Fútbol
        </h1>
        {isAuthenticated && user ? (
          <div className="space-y-4">
            <p className="text-green-700 font-medium">
              ¡Bienvenido, <span className="font-semibold">{user.username}</span>!
            </p>
            <p className="text-sm text-gray-600">
              Sesión iniciada con: <span className="font-mono">{user.email}</span>
            </p>
            <button
              onClick={logout}
              className="mt-4 px-4 py-2 bg-gray-600 hover:bg-gray-700 text-white rounded-md text-sm font-medium transition-colors cursor-pointer"
            >
              Cerrar Sesión
            </button>
          </div>
        ) : (
          <div className="space-y-4">
            <p className="text-gray-600">
              Bienvenido a la plataforma. Regístrate para acceder al sistema de valoración de jugadores.
            </p>
            <div>
              <Link
                to="/register"
                className="inline-block px-6 py-2.5 bg-[#FF9500] hover:bg-[#e08500] text-white font-semibold rounded-md shadow-sm transition-colors"
              >
                Ir a Registrarse
              </Link>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};
