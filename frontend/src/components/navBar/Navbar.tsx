import { useEffect, useState } from 'react';
import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { useDailyBonus } from '../../hooks/useDailyBonus';
import { UserMenu } from './UserMenu';
import { LoginStreak } from './LoginStreak';
import { CreditsSection } from './CreditsSection';
import { LogoIcon } from '../../icon/LogoIcon';
import { PageLink } from '../ui/PageLink';
import { DepositCreditsModal } from '../common/DepositCreditsModal';
import { DailyBonusModal } from '../bonus/DailyBonusModal';

export const Navbar: React.FC = () => {
  const { user, isAuthenticated, logout } = useAuth();
  const [isDepositModalOpen, setIsDepositModalOpen] = useState(false);
  const [isBonusModalOpen, setIsBonusModalOpen] = useState(false);
  const { status, isLoading: isBonusLoading, isClaiming, claim, isAvailable } = useDailyBonus();

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

          <nav className="hidden lg:flex items-center gap-4 lg:gap-6">
            <PageLink className="bg-brand-orange font-medium hover:bg-[#E58600]" to="/catalogo">
              Catálogo
            </PageLink>
            <PageLink className="bg-brand-orange font-medium hover:bg-[#E58600]" to="/market">
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

              <LoginStreak  status={status} onClick={() => setIsBonusModalOpen(true)} className="hidden sm:flex"/>

              <CreditsSection  user={user} onDepositClick={() => setIsDepositModalOpen(true)} className="hidden sm:flex" data-testid="user-credits-navbar"/>

              <span className="hidden sm:inline font-medium truncate max-w-30 sm:max-w-40"
                data-testid="user-name-navbar">
                {user.username}
              </span>

              <UserMenu user={user} onLogout={logout} bonusStatus={status} onOpenBonusModal={() => setIsBonusModalOpen(true)} 
              onOpenDepositModal={() => setIsDepositModalOpen(true)}/>
            </div>
          ) : (
            <>
              <Link to="/login" className="hidden md:flex px-3 py-1.5 text-center font-medium text-white transition-colors hover:underline" >
                Iniciar sesión
              </Link>
              
              <div className="lg:hidden">
                <UserMenu user={null} onLogout={logout} />
              </div>
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
