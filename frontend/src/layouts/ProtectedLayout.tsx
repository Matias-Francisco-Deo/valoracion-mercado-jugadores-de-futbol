import React from 'react';
import { Outlet, Navigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Navbar } from '@/components/common/Navbar';
import { Footer } from '@/components/common/Footer';
import cancha from '@/assets/cancha.avif';

export const ProtectedLayout: React.FC = () => {
  const { isAuthenticated } = useAuth();



  return (
    <div className="min-h-screen flex flex-col overflow-x-hidden w-full bg-gray-50">
      <Navbar />
      <main
        className="flex-1 flex flex-col p-3 sm:p-6 bg-pitch-green bg-cover bg-center overflow-y-auto"
        style={{ backgroundImage: `url(${cancha})` }}>
        <Outlet />
      </main>
      <Footer />
    </div>
  );
};
