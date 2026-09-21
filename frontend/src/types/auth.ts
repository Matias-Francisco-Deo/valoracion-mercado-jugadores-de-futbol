/**
 * Shared TypeScript interfaces and types for Authentication and User domains.
 */

export interface UserProfile {
  id: string | number;
  email: string;
  username: string;
  creditBalance: number;
  tokens:number[];
}

export interface RegisterCredentials {
  email: string;
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  expiresAt: string;
  user: UserProfile;
}

export interface AuthContextType {
  user: UserProfile | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  register: (credentials: RegisterCredentials) => Promise<void>;
  logout: () => void;
}

export interface RegisterFormErrors {
  email?: string;
  username?: string;
  password?: string;
}

