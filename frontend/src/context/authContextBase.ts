import { createContext } from 'react';
import type { AuthContextType } from '../types/auth';

export const STORAGE_KEY_TOKEN = 'auth_token';
export const STORAGE_KEY_USER = 'auth_user';

export const AuthContext = createContext<AuthContextType | undefined>(undefined);
