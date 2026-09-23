import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { UserMenu } from './UserMenu';

/**
 * Top navigation bar featuring the Overcode brand and login navigation link.
 * Styled with solid brand orange #FF9500.
 */
export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  


  return (
    <header className="h-16 w-full shrink-0 bg-brand-orange shadow-md z-10">
      <div className="h-full w-full mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between">
        <Link
          to="/"
          className="text-white text-2xl font-bold tracking-tight hover:opacity-90 transition-opacity"
        >
          Overcode
        </Link>
        <nav>
          {!isAuthenticated || !user ? (
            <Link
              to="/login"
              className="rounded px-3 py-1.5 text-base font-medium text-white transition-colors hover:underline"
            >
              Iniciar sesión
            </Link>
          ) : (
            <UserMenu user={user} onLogout={logout} />
          )}
        </nav>
      </div>
    </header>
  );
};
