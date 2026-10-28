import { useState, useRef, useEffect, type ComponentProps } from 'react';
import { useNavigate } from 'react-router-dom';
import { Menu } from 'lucide-react';
import { Button } from '../ui/Button';
import { PageLink } from '../ui/PageLink';
import type { UserProfile } from '@/types/auth';

type UserMenuProps = ComponentProps<'div'> & {
    user?: UserProfile | null;
    onLogout?: () => void;
};

export const UserMenu = ({ user, onLogout, className = '', ...props }: UserMenuProps) => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const menuRef = useRef<HTMLDivElement>(null);
    const navigate = useNavigate();

    const handleLogout = () => {
        if (onLogout) {
            onLogout();
        }
        setIsMenuOpen(false);
        navigate('/');
    };

    const handleNavigate = () => {
        setIsMenuOpen(false);
    };

    // Cerrar al clickear fuera del menu
    useEffect(() => {
        const handleClickOutside = (event: MouseEvent) => {
            if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
                setIsMenuOpen(false);
            }
        };

        if (isMenuOpen) {
            document.addEventListener('mousedown', handleClickOutside);
        }
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, [isMenuOpen]);

    return (
        <div ref={menuRef} className={`relative ${className}`} {...props}>
            <Button
                onClick={() => setIsMenuOpen((open) => !open)}
                aria-expanded={isMenuOpen}
                aria-label="Menú"
                data-testid="hamburger-menu"
                className="flex items-center justify-center p-2 rounded-md hover:bg-hover-orange transition-colors  border-none shadow-none cursor-pointer">
                <Menu className="h-6 w-6" aria-hidden="true" />
            </Button>

            {isMenuOpen && (
                <div role="menu" className="absolute right-0 top-full z-50 mt-2 w-64 max-w-[calc(100vw-2rem)] rounded-md bg-[#E58600] p-3 shadow-xl border border-black/10 flex flex-col gap-2">
                    {user ? (
                        <>
                            {/* PageLinks visibles por debajo de md */}
                            <div className="flex flex-col gap-2 md:hidden">
                                <PageLink to="/catalogo" onClick={handleNavigate} data-testid="dropdown-catalogo"
                                    className="w-full text-center hover:bg-brand-orange font-medium bg-[#E58600]">
                                    Catálogo
                                </PageLink>
                                <PageLink to="/market" onClick={handleNavigate} data-testid="dropdown-market"
                                    className="w-full text-center bg-brand-orange font-medium hover:bg-[#E58600]">
                                    Mercado
                                </PageLink>
                                <PageLink to={`/inventario/${user.id}`} onClick={handleNavigate} data-testid="dropdown-inventory"
                                    className="w-full text-center bg-brand-orange font-medium hover:bg-[#E58600]">
                                    Inventario
                                </PageLink>
                                <hr className="my-1 border-black/20" />
                            </div>

                            {/* creditos no visibles en desplegable */}
                            <div className="px-2 pb-1  flex flex-col gap-0.5">
                                <p className="truncate text-sm" data-testid="user-email-dropdown" title={user.email}>
                                    {user.email}
                                </p>
                                <p className="truncate font-semibold" data-testid="user-name-dropdown" title={user.username}>
                                    {user.username}
                                </p>
                                <p className="text-sm sm:hidden" data-testid="user-credits-dropdown">
                                    Creditos: ${user.creditBalance}
                                </p>
                            </div>

                            {/* Logout */}
                            <Button role="menuitem" onClick={handleLogout} data-testid="logout-button"
                                className="border border-black/20 flex w-full items-center justify-between rounded-md px-4 py-1.5 transition-colors hover:bg-brand-orange  font-medium">
                                <span aria-hidden="true" className=" text-sm">
                                    ↪
                                </span>
                                Cerrar sesión
                            </Button>
                        </>
                    ) : (
                        <div className="flex flex-col gap-2">
                            <PageLink to="/catalogo" onClick={handleNavigate} data-testid="dropdown-catalogo"
                                className="w-full text-center bg-brand-orange font-medium hover:bg-[#E58600]">
                                Catálogo
                            </PageLink>
                            <PageLink to="/market" onClick={handleNavigate} data-testid="dropdown-market"
                                className="w-full text-center bg-brand-orange font-medium hover:bg-[#E58600]">
                                Mercado
                            </PageLink>
                            <PageLink to="/login" onClick={handleNavigate} data-testid="dropdown-login"
                                className="w-full text-center bg-brand-orange font-medium hover:bg-[#E58600]">
                                Iniciar sesión
                            </PageLink>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};