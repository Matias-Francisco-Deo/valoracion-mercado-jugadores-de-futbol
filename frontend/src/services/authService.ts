import { apiClient } from './api';
import type { LoginCredentials, RegisterCredentials, AuthResponse } from '../types/auth';

/**
 * Authentication service handling API communication with authentication endpoints.
 */
export const authService = {
  /**
   * Registers a new user with email, username, and password.
   * On success, returns an AuthResponse with token and user profile.
   * On failure, throws an ApiError with a sanitized message.
   */
  async register(credentials: RegisterCredentials): Promise<AuthResponse> {
    return apiClient<AuthResponse>('/auth/register', {
      method: 'POST',
      body: JSON.stringify({
        email: credentials.email.trim(),
        username: credentials.username.trim(),
        password: credentials.password,
      }),
    });
  },

  async login(credentials: LoginCredentials): Promise<AuthResponse> {
    return apiClient<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({
        email: credentials.email.trim(),
        password: credentials.password,
      }),
    });
  },
};
