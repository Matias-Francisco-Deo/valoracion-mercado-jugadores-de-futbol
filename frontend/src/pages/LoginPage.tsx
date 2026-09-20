import React from 'react';
import { Link } from 'react-router-dom';

/**
 * Placeholder Login page component ('/login') for upcoming feature implementation.
 */
export default function LoginPage() {
  return (
    <div className="flex-1 flex flex-col items-center justify-center p-6 text-center bg-gray-50">
      <div className="max-w-md w-full bg-white p-8 rounded-lg shadow-md space-y-4">
        <h1 className="text-2xl font-bold text-gray-900">Iniciar Sesión</h1>
        <p className="text-gray-600">
          La funcionalidad de inicio de sesión estará disponible próximamente en una fase posterior.
        </p>
        <div className="pt-2">
          <Link
            to="/register"
            className="inline-block px-5 py-2 bg-[#FF9500] hover:bg-[#e08500] text-white font-medium rounded-md shadow-sm transition-colors"
          >
            Volver a Registrarse
          </Link>
        </div>
      </div>
    </div>
  );
};
