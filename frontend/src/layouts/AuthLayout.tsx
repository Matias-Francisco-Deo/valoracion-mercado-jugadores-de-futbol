import React from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from '../components/common/Navbar';
import { Footer } from '@/components/common/Footer';

/**
 * Shared layout component framing the application with the top Navbar and dynamic route content.
 * Structured to ensure zero unwanted vertical scrollbars.
 */
export const AuthLayout: React.FC = () => {
  return (
    <div className="min-h-screen flex flex-col overflow-x-hidden w-full bg-gray-50">
      <Navbar />
      <main className="flex-1 flex flex-col min-h-0 w-full">
        <Outlet />
      </main>
      <Footer/>
    </div>
  );
};
