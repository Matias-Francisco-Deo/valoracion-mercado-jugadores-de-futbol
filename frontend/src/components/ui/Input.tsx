import React, { useId } from 'react';
import { twMerge } from 'tailwind-merge';

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  /** Visible label text associated with the input control */
  label: string;
  /** Inline error message displayed beneath the input control */
  error?: string;
  /** Additional helper text displayed beneath the input */
  helperText?: string;
  /** Custom wrapper/container Tailwind classes */
  containerClassName?: string;
  /** Custom input element Tailwind classes */
  className?: string;
}

export const Input: React.FC<InputProps> = ({
  label,
  error,
  helperText,
  containerClassName,
  className,
  id,
  ...props
}) => {
  const generatedId = useId();
  const inputId = id || generatedId;
  const errorId = error ? `${inputId}-error` : undefined;
  const helperId = helperText ? `${inputId}-helper` : undefined;

  const describedBy = [errorId, helperId].filter(Boolean).join(' ') || undefined;

  const baseClasses =
    'form-input-base w-full px-3 py-2 border rounded-md text-gray-900 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 transition-colors';

  const stateClasses = error
    ? 'border-red-500 focus:ring-red-500 focus:border-red-500'
    : 'border-gray-300 focus:ring-[#FF9500] focus:border-[#FF9500]';

  return (
    <div className={twMerge('w-full mb-4', containerClassName)}>
      <label htmlFor={inputId} className="form-label block text-sm font-medium text-gray-800 mb-1">
        {label}
      </label>
      <input
        id={inputId}
        aria-invalid={Boolean(error)}
        aria-describedby={describedBy}
        className={twMerge(baseClasses, stateClasses, className)}
        {...props}
      />
      {error && (
        <p id={errorId} className="form-error-text text-xs text-red-600 mt-1" role="alert">
          {error}
        </p>
      )}
      {!error && helperText && (
        <p id={helperId} className="text-xs text-gray-500 mt-1">
          {helperText}
        </p>
      )}
    </div>
  );
};
