import React, { useId } from 'react';
import { Input } from './Input';
import { cn } from '@/lib/utils';

interface FieldProps extends Omit<React.ComponentProps<'input'>, 'placeholder'> {
  label: string
  placeholder?: string;
  error?: string
  inputClassName?: string;
}

export const Field = ({ label, placeholder, error, inputClassName, className, ...props }: FieldProps) => {
  const id = useId()
  const errorId = `${id}-error`

  const hasError = Boolean(error)

  return (
    <div className={cn('w-full mb-4', className)}>
      <label htmlFor={id} className="form-label block text-sm font-medium text-gray-800 mb-1">
        {label}
      </label >
      <Input
        id={id}
        placeholder={placeholder ?? ""}
        aria-invalid={Boolean(error)}
        aria-describedby={hasError ? errorId : undefined}
        className={cn('form-input-base bg-white ',inputClassName)} 
        {...props}
      />
      {error && (
        <p id={errorId} className="form-error-text text-xs text-red-600 mt-1" role="alert">
          {error}
        </p>
      )}
      {!error && (
        <p  className="text-xs text-gray-500 mt-1">
          {error}
        </p>
      )}
    </div>
  );
};