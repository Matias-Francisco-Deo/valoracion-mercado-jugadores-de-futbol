import React from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from '../components/common/Navbar';
import {Footer} from "@/components/common/Footer.tsx";
import {PageWindow} from "@/components/common/PageWindow.tsx";
import pasto from "@/assets/pasto.jpg";

export const ProtectedLayout: React.FC = () => {
  return (

      <div className="min-h-screen flex flex-col overflow-x-hidden w-full bg-gray-50">
          <Navbar />
          <main className="flex-1 flex flex-col p-6 bg-pitch-green bg-center"
                style={{ backgroundImage: `url(${pasto})`,
                    backgroundSize: '60% 100%'
          }}
          >
              <PageWindow>
                  <Outlet/>
              </PageWindow>
          </main>
          <Footer/>
      </div>
  );
};
