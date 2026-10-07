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
});

export type LoginFormValues = yup.InferType<typeof loginSchema>;
