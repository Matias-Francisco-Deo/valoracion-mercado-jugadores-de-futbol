import React from 'react';
import { twMerge } from 'tailwind-merge';

export interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  /** Visual variant of the button */
  variant?: 'primary' | 'secondary' | 'outline';
  /** Shows a loading state and disables the button */
  isLoading?: boolean;
  /** Optional custom Tailwind classes to extend or override default styling */
  className?: string;
  /** Button children content */
  children: React.ReactNode;
}

export const Button: React.FC<ButtonProps> = ({
  variant = 'primary',
  isLoading = false,
  className,
  disabled,
  children,
  ...props
}) => {
  const baseClasses =
    'w-full py-2.5 px-4 font-semibold rounded-md shadow-sm transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2';

  const variantClasses: Record<'primary' | 'secondary' | 'outline', string> = {
    primary: 'bg-[#FF9500] hover:bg-[#e08500] text-white focus:ring-[#FF9500]',
    secondary: 'bg-gray-600 hover:bg-gray-700 text-white focus:ring-gray-500',
    outline: 'border border-[#FF9500] text-[#FF9500] hover:bg-[#FF9500]/10 focus:ring-[#FF9500]',
  };

  const isButtonDisabled = disabled || isLoading;

  return (
    <button
      className={twMerge(baseClasses, variantClasses[variant], className)}
      disabled={isButtonDisabled}
      {...props}
    >
      {isLoading && (
        <svg
          className="animate-spin h-5 w-5 text-current"
          xmlns="http://www.w3.org/2000/svg"
          fill="none"
          viewBox="0 0 24 24"
          aria-hidden="true"
        >
          <circle
            className="opacity-25"
            cx="12"
            cy="12"
            r="10"
            stroke="currentColor"
            strokeWidth="4"
          />
          <path
            className="opacity-75"
            fill="currentColor"
            d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
          />
        </svg>
      )}
      {children}
    </button>
  );
};
