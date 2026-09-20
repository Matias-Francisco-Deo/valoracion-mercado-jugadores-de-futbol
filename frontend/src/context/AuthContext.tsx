import React, { useState, useCallback } from 'react';
import type { UserProfile, RegisterCredentials, AuthContextType } from '../types/auth';
import { authService } from '../services/authService';
import {
  AuthContext,
  STORAGE_KEY_TOKEN,
  STORAGE_KEY_USER,
} from './authContextBase';

export interface AuthProviderProps {
  children: React.ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [token, setToken] = useState<string | null>(() => {
    try {
      const storedToken = localStorage.getItem(STORAGE_KEY_TOKEN);
      const storedUser = localStorage.getItem(STORAGE_KEY_USER);
      if (storedToken && storedUser) {
        return storedToken;
      }
    } catch (err: unknown) {
      console.error('[AuthContext] Error reading initial token from localStorage:', err);
    }
    return null;
  });

  const [user, setUser] = useState<UserProfile | null>(() => {
    try {
      const storedToken = localStorage.getItem(STORAGE_KEY_TOKEN);
      const storedUser = localStorage.getItem(STORAGE_KEY_USER);
      if (storedToken && storedUser) {
        return JSON.parse(storedUser) as UserProfile;
      }
    } catch (err: unknown) {
      console.error('[AuthContext] Error reading initial user from localStorage:', err);
    }
    return null;
  });

  const [isLoading, setIsLoading] = useState<boolean>(false);

  const register = useCallback(async (credentials: RegisterCredentials): Promise<void> => {
    setIsLoading(true);
    try {
      const response = await authService.register(credentials);
      setToken(response.token);
      setUser(response.user);

      localStorage.setItem(STORAGE_KEY_TOKEN, response.token);
      localStorage.setItem(STORAGE_KEY_USER, JSON.stringify(response.user));
    } finally {
      setIsLoading(false);
    }
  }, []);

  const logout = useCallback((): void => {
    localStorage.removeItem(STORAGE_KEY_TOKEN);
    localStorage.removeItem(STORAGE_KEY_USER);
    setToken(null);
    setUser(null);
  }, []);

  const value: AuthContextType = {
    user,
    token,
    isAuthenticated: Boolean(token && user),
    isLoading,
    register,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
