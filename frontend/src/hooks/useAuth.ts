import { useContext } from 'react';
import { AuthContext } from '../context/authContextBase';
import type { AuthContextType } from '../types/auth';

/**
 * Custom hook for safely consuming AuthContext.
 * Guarantees components do not access localStorage directly.
 */
export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
