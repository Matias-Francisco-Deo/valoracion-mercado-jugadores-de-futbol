import * as yup from 'yup';

export const registerSchema = yup.object({
  email: yup
    .string()
    .trim()
    .required('El correo electrónico es requerido.')
    .email('Ingresa un formato de correo electrónico válido.'),
  username: yup
    .string()
    .trim()
    .required('El nombre de usuario es requerido.')
    .min(3, 'El nombre de usuario debe tener al menos 3 caracteres.')
    .max(30, 'El nombre de usuario no puede superar los 30 caracteres.')
    .matches(
      /^[a-zA-Z0-9_]+$/,
      'El nombre de usuario solo puede contener letras, números y guiones bajos.',
    ),
  password: yup
    .string()
    .required('La contraseña es requerida.')
    .min(6, 'La contraseña debe tener al menos 6 caracteres.')
    .max(30, 'La contraseña no puede superar los 30 caracteres.'),
});
