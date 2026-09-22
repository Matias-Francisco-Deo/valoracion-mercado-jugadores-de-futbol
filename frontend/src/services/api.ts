/**
 * Base API client with strictly sanitized error handling adhering to Constitution Principle I.
 */

const BASE_URL = import.meta.env.VITE_API_BASE_URL;

export interface BackendErrorBody {
  statusCode?: number;
  error?: string;
  message?: string | string[];
}

export class ApiError extends Error {
  statusCode: number;

  constructor(statusCode: number, message: string) {
    super(message);
    this.name = 'ApiError';
    this.statusCode = statusCode;
  }
}


export async function apiClient<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const url = `${BASE_URL}${endpoint}`;

  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  try {
    const response = await fetch(url, {
      ...options,
      headers,
    });

    if (!response.ok) {
      const responseText = await response.text();
      let errorBody: BackendErrorBody = {};

      try {
        errorBody = JSON.parse(responseText) as BackendErrorBody;
      } catch {
        errorBody = {};
      }

      const backendMessage = Array.isArray(errorBody.message)
        ? errorBody.message.join(', ')
        : errorBody.message;
      const errorMessage = response.status >= 500
        ? 'No se pudo conectar con el servidor.'
        : backendMessage || 'La solicitud fue rechazada por el servidor.';

      throw new ApiError(
        response.status,
        errorMessage,
      );
    }

    // Handle 204 No Content
    if (response.status === 204) {
      return {} as T;
    }

    return (await response.json()) as T;
  } catch (error: unknown) {
    if (error instanceof ApiError) {
      throw error;
    }

    // Network interruption, DNS failure, or connection refused
    console.error(`[Network/Client Error] ${options.method || 'GET'} ${url}:`, error);

    throw new ApiError(
      0,
      'No se pudo conectar con el servidor.',
    );
  }
}
