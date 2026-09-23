import React, { lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthLayout } from '../layouts/AuthLayout';
import {ProtectedLayout} from "@/layouts/ProtectedLayout.tsx";

const HomePage = lazy(() => import('@/pages/HomePage'))
const RegisterPage = lazy(() => import('@/pages/RegisterPage'))
const LoginPage = lazy(() => import('@/pages/LoginPage'))
const PlayerPage = lazy(() => import('@/pages/PlayerPage'))
const NotFoundPage = lazy(() => import('@/pages/NotFoundPage'))

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route Component={AuthLayout}>{/*layout de registro y login */}
        <Route path="/register" Component={RegisterPage} />
        <Route path="/login" Component={LoginPage} />
        
      </Route>
      <Route Component={ProtectedLayout}>{/*layout general*/}
          <Route path="/" Component={HomePage} />
          <Route path="/p/:playerId" Component={PlayerPage} />
          <Route path="*" Component={NotFoundPage} errorElement/>
      </Route>

    </Routes>
  );
};
