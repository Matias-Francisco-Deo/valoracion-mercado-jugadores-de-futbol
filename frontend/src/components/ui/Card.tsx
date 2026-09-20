import React from 'react';
import { twMerge } from 'tailwind-merge';

export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  /** Optional custom Tailwind classes to extend or override default card styling */
  className?: string;
  children: React.ReactNode;
}

export const Card: React.FC<CardProps> = ({ className, children, ...props }) => {
  const baseClasses = 'card-container rounded-lg shadow-xl p-6 md:p-8 w-full max-w-md';

  return (
    <div className={twMerge(baseClasses, className)} {...props}>
      {children}
    </div>
  );
};
