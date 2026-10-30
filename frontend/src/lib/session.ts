import type { UserProfile } from '../types/auth';

export interface AuthSession {
  token: string;
  expiresAt: string;
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

    const session = JSON.parse(stored) as AuthSession;

    // validar los datos mínimos de la sesión
    if (
      !session.token ||
      !session.expiresAt ||
      !session.user?.id
    ) {
      removeSession();
      return null;
    }

    //comprobar si el token ya venció
    const expirationTime = new Date(session.expiresAt).getTime();

    if (
      !Number.isFinite(expirationTime) ||
      expirationTime <= Date.now()
    ) {
      removeSession();
      return null;
    }

    return session;
  } catch {
    removeSession();
    return null;
  }
}

export function removeSession(): void {
  localStorage.removeItem(STORAGE_KEY);
}