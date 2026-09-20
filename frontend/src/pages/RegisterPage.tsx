import React from 'react';
import { RegisterCard } from '../components/auth/RegisterCard';
import canchaPelotaBg from '../assets/cancha-pelota.png';

/**
 * Registration page view hosted at /register.
 * Features a solid green background (#096638) overlaid with cancha-pelota.png,
 * and a centered registration card (#A8A8A8).
 * Enforces dynamic viewport constraints to ensure zero vertical layout scrollbars.
 */
export const RegisterPage: React.FC = () => {
  return (
    <div className="relative w-full h-[calc(100dvh-4rem)] flex items-center justify-center p-4 bg-[#096638] overflow-hidden">
      {/* Background soccer pitch image overlay covering the solid green background */}
      <img
        src={canchaPelotaBg}
        alt=""
        aria-hidden="true"
        className="absolute inset-0 w-full h-full object-cover pointer-events-none select-none"
      />

      {/* Centered Registration Card Container */}
      <div className="relative z-10 w-full max-w-md flex justify-center">
        <RegisterCard />
      </div>
    </div>
  );
};
