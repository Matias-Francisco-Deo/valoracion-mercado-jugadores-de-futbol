import React, { lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import { AuthLayout } from '../layouts/AuthLayout';
import { GeneralLayout } from '@/layouts/GeneralLayout';
import { ProtectedLayout } from '@/layouts/ProtectedLayout';

const HomePage = lazy(() => import('@/pages/HomePage'))
const RegisterPage = lazy(() => import('@/pages/RegisterPage'))
const LoginPage = lazy(() => import('@/pages/LoginPage'))
const PlayerPage = lazy(() => import('@/pages/PlayerPage'))
const NotFoundPage = lazy(() => import('@/pages/NotFoundPage'))
const CatalogoPage = lazy(() => import('@/pages/CatalogoPage'))
const InventoryPage = lazy(() => import('@/pages/InventoryPage'))

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route Component={AuthLayout}>{/*layout de registro y login */}
        <Route path="/register" Component={RegisterPage} />
        <Route path="/login" Component={LoginPage} />
        
      </Route>
      <Route Component={GeneralLayout}>{/*layout general*/}
          <Route path="/" Component={HomePage} />
          <Route path="/catalogo" Component={CatalogoPage} />
          <Route path="/p/:playerId" Component={PlayerPage} />
          <Route path="*" Component={NotFoundPage} errorElement/>
      </Route>
      <Route Component={ProtectedLayout}>
        <Route path="/inventario" Component={InventoryPage} />
      </Route>

    </Routes>
  );
};
