import React from 'react';
import { twMerge } from 'tailwind-merge';

export interface AlertBannerProps extends React.HTMLAttributes<HTMLDivElement> {
  /** Severity style */
  type?: 'error' | 'success' | 'info';
  /** Sanitized message text to display */
  message: string;
  /** Optional dismiss callback */
  onClose?: () => void;
  className?: string;
}

export const AlertBanner: React.FC<AlertBannerProps> = ({
  type = 'error',
  message,
  onClose,
  className,
  ...props
}) => {
  const baseClasses = 'p-3 rounded-md text-sm mb-4 border flex items-start justify-between gap-2';

  const typeClasses: Record<'error' | 'success' | 'info', string> = {
    error: 'bg-red-50 border-red-200 text-red-700',
    success: 'bg-green-50 border-green-200 text-green-700',
    info: 'bg-blue-50 border-blue-200 text-blue-700',
  };

  return (
    <div
      role="alert"
      className={twMerge(baseClasses, typeClasses[type], className)}
      {...props}
    >
      <span className="flex-1 font-medium">{message}</span>
      {onClose && (
        <button
          type="button"
          onClick={onClose}
          aria-label="Cerrar notificación"
          className="text-current opacity-70 hover:opacity-100 transition-opacity font-bold ml-2 leading-none cursor-pointer"
        >
          ×
        </button>
      )}
    </div>
  );
};
