import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import * as yup from 'yup';
import { useAuth } from '../../hooks/useAuth';
import { loginSchema, type LoginFormValues } from '../../schemas/loginSchema';
import type { LoginFormErrors } from '../../types/auth';
import { Button } from '../ui/Button';
import { Field } from '../ui/Field';

export const LoginForm = (props: React.ComponentProps<'form'>) => {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [formData, setFormData] = useState<LoginFormValues>({ email: '', password: '' });
  const [errors, setErrors] = useState<LoginFormErrors>({});
  const [serverError, setServerError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (field: keyof LoginFormValues) => (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    setFormData((previous) => ({ ...previous, [field]: event.target.value }));
    setServerError('');
    setErrors((previous) => ({ ...previous, [field]: undefined }));
  };

  const validate = async (): Promise<LoginFormErrors> => {
    try {
      await loginSchema.validate(formData, { abortEarly: false });
      return {};
    } catch (error: unknown) {
      if (!(error instanceof yup.ValidationError)) {
        throw error;
      }

      return error.inner.reduce<LoginFormErrors>((validationErrors, validationError) => {
        if (validationError.path && !validationErrors[validationError.path as keyof LoginFormErrors]) {
          validationErrors[validationError.path as keyof LoginFormErrors] = validationError.message;
        }
        return validationErrors;
      }, {});
    }
  };

  const handleSubmit = async (event: React.SubmitEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (isSubmitting) return;

    const validationErrors = await validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setErrors({});
    setIsSubmitting(true);

    return login(formData).then(() => {
      navigate('/');
    }).catch((err: unknown) => {
      setServerError(err instanceof Error ? err.message : 'No se pudo iniciar sesión.');
    }).finally(() => {
      setIsSubmitting(false);
    });

  };

  return (
    <form
      onSubmit={handleSubmit}
      noValidate
      className="flex w-full max-w-md flex-col gap-4 rounded-xl border border-gray-400/50 bg-card-gray p-6 shadow-2xl backdrop-blur-xs sm:p-8"
      {...props}
    >
      <h1 className="mb-4 text-center text-2xl font-bold tracking-tight sm:text-3xl">
        Iniciar sesión
      </h1>

      {serverError && (
        <div role="alert" className="flex rounded-lg justify-center w-full  px-3 py-2 text-sm text-destructive">
          {serverError}
        </div>
      )}

      <Field
        label="Correo Electrónico"
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
        label="Contraseña"
        type="password"
        name="password"
        autoComplete="current-password"
        value={formData.password}
        onChange={handleChange('password')}
        error={errors.password}
        maxLength={30}
        required
      />

      <div className="mt-2">
        <Button type="submit" disabled={isSubmitting} className="btn-primary">
          {isSubmitting ? 'Iniciando sesión...' : 'Iniciar sesión'}
        </Button>
      </div>

      <div className="mt-3 text-center text-sm font-medium">
        ¿No tienes cuenta? Regístrate{' '}
        <Link to="/register" className="font-bold text-[#9EE2EE] transition-colors hover:text-[#e08500]">
          aquí.
        </Link>
      </div>
    </form>
  );
};
