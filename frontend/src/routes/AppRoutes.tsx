import React, { lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthLayout } from '../layouts/AuthLayout';
import {ProtectedLayout} from "@/layouts/ProtectedLayout.tsx";

const HomePage = lazy(() => import('@/pages/HomePage'))
const RegisterPage = lazy(() => import('@/pages/RegisterPage'))
const LoginPage = lazy(() => import('@/pages/LoginPage'))
const PlayerPage = lazy(() => import('@/pages/PlayerPage'))

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route element={<AuthLayout />}>
        <Route path="/register" Component={RegisterPage} />
        <Route path="/login" Component={LoginPage} />
        
      </Route>
      <Route element={<ProtectedLayout />}>{/*esta es la zona segura :D*/}
          <Route path="*" Component={HomePage} />
          <Route path="/" Component={HomePage} />
          <Route path="/player" Component={PlayerPage} />{/*mover a zona segura */}
      </Route>

    </Routes>
  );
};
