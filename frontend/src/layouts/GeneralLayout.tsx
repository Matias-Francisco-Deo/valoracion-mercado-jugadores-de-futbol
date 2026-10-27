import React from 'react';
import { Outlet } from 'react-router-dom';
import { Navbar } from '../components/common/Navbar';
import {Footer} from "@/components/common/Footer.tsx";
import cancha from '@/assets/cancha.avif';

export const GeneralLayout: React.FC = () => {
    return (

        <div className="h-screen flex flex-col overflow-x-hidden w-full bg-gray-50">
            <Navbar />
            <main className="flex-1 flex flex-col sm:p-6 bg-pitch-green bg-center overflow-y-scroll"
                style={{ backgroundImage: `url(${cancha})`, backgroundSize: '60% 100%'}}>
                <Outlet/>
            </main>
            <Footer/>
        </div>
    );
};
