import { useEffect, useState } from 'react';
import { Plus, Flame } from 'lucide-react';
import { useDailyBonus } from '../../hooks/useDailyBonus';
import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { UserMenu } from './UserMenu';
import { LogoIcon } from '../../icon/LogoIcon';
import { PageLink } from '../ui/PageLink';
import { DepositCreditsModal } from './DepositCreditsModal';
import { DailyBonusModal } from '../bonus/DailyBonusModal';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const [isDepositModalOpen, setIsDepositModalOpen] = useState(false);
  const [isBonusModalOpen, setIsBonusModalOpen] = useState(false);
  const { status, isLoading: isBonusLoading, isClaiming, claim, isAvailable } = useDailyBonus();

  // Auto-open daily bonus modal once per session if the user hasn't claimed yet
  useEffect(() => {
    if (isAuthenticated && status && !status.alreadyClaimedToday && isAvailable) {
      const hasShown = sessionStorage.getItem('daily_bonus_auto_shown');
      if (!hasShown) {
        sessionStorage.setItem('daily_bonus_auto_shown', 'true');
        setIsBonusModalOpen(true);
      }
    }
  }, [isAuthenticated, status, isAvailable]);

  return (
    <header className="h-16 w-full shrink-0 bg-brand-orange shadow-md">
      <div className="h-full w-full mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between">

        <div className="flex items-center gap-4 lg:gap-6">
          <Link to="/" className="flex text-black items-center text-4xl font-bold tracking-wider hover:opacity-90 transition-opacity font-logo shrink-0">
            <LogoIcon />
            <span>Overcode</span>
          </Link>

          <nav className="hidden md:flex items-center gap-4 lg:gap-6">
            <PageLink className="bg-brand-orange font-medium hover:bg-[#E58600]" to="/catalogo">
              Catálogo
            </PageLink>
            <PageLink className="bg-brand-orange font-medium hover:bg-[#E58600]"  to="/market">
              Mercado
            </PageLink>
            {isAuthenticated && user && (
              <PageLink className="bg-brand-orange font-medium hover:bg-[#E58600]" to={`/inventario/${user.id}`}>
                Inventario
              </PageLink>
            )}
          </nav>
        </div>

        <div className="flex items-center">
          {isAuthenticated && user ? (
            <div className="flex items-center gap-3 sm:gap-4">

              <div className="hidden sm:flex flex-col items-center justify-center" data-testid="user-credits-navbar">
                <span className="text-xs">Creditos</span>
                <span className="text-xs font-semibold">${user.creditBalance}</span>
              </div>

              <span className="hidden sm:inline font-medium truncate max-w-30 sm:max-w-40"
                data-testid="user-name-navbar">
                {user.username}
              </span>

              <UserMenu user={user} onLogout={logout} />
            </div>
          ) : (
            <>
              <div className="md:hidden">
                <UserMenu user={null} onLogout={logout} />
              </div>

              <Link to="/login" className="hidden md:flex px-3 py-1.5 text-center font-medium text-white transition-colors hover:underline" >
                Iniciar sesión
              </Link>
            </>
          )}
        </div>
      </div>
      <DepositCreditsModal
        isOpen={isDepositModalOpen}
        onClose={() => setIsDepositModalOpen(false)}
      />
      <DailyBonusModal
        isOpen={isBonusModalOpen}
        onClose={() => setIsBonusModalOpen(false)}
        status={status}
        isLoading={isBonusLoading}
        isClaiming={isClaiming}
        onClaim={claim}
      />
    </header>
  );
};
