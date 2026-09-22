import * as yup from 'yup';

export const loginSchema = yup.object({
  email: yup
    .string()
    .trim()
    .required('El correo electrónico es requerido.')
    .email('Ingresa un formato de correo electrónico válido.'),
  password: yup
    .string()
    .required('La contraseña es requerida.')
    .min(6, 'La contraseña debe tener al menos 6 caracteres.')
    .max(30, 'La contraseña no puede superar los 30 caracteres.'),
});

export type LoginFormValues = yup.InferType<typeof loginSchema>;
