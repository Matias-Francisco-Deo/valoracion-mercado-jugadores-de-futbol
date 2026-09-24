import {Link} from 'react-router-dom';
import {useAuth} from '../../hooks/useAuth';
import {UserMenu} from './UserMenu';
import {LogoIcon} from './LogoIcon';

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
          className="flex text-black items-center text-4xl font-bold tracking-wider hover:opacity-90 transition-opacity font-logo"
        >
          <LogoIcon/>
          Overcode
        </Link>
        <nav className="flex justify-end">
          {!isAuthenticated || !user ? (
            <Link
              to="/login"
              className="px-3 py-1.5 text-center font-medium text-white flex transition-colors hover:underline"
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
