import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../hooks/useAuth';
import { Input } from '../ui/Input';
import { Button } from '../ui/Button';
import { ApiError } from '../../services/api';
import type { RegisterCredentials, RegisterFormErrors } from '../../types/auth';

export interface RegisterFormProps {
  onError?: (message: string | null) => void;
}

export const RegisterForm: React.FC<RegisterFormProps> = ({ onError }) => {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [formData, setFormData] = useState<RegisterCredentials>({
    email: '',
    username: '',
    password: '',
  });

  const [errors, setErrors] = useState<RegisterFormErrors>({});
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  const validate = (data: RegisterCredentials): RegisterFormErrors => {
    const validationErrors: RegisterFormErrors = {};
    const trimmedEmail = data.email.trim();
    const trimmedUsername = data.username.trim();
    const password = data.password;

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    const usernameRegex = /^[a-zA-Z0-9_]+$/;

    // Email validation
    if (!trimmedEmail) {
      validationErrors.email = 'El correo electrónico es requerido.';
    } else if (!emailRegex.test(trimmedEmail)) {
      validationErrors.email = 'Ingresa un formato de correo electrónico válido.';
    }

    // Username validation
    if (!trimmedUsername) {
      validationErrors.username = 'El nombre de usuario es requerido.';
    } else if (trimmedUsername.length < 3) {
      validationErrors.username = 'El nombre de usuario debe tener al menos 3 caracteres.';
    } else if (trimmedUsername.length > 30) {
      validationErrors.username = 'El nombre de usuario no puede superar los 30 caracteres.';
    } else if (!usernameRegex.test(trimmedUsername)) {
      validationErrors.username = 'El nombre de usuario solo puede contener letras, números y guiones bajos.';
    }

    // Password validation
    if (!password) {
      validationErrors.password = 'La contraseña es requerida.';
    } else if (password.length < 6) {
      validationErrors.password = 'La contraseña debe tener al menos 6 caracteres.';
    } else if (password.length > 100) {
      validationErrors.password = 'La contraseña no puede superar los 100 caracteres.';
    }

    return validationErrors;
  };

  const handleChange = (field: keyof RegisterCredentials) => (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setFormData((prev) => ({ ...prev, [field]: e.target.value }));
    // Clear field-specific error as user types
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: undefined }));
    }
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (isSubmitting) return;

    onError?.(null);

    const validationErrors = validate(formData);
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setErrors({});
    setIsSubmitting(true);

    try {
      await register({
        email: formData.email.trim(),
        username: formData.username.trim(),
        password: formData.password,
      });
      // Redirect to Home index upon successful registration
      navigate('/');
    } catch (err: unknown) {
      if (err instanceof ApiError) {
        onError?.(err.sanitizedMessage);
      } else if (err instanceof Error) {
        onError?.(err.message);
      } else {
        onError?.('Ocurrió un error inesperado al procesar el registro.');
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} noValidate className="w-full">
      <Input
        label="Correo Electrónico"
        id="register-email"
        type="email"
        name="email"
        autoComplete="email"
        placeholder="ejemplo@correo.com"
        value={formData.email}
        onChange={handleChange('email')}
        error={errors.email}
      />

      <Input
        label="Nombre de Usuario"
        id="register-username"
        type="text"
        name="username"
        autoComplete="username"
        placeholder="usuario_123"
        value={formData.username}
        onChange={handleChange('username')}
        error={errors.username}
      />

      <Input
        label="Contraseña"
        id="register-password"
        type="password"
        name="password"
        autoComplete="new-password"
        placeholder="Mínimo 6 caracteres"
        value={formData.password}
        onChange={handleChange('password')}
        error={errors.password}
      />

      <div className="mt-6">
        <Button
          type="submit"
          variant="primary"
          isLoading={isSubmitting}
          className="btn-primary"
        >
          Registrarse
        </Button>
      </div>

      <div className="mt-5 text-center text-sm text-gray-900 font-medium">
        ¿Ya tienes cuenta? Inicia sesión{' '}
        <Link
          to="/login"
          className="font-bold underline hover:text-[#e08500] transition-colors"
        >
          aqui
        </Link>
      </div>
    </form>
  );
};
