import { useState, type ComponentProps } from "react"
import { Button } from "../ui/Button"
import type { UserProfile } from "@/types/auth";
import { useNavigate } from "react-router-dom";

type UserMenuProps = ComponentProps<'div'> & {
    user: UserProfile;
    onLogout: () => void;
}

export const UserMenu = ({user, onLogout, ...props}: UserMenuProps) => {
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const navigate = useNavigate();

    const handleLogout = () => {
        onLogout();
        setIsMenuOpen(false);
        navigate('/');
    }

    return (
        <div className={`relative ${props.className}`} {...props}>
            <Button 
            onClick={() => setIsMenuOpen((open) => !open)}
            aria-expanded={isMenuOpen}
            className="flex max-w-[calc(100vw-8rem)] items-center gap-2 rounded px-3 py-1.5 text-base font-medium text-white transition-colors hover:bg-[#e08500]">
                <span aria-hidden="true">●</span>
                <span className="truncate capitalize">{user.username}</span>
            </Button>
            {isMenuOpen && (
                <div
                role="menu"
                className="absolute border right-0 top-full z-20 mt-2 w-64 max-w-[calc(100vw-2rem)] rounded-md bg-[#E58600] p-3 shadow-xl">
                    <div className=" border-gray-200 px-2 pb-3">
                        <p className="truncate font-semibold capitalize">{user.username}</p>
                        <p className="wrap-break-word text-sm">{user.email}</p>
                    </div>
                    <Button
                        role="menuitem"
                        onClick={handleLogout}
                        className="border flex w-full items-center justify-between rounded-md px-4 py-1.5 transition-colors hover:bg-brand-orange">
                        <span aria-hidden="true" className="text-black text-sm">
                            ↪
                        </span>
                        Cerrar sesión
                    </Button>
                </div>
            )}
        </div>
    )
}