/**
 * Base API client with strictly sanitized error handling adhering to Constitution Principle I.
 */

import { HttpError } from "@/lib/http-error";
import { getSession, removeSession } from "@/lib/session";

const BASE_URL = import.meta.env.VITE_API_BASE_URL;

export interface ApiError {
    message: string
  
}

export class ApiError extends Error {
  statusCode: number;

  constructor(statusCode: number, message: string) {
    super(message);
    this.name = 'ApiError';
    this.statusCode = statusCode;
  }
}


export async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const url = `${BASE_URL}${endpoint}`;

  const session = getSession();

  const response = await fetch(url, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(session?.token && { 'Authorization': `Bearer ${session.token}` }),
    }
  }).catch(() => {
    throw new HttpError(0, 'No se pudo conectar con el servidor.');
  });

    if (!response.ok) {
      const data: ApiError = await response.json().catch(() => ({
        error:{
          message: 'Ocurrió un error inesperado.',
        },
      }))

      if (response.status === 401 && session) {
          removeSession()
        window.location.href = '/login?reason=session-expired'
      }

      throw new HttpError(response.status, data.message)

    }

    // Handle 204 No Content
    if (response.status === 204) {
      return {} as T;
    }

  return response.json()
}

export const futbolApi = {
  get<T>(path: string) {
    return request<T>(path)
  },

  post<T>(path: string, body: unknown) {
    return request<T>(path, {
      method: 'POST',
      body: JSON.stringify(body),
    })
  },

  put<T>(path: string, body: unknown) {
    return request<T>(path, {
      method: 'PUT',
      body: JSON.stringify(body),
    })
  },

  patch<T>(path: string, body: unknown) {
    return request<T>(path, {
      method: 'PATCH',
      body: JSON.stringify(body),
    })
  },

  delete<T>(path: string) {
    return request<T>(path, {
      method: 'DELETE',
    })
  },
}