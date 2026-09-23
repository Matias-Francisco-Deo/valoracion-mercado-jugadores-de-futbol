import React, { createContext, useCallback, useState } from 'react';
import type { LoginCredentials, RegisterCredentials, AuthContextType } from '../types/auth';
import { authService } from '../services/authService';
import { getSession, removeSession, saveSession } from '@/lib/session';

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

export interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [session, setSession] = useState(() => getSession());
  const [isLoading, setIsLoading] = useState(false);

  const register = useCallback(async (credentials: RegisterCredentials): Promise<void> => {
    setIsLoading(true);

    try {
      const response = await authService.register(credentials);
      
      const newSession = {
        token: response.token,
        expiresAt: response.expiresAt,
        user: response.user,
      };

      saveSession(newSession);
      setSession(newSession);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const login = useCallback(async (credentials: LoginCredentials): Promise<void> => {
    setIsLoading(true);

    try {
      const response = await authService.login(credentials);
      const newSession = {
        token: response.token,
        expiresAt: response.expiresAt,
        user: response.user,
      };

      saveSession(newSession);
      setSession(newSession);
    } finally {
      setIsLoading(false);
    }
  }, []);

  const logout = useCallback((): void => {
    removeSession();
    setSession(null);

  }, []);

  const value: AuthContextType = {
    user: session?.user ?? null,
    token: session?.token ?? null,
    isAuthenticated: session !== null,
    isLoading,
    register,
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
