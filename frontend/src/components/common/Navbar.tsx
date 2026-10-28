import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Flame } from 'lucide-react';
import { useAuth } from '../../hooks/useAuth';
import { useDailyBonus } from '../../hooks/useDailyBonus';
import { UserMenu } from './UserMenu';
import { LogoIcon } from '../../icon/LogoIcon';
import { PageLink } from '../ui/PageLink';
import { DepositCreditsModal } from './DepositCreditsModal';
import { DailyBonusModal } from '../bonus/DailyBonusModal';

/**
 * Top navigation bar featuring the Overcode brand and login navigation link.
 * Styled with solid brand orange #FF9500.
 */
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
        <nav className="flex items-center gap-6">
          <Link to="/"
            className="flex text-black items-center text-4xl font-bold tracking-wider hover:opacity-90 transition-opacity font-logo">
            <LogoIcon />
            Overcode
          </Link>

          <PageLink className="bg-brand-orange font-medium hover:bg-[#E58600]" to="/catalogo">
            Catálogo 
          </PageLink>
          <PageLink className="bg-brand-orange font-medium hover:bg-[#E58600]" to={"/market"}>
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
            <div className='flex items-center gap-1'>
              {/* Daily Streak Bonus Trigger Button */}
              {isAvailable && (
                <button
                  type="button"
                  onClick={() => setIsBonusModalOpen(true)}
                  className="relative flex items-center gap-1.5 rounded-lg bg-black/10 hover:bg-black/20 px-3 py-1 text-black transition-all cursor-pointer mr-2 active:scale-95"
                  title="Ver progreso de racha diaria"
                >
                  <Flame
                    size={16}
                    className={
                      status?.alreadyClaimedToday
                        ? "text-orange-600 fill-orange-500"
                        : "text-red-600 fill-red-500 animate-pulse"
                    }
                  />
                  <div className="flex flex-col items-start leading-tight">
                    <span className="text-[10px] font-medium uppercase tracking-wider text-black/70">Racha</span>
                    <span className="text-sm font-bold">{status?.currentStreak ?? 0} {status?.currentStreak === 1 ? 'día' : 'días'}</span>
                  </div>
                  {!status?.alreadyClaimedToday && (
                    <span className="absolute -top-1 -right-1 flex h-2.5 w-2.5">
                      <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-red-400 opacity-75"></span>
                      <span className="relative inline-flex rounded-full h-2.5 w-2.5 bg-red-500"></span>
                    </span>
                  )}
                </button>
              )}

              <div className='flex items-center gap-2 rounded-lg bg-black/10 px-3 py-1 text-black mr-2'>
                <div className='flex flex-col items-start leading-tight'>
                  <span className='text-[10px] font-medium uppercase tracking-wider text-black/70'>Créditos</span>
                  <span className='text-sm font-bold'>${user.creditBalance}</span>
                </div>
                <button
                  type="button"
                  onClick={() => setIsDepositModalOpen(true)}
                  className="flex h-6 w-6 items-center justify-center rounded-full bg-black text-white hover:bg-black/80 transition-transform active:scale-95 cursor-pointer"
                  title="Cargar créditos"
                >
                  <Plus size={14} />
                </button>
              </div>
              <UserMenu user={user} onLogout={logout} />
            </div>
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
