import React, { lazy } from 'react';
import { Routes, Route } from 'react-router-dom';
import { MainLayout } from '../layouts/MainLayout';
import {ProtectedLayout} from "@/layouts/ProtectedLayout.tsx";

const HomePage = lazy(() => import('@/pages/HomePage'))
const RegisterPage = lazy(() => import('@/pages/RegisterPage'))
const LoginPage = lazy(() => import('@/pages/LoginPage'))

export const AppRoutes: React.FC = () => {
  return (
    <Routes>
      <Route element={<MainLayout />}>
        <Route path="/register" Component={RegisterPage} />
        <Route path="/login" Component={LoginPage} />
      </Route>
        <Route element={<ProtectedLayout />}>
            <Route path="*" Component={HomePage} />
            <Route path="/" Component={HomePage} />
        </Route>

    </Routes>
  );
};
