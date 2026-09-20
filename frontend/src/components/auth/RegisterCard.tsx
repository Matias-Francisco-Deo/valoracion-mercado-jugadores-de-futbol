import React, { useState } from 'react';
import { Card } from '../ui/Card';
import { AlertBanner } from '../ui/AlertBanner';
import { RegisterForm } from './RegisterForm';

/**
 * Centered card container for user registration.
 * Styled with solid background #A8A8A8 and top-of-card alert feedback.
 */
export const RegisterCard: React.FC = () => {
  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  return (
    <Card className="bg-[#A8A8A8] border border-gray-400/50 shadow-2xl p-6 sm:p-8 rounded-xl max-w-md w-full mx-auto backdrop-blur-xs">
      <h2 className="text-2xl sm:text-3xl font-bold text-gray-900 text-center mb-6 tracking-tight">
        Registrarse
      </h2>

      {errorMessage && (
        <AlertBanner
          type="error"
          message={errorMessage}
          onClose={() => setErrorMessage(null)}
          className="mb-5 shadow-sm"
        />
      )}

      <RegisterForm onError={setErrorMessage} />
    </Card>
  );
};
