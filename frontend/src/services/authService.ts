import { futbolApi } from './api';
import type { LoginCredentials, RegisterCredentials, AuthResponse } from '../types/auth';

  export async function registerUser(credentials: RegisterCredentials): Promise<AuthResponse> {
    return futbolApi.post<AuthResponse>('/auth/register', {
      email: credentials.email.trim(),
      username: credentials.username.trim(),
      password: credentials.password,
    });
    
  }

  export async function loginUser(credentials: LoginCredentials): Promise<AuthResponse> {
    return futbolApi.post<AuthResponse>('/auth/login', {
      email: credentials.email.trim(),
      password: credentials.password,
    });
  }