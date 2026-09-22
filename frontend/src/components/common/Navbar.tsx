import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';

/**
 * Top navigation bar featuring the Overcode brand and login navigation link.
 * Styled with solid brand orange #FF9500.
 */
export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    setIsMenuOpen(false);
    navigate('/');
  };

  return (
    <header className="h-16 w-full shrink-0 bg-brand-orange shadow-md z-10">
      <div className="h-full w-full mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between">
        <Link
          to="/"
          className="text-white text-2xl font-bold tracking-tight hover:opacity-90 transition-opacity"
        >
          Overcode
        </Link>
        <nav className="relative">
          {!isAuthenticated || !user ? (
            <Link
              to="/login"
              className="rounded px-3 py-1.5 text-base font-medium text-white transition-colors hover:underline"
            >
              Iniciar sesión
            </Link>
          ) : (
            <div className="relative">
              <button
                type="button"
                onClick={() => setIsMenuOpen((open) => !open)}
                aria-expanded={isMenuOpen}
                aria-haspopup="menu"
                className="flex max-w-[calc(100vw-8rem)] items-center gap-2 rounded px-3 py-1.5 text-base font-medium text-white transition-colors hover:bg-[#e08500]"
              >
                <span aria-hidden="true">●</span>
                <span className="truncate">{user.username}</span>
              </button>

              {isMenuOpen && (
                <div
                  role="menu"
                  className="absolute right-0 top-full z-20 mt-2 w-64 max-w-[calc(100vw-2rem)] rounded-md bg-white p-3 text-gray-900 shadow-xl"
                >
                  <div className="border-b border-gray-200 px-2 pb-3">
                    <p className="truncate font-semibold">{user.username}</p>
                    <p className="wrap-break-word text-sm text-gray-600">{user.email}</p>
                  </div>
                  <button
                    type="button"
                    role="menuitem"
                    onClick={handleLogout}
                    className="mt-2 flex w-full items-center gap-2 rounded px-2 py-2 text-left text-sm font-medium text-gray-800 transition-colors hover:bg-gray-100"
                  >
                    <span aria-hidden="true">↪</span>
                    Cerrar sesión
                  </button>
                </div>
              )}
            </div>
          )}
        </nav>
      </div>
    </header>
  );
};
