import React from 'react';
import { Link } from 'react-router-dom';

/**
 * Top navigation bar featuring the Overcode brand and login navigation link.
 * Styled with solid brand orange #FF9500.
 */
export const Navbar: React.FC = () => {
  return (
    <header className="h-16 bg-[#FF9500] flex-shrink-0 w-full shadow-md z-10">
      <div className="h-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between">
        <Link
          to="/"
          className="text-white text-2xl font-bold tracking-tight hover:opacity-90 transition-opacity"
        >
          Overcode
        </Link>
        <nav>
          <Link
            to="/login"
            className="text-white font-medium hover:underline text-base px-3 py-1.5 rounded transition-colors"
          >
            Iniciar Sesión
          </Link>
        </nav>
      </div>
    </header>
  );
};
