import React from 'react';

/**
 * Top navigation bar featuring the Overcode brand and login navigation link.
 * Styled with solid brand orange #FF9500.
 */
export const Footer: React.FC = () => {
  return (
    <header className="h-24 bg-brand-orange shrink-0 w-full shadow-md z-10">
      <div className="h-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex items-center justify-between">
        <p>
          Overcode Fútbol © - No todos los derechos - 2026
        </p>
    </div>
    </header>
  );
};
