import {Link} from 'react-router-dom';
import {useAuth} from '../../hooks/useAuth';
import {UserMenu} from './UserMenu';
import {LogoIcon} from '../../icon/LogoIcon';
import { PageLink } from '../ui/PageLink';

/**
 * Top navigation bar featuring the Overcode brand and login navigation link.
 * Styled with solid brand orange #FF9500.
 */
export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  


  return (
    <header className="h-16 w-full shrink-0 bg-brand-orange shadow-md">
      <div className="h-full w-full mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between">
        <nav className="flex items-center gap-6">
          <Link  to="/"
            className="flex text-black items-center text-4xl font-bold tracking-wider hover:opacity-90 transition-opacity font-logo">
            <LogoIcon/>
            Overcode
          </Link>

          <PageLink className="bg-brand-orange font-medium hover:bg-[#E58600]" to="/catalogo">
            Catálogo 
          </PageLink>
          <PageLink className="bg-brand-orange font-medium hover:bg-[#E58600]" to={"market"}>
              Mercado
          </PageLink>
          {isAuthenticated && user && (
            <PageLink className="bg-brand-orange font-medium hover:bg-[#E58600]" to={`/inventario/${user.id}`}>
              Inventario
            </PageLink>
          )}
        </nav>
        
        <div>
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
        </div>
      </div>
    </header>
  );
};
