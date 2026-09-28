# Contract: Reusable UI Components, Layout & Context Contracts

**Feature Branch**: `001-user-registration`  
**Spec**: `specs/001-user-registration/spec.md`  
**Status**: Draft  

---

## 1. Reusable Generic UI Components

To maintain consistency and avoid code duplication across current and future pages (such as Login and Market Valuation), generic components must accept `className` and native HTML attributes while providing sensible default styling via `tailwind-merge` (`twMerge`). Only primitives with reasonable current or foreseeable utility are abstracted.

### 1.1 `Button` Component (`src/components/ui/Button.tsx`)

A customizable button component supporting visual variants and loading spinners.

```typescript
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
```

- **Default Classes**: `w-full py-2.5 px-4 font-semibold rounded-md shadow-sm transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed`
- **Variant Default (`primary`)**: `bg-[#FF9500] hover:bg-[#e08500] text-white focus:ring-[#FF9500]`
- **Merge Behavior**: Uses `twMerge(baseClasses, variantClasses, className)`.

---

### 1.2 `Input` Component (`src/components/ui/Input.tsx`)

An accessible, labeled input component displaying inline validation error feedback.

```typescript
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
```

- **Default Classes**: `w-full px-3 py-2 border rounded-md text-gray-900 bg-white placeholder-gray-400 focus:outline-none focus:ring-2 transition-colors`
- **Normal State**: `border-gray-300 focus:ring-[#FF9500] focus:border-[#FF9500]`
- **Error State**: `border-red-500 focus:ring-red-500 focus:border-red-500`
- **Accessibility**: Automatically links `label` and `input` using generated or provided `id` attribute; associates `error` message via `aria-describedby` and `aria-invalid`.
- **Merge Behavior**: Uses `twMerge(baseClasses, stateClasses, className)`.

---

### 1.3 `Card` Component (`src/components/ui/Card.tsx`)

A reusable container card providing standard rounding, elevation, and background structure.

```typescript
export interface CardProps extends React.HTMLAttributes<HTMLDivElement> {
  /** Optional custom Tailwind classes to extend or override default card styling */
  className?: string;
  children: React.ReactNode;
}
```

- **Default Classes**: `rounded-lg shadow-xl p-6 md:p-8 w-full max-w-md`
- **Merge Behavior**: Uses `twMerge(baseClasses, className)`.

---

### 1.4 `AlertBanner` Component (`src/components/ui/AlertBanner.tsx`)

Top-of-card notification banner for displaying sanitized operational, network, or validation alerts.

```typescript
export interface AlertBannerProps extends React.HTMLAttributes<HTMLDivElement> {
  /** Severity style */
  type?: 'error' | 'success' | 'info';
  /** Sanitized message text to display */
  message: string;
  /** Optional dismiss callback */
  onClose?: () => void;
  className?: string;
}
```

- **Default Classes**: `p-3 rounded-md text-sm mb-4 border flex items-start justify-between`
- **Error Variant**: `bg-red-50 border-red-200 text-red-700`
- **Merge Behavior**: Uses `twMerge(baseClasses, typeClasses, className)`.

---

## 2. Layout & Common Components

### 2.1 `Navbar` Component (`src/components/common/Navbar.tsx`)

Top navigation bar complying with design tokens:
- **Background**: Solid orange `#FF9500`.
- **Height**: Fixed height `h-16` (`64px`), flex layout.
- **Left Slot**: Platform brand title `"Overcode"`, styled with clean readable typography linking to `/`.
- **Right Slot**: Action link `"Iniciar Sesión"` routing to `/login`.

### 2.2 `MainLayout` Component (`src/layouts/MainLayout.tsx`)

The shared shell of the application wrapping page routes.

- **Zero-Scrollbar Requirement**:
  - Outermost wrapper: `min-h-screen flex flex-col overflow-x-hidden`.
  - Navbar: `h-16 flex-shrink-0`.
  - Main container: `<main className="flex-1 flex flex-col min-h-0 relative">`.
  - Nested `<Outlet />` renders page components. For `/register`, the content occupies `h-[calc(100dvh-4rem)]` with `overflow-hidden` at the boundary so that no vertical layout scrollbar is ever rendered.

---

## 3. Centralized Authentication Context (`AuthContext`)

### 3.1 Type Definitions (`src/types/auth.ts`)

```typescript
export interface UserProfile {
  id: string | number;
  email: string;
  username: string;
  createdAt?: string;
}

export interface RegisterCredentials {
  email: string;
  username: string;
  password: string;
}

export interface AuthContextType {
  user: UserProfile | null;
  token: string | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  register: (credentials: RegisterCredentials) => Promise<void>;
  logout: () => void;
}
```

### 3.2 Component Consumption Contract

```typescript
// Any component needing auth state or actions MUST consume useAuth():
import { useAuth } from '../hooks/useAuth';

function MyComponent() {
  const { user, isAuthenticated, register, isLoading } = useAuth();
  // Direct interaction with localStorage is strictly prohibited.
}
```
