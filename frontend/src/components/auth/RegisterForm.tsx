import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import * as yup from 'yup';
import { useAuth } from '../../hooks/useAuth';
import { Field } from '../ui/Field';
import { Button } from '../ui/Button';
import type { RegisterCredentials, RegisterFormErrors } from '../../types/auth';
import { registerSchema } from '../../schemas/registerSchema';


export const RegisterForm = (props: React.ComponentProps<'form'>) => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [serverError, setServerError] = useState('')
  const [formData, setFormData] = useState<RegisterCredentials>({
    email: '',
    username: '',
    password: '',
  });

  const [errors, setErrors] = useState<RegisterFormErrors>({});
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);

  const handleChange = (field: keyof RegisterCredentials) => (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setServerError("");
    setFormData((prev) => ({ ...prev, [field]: e.target.value }));
    // Clear field-specific error as user types
    if (errors[field]) {
      setErrors((prev) => ({ ...prev, [field]: undefined }));
    }
  };

  const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (isSubmitting) return;

    try {
      await registerSchema.validate(formData, { abortEarly: false });
    } catch (error: unknown) {
      if (error instanceof yup.ValidationError) {
        const validationErrors = error.inner.reduce<RegisterFormErrors>((fieldErrors, validationError) => {
          if (validationError.path && !fieldErrors[validationError.path as keyof RegisterFormErrors]) {
            fieldErrors[validationError.path as keyof RegisterFormErrors] = validationError.message;
          }
          return fieldErrors;
        }, {});
        setErrors(validationErrors);
      }
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
      setServerError(err instanceof Error ? err.message : 'No se pudo registrar el usuario.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} noValidate
    className="flex flex-col bg-[#A8A8A8] border border-gray-400/50 shadow-2xl gap-4 p-6 sm:p-8 rounded-xl max-w-md w-full mx-auto backdrop-blur-xs" {...props}>
      
      <h2 className="text-2xl sm:text-3xl font-bold text-center mb-6 tracking-tight">
        Registrarse
      </h2>

      {serverError && (
        <div role='alert'className='flex justify-center w-full text-sm text-destructive border-destructive rounded-lg'>
          {serverError}
        </div>
      )}

      <Field
        label="Correo Electrónico"
        id="register-email"
        type="email"
        name="email"
        autoComplete="email"
        placeholder="ejemplo@correo.com"
        value={formData.email}
        onChange={handleChange('email')}
        error={errors.email}
        required
      />

      <Field
        label="Nombre de Usuario"
        id="register-username"
        type="text"
        name="username"
        autoComplete="username"
        placeholder="usuario_123"
        value={formData.username}
        onChange={handleChange('username')}
        error={errors.username}
        required
      />

      <Field
        label="Contraseña"
        id="register-password"
        type="password"
        name="password"
        autoComplete="new-password"
        placeholder="Mínimo 6 caracteres"
        value={formData.password}
        onChange={handleChange('password')}
        error={errors.password}
        maxLength={30}
        required
      />

      <div className="mt-6">
        <Button  type="submit" disabled={isSubmitting} className="btn-primary">
          {isSubmitting ? 'Registrando...' : 'Registrarse'}
        </Button>
      </div>

      <div className="mt-5 text-center text-sm text-gray-900 font-medium">
        ¿Ya tienes cuenta? Inicia sesión{' '}
        <Link
          to="/login"
          className="font-bold underline hover:text-[#e08500] transition-colors">
          aqui
        </Link>
      </div>
    </form>
  );
};
