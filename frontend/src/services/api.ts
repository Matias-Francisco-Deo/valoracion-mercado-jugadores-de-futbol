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
  public statusCode: number;
  public sanitizedMessage: string;
  public rawMessage?: string;

  constructor(statusCode: number, sanitizedMessage: string, rawMessage?: string) {
    super(sanitizedMessage);
    this.name = 'ApiError';
    this.statusCode = statusCode;
    this.sanitizedMessage = sanitizedMessage;
    this.rawMessage = rawMessage;
  }
}

function getSanitizedErrorMessage(status: number, rawMessage?: string): string {
  if (status === 409) {
    return 'El correo electrónico o nombre de usuario ya se encuentra registrado.';
  }
  if (status === 400) {
    return 'Los datos ingresados son inválidos. Por favor revisa los campos e intenta nuevamente.';
  }
  if (status === 404) {
    return 'El recurso solicitado no fue encontrado.';
  }
  if (status >= 500) {
    return 'Ocurrió un error en el servidor. Por favor intenta nuevamente más tarde.';
  }
  return rawMessage && typeof rawMessage === 'string'
    ? rawMessage
    : 'Ocurrió un error inesperado. Por favor intenta nuevamente.';
}

export async function apiClient<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const url = `${BASE_URL}${endpoint}`;
  const headers: HeadersInit = {
    'Content-Type': 'application/json',
    ...(options.headers || {}),
  };

  try {
    const response = await fetch(url, {
      ...options,
      headers,
    });

    if (!response.ok) {
      let rawErrorText = '';
      try {
        const errorJson = (await response.json()) as BackendErrorBody;
        rawErrorText = Array.isArray(errorJson.message)
          ? errorJson.message.join(', ')
          : errorJson.message || errorJson.error || '';
      } catch {
        rawErrorText = await response.text().catch(() => '');
      }

      console.error(`[API Error] ${options.method || 'GET'} ${url} returned ${response.status}:`, rawErrorText);

      const sanitizedMessage = getSanitizedErrorMessage(response.status, rawErrorText);
      throw new ApiError(response.status, sanitizedMessage, rawErrorText);
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
      'No se pudo conectar con el servidor. Por favor verifica tu conexión a internet o intenta nuevamente más tarde.',
      error instanceof Error ? error.message : String(error)
    );
  }
}
