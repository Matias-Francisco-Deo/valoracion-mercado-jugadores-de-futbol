import type { UserProfile } from '../types/auth';

export interface AuthSession {
  token: string;
  user: UserProfile;
}

const STORAGE_KEY = 'auth-session';

export function saveSession(session: AuthSession): void {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
}

export function getSession(): AuthSession | null {
  try {
    const stored = localStorage.getItem(STORAGE_KEY);

    if (!stored) {
      return null;
    }

    return JSON.parse(stored) as AuthSession;
  } catch {
    return null;
  }
}

export function removeSession(): void {
  localStorage.removeItem(STORAGE_KEY);
}