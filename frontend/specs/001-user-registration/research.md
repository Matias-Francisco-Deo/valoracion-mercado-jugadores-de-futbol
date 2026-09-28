# Research & Technical Decisions: User Registration Page & Layout

**Feature Branch**: `001-user-registration`  
**Feature Spec**: `specs/001-user-registration/spec.md`  
**Status**: Completed  

---

## 1. Styling Strategy: Tailwind CSS v4, Design Tokens & Shared CSS

### Context & Requirements
- Requirement to use Tailwind CSS for styling all components and pages.
- Mandatory design tokens from specification:
  - Navbar background: `#FF9500` (Orange)
  - Registration page background: `#096638` (Soccer pitch green)
  - Registration card background: `#A8A8A8` (Form card gray)
- If a style is likely to be reused across multiple components or pages, it must be defined in `src/index.css` instead of duplicated inline.
- Reusable UI components must accept `className` and merge overrides reliably.

### Decision
1. **Tailwind CSS v4 Configuration**:
   - Enable `@tailwindcss/vite` plugin in `vite.config.ts`.
   - Import Tailwind directly in `src/index.css` via `@import "tailwindcss";`.
   - Register custom theme variables using `@theme` in `src/index.css`:
     - `--color-brand-orange: #FF9500;`
     - `--color-pitch-green: #096638;`
     - `--color-card-gray: #A8A8A8;`
2. **Centralized Reusable Classes in `src/index.css`**:
   - Define reusable base styles and common utilities in `src/index.css`:
     - `.form-label`: Common label typography, font-weight, color, and margin.
     - `.form-input-base`: Common input padding, border, focus-ring, transition, and placeholder styling.
     - `.form-error-text`: Common inline validation error typography and red color.
     - `.btn-primary`: Reusable submit button base styles (transitions, hover states, disabled states).
     - `.card-container`: Shared card elevation, rounded corners, padding, and centered layout.
3. **Class Merging with `tailwind-merge`**:
   - Use `twMerge` from `tailwind-merge` inside generic UI components (`Button`, `Input`, `Card`) to allow consumers to supply custom `className` properties that cleanly override or extend default styling without class collisions.

### Rationale
- Tailwind v4 eliminates complex config files (`tailwind.config.js`) in favor of direct CSS theme extensions in `index.css`.
- Centralizing repetitive form styles in `index.css` prevents class clutter in JSX and enforces visual consistency across future forms (such as Login).
- `tailwind-merge` ensures that utility class conflicts (e.g. padding `px-4` vs `px-6` or background overrides) resolve predictably in favor of the caller's `className`.

### Alternatives Considered
- *Inline-only Tailwind utilities*: Rejected because duplicating long class lists across inputs and buttons violates the user's explicit directive to place reusable styles in `index.css`.
- *CSS Modules / Styled Components*: Rejected because the project constitution and requirements dictate Tailwind CSS as the unified styling framework.

---

## 2. Layout Structure & Scrollbar Prevention

### Context & Requirements
- The registration page MUST NOT display a vertical scrollbar as a result of being rendered inside the overall application layout.
- The layout contains a top navigation bar (orange `#FF9500`) and the page content area.
- The registration page features a background image `cancha-pelota.png` layered over a solid green `#096638` background, covering the entire page area behind the centered card.

### Decision
1. **Root & Body Layout Normalization**:
   - In `src/index.css`, strip default Vite template constraints (`#root` max-width 1126px and centering borders).
   - Configure `html, body, #root`:
     - Height: `100%` / `min-h-screen`
     - Margin: `0`
     - Overflow-x: `hidden`
2. **Layout Geometry in `MainLayout.tsx`**:
   - `MainLayout` renders a flex container: `min-h-screen flex flex-col`.
   - `Navbar` occupies a fixed height: `h-16 flex-shrink-0`.
   - The primary `<main>` wrapper uses `flex-1 flex flex-col min-h-0`.
3. **Registration Viewport Fitting (`RegisterPage.tsx`)**:
   - `RegisterPage` container applies `h-[calc(100dvh-4rem)] flex items-center justify-center p-4 overflow-hidden relative`.
   - Using dynamic viewport units (`100dvh`) subtracted by the navbar height (`4rem` / `h-16`) guarantees exact screen fitting across desktop browsers and mobile browser chrome bars without vertical scrolling.
   - For small devices where the card height approaches the screen height, internal scroll or compact vertical padding will prevent the layout-level scrollbar from appearing.

### Rationale
- Vertical scrollbars on full-screen landing or authentication cards are typically caused by `100vh` ignoring mobile toolbars or accumulating padding/margins beyond 100%. `h-[calc(100dvh-4rem)]` combined with `overflow-hidden` at the page boundary guarantees zero layout-level vertical scrollbar.
- Keeps the navigation bar always in view while centering the card cleanly in the remaining space.

### Alternatives Considered
- *Absolute positioning (`absolute inset-0 top-16`)*: Viable, but standard flexbox (`flex-1 min-h-0`) is more idiomatic in React Router layout patterns and handles dynamic header changes better.
- *Default `min-h-screen` without calc*: Rejected because adding a 64px navbar onto a `min-h-screen` page forces total height to `100vh + 64px`, creating a permanent vertical scrollbar.

---

## 3. Centralized Authentication State Architecture (Auth Context)

### Context & Requirements
- Centralized React Auth Context must manage authentication state and authentication-related logic.
- The Auth Context must handle all interactions with `localStorage` (storing/retrieving JWT token and user profile).
- Individual UI components MUST NOT access `localStorage` directly for authentication purposes; they must consume state and actions via the Auth Context.
- Authentication logic must be isolated so future authentication changes do not require modifications across individual components.

### Decision
1. **Context & Provider Module (`src/context/AuthContext.tsx`)**:
   - Define `AuthContext` with type `AuthContextType`:
     - `user: UserProfile | null`
     - `token: string | null`
     - `isAuthenticated: boolean`
     - `isLoading: boolean`
     - `register: (data: RegisterCredentials) => Promise<void>`
     - `login: (data: LoginCredentials) => Promise<void>`
     - `logout: () => void`
   - Store storage keys in typed constants:
     - `STORAGE_TOKEN_KEY = 'auth_token'`
     - `STORAGE_USER_KEY = 'auth_user'`
2. **LocalStorage Encapsulation**:
   - Only `AuthContext.tsx` contains `localStorage.getItem`, `localStorage.setItem`, and `localStorage.removeItem`.
   - On initial mount, `AuthProvider` reads stored credentials to restore the session synchronously or inside `useEffect`.
3. **Custom Hook (`src/hooks/useAuth.ts`)**:
   - Export `useAuth()` custom hook in `src/hooks/useAuth.ts` which checks for context availability (`if (!context) throw new Error("useAuth must be used within an AuthProvider")`) and returns the context.
4. **Backend Communication Abstraction (`src/services/authService.ts`)**:
   - `authService.ts` contains raw HTTP calls (`POST /auth/register`).
   - `AuthProvider.register` invokes `authService.registerUser(credentials)`, retrieves the response (`{ token, user }`), saves to `localStorage`, and updates React state.

### Rationale
- Strict compliance with user instructions: components only consume `useAuth()`.
- Centralizing `localStorage` reads/writes prevents synchronization bugs, stale state across windows/tabs, and makes it trivial to swap storage mechanisms (e.g. to cookie-based or in-memory) in the future without touching UI components.

### Alternatives Considered
- *Redux Toolkit / Zustand*: Rejected because React's built-in `createContext` and `useContext` are lightweight, dependency-free, and perfectly suited for client-side authentication sessions in this project.
- *Direct API call in component + direct `localStorage.setItem`*: Explicitly forbidden by user requirements.

---

## 4. Project Directory Structure & Modular Separation

### Context & Requirements
- Maintain a consistent and organized project structure with designated responsibilities:
  - `pages/` — application pages
  - `components/` — reusable UI and application components
  - `hooks/` — custom React hooks
  - `routes/` — routing configuration
  - `layouts/` — shared page layouts
  - `types/` — shared TypeScript types
  - `services/` — service and API/backend communication logic
  - `context/` — centralized React contexts (Auth Context)

### Decision
Directory layout under `src/`:
```text
src/
├── assets/
│   └── cancha-pelota.png           # Existing pitch ball image asset
├── components/
│   ├── auth/
│   │   ├── RegisterCard.tsx        # Centered form card container
│   │   └── RegisterForm.tsx        # Registration form fields and submit action
│   ├── common/
│   │   └── Navbar.tsx              # Orange top navigation bar
│   └── ui/
│       ├── AlertBanner.tsx         # User-friendly error/alert notice component
│       ├── Button.tsx              # Reusable button with native props + twMerge
│       ├── Card.tsx                # Reusable card container with native props + twMerge
│       └── Input.tsx               # Reusable labeled input with native props + twMerge
├── context/
│   └── AuthContext.tsx             # React Context for authentication & localStorage
├── hooks/
│   └── useAuth.ts                  # Hook for accessing AuthContext
├── layouts/
│   └── MainLayout.tsx              # Shared layout containing Navbar and <Outlet />
├── pages/
│   ├── HomePage.tsx                # Index route ('/') placeholder
│   ├── LoginPage.tsx               # Placeholder login route ('/login')
│   └── RegisterPage.tsx            # Full-page registration view ('/register')
├── routes/
│   └── AppRoutes.tsx               # Centralized React Router configuration
├── services/
│   ├── api.ts                      # Base fetch client / error handling
│   └── authService.ts              # API calls for registration and auth
├── types/
│   └── auth.ts                     # TypeScript definitions for auth, user, and credentials
├── index.css                       # Global styles, Tailwind imports, reusable CSS classes
├── main.tsx                        # Application entry point with BrowserRouter & AuthProvider
└── vite-env.d.ts                   # Vite environment definitions
```

### Rationale
- Each folder has an unmistakable, single purpose aligned with standard React/TypeScript best practices and the user's explicit structural constraints.
- No business logic or storage calls reside in component leaves.

---

## 5. Reusable UI Components Strategy

### Context & Requirements
- Create generic and reusable UI components (`Button`, `Input`, `Card`) only where there is a reasonable current or foreseeable use.
- Components must accept a `className` prop and all appropriate native HTML props.
- Allow instances to extend or override default styling.
- Provide sensible default styling so common use cases do not repeat classes.

### Decision
1. **`Button` Component (`src/components/ui/Button.tsx`)**:
   - Extends `React.ButtonHTMLAttributes<HTMLButtonElement>`.
   - Accepts variant (`primary`, `secondary`, `outline`), `isLoading?: boolean`, and `className`.
   - Merges default styles with `twMerge`.
2. **`Input` Component (`src/components/ui/Input.tsx`)**:
   - Extends `React.InputHTMLAttributes<HTMLInputElement>`.
   - Accepts `label?: string`, `error?: string`, `helperText?: string`, and `className`.
   - Generates accessible `id` and `htmlFor` pairings.
   - Merges default styles with `twMerge`.
3. **`Card` Component (`src/components/ui/Card.tsx`)**:
   - Extends `React.HTMLAttributes<HTMLDivElement>`.
   - Sensible default padding, rounded corners, and shadow; merges `className`.
4. **`AlertBanner` Component (`src/components/ui/AlertBanner.tsx`)**:
   - Reusable notification banner for top-of-card error and conflict alerts, sanitizing raw technical errors.

### Rationale
- Prevents UI code duplication and guarantees accessibility (accessible labels, ARIA error attributes).
- Avoids over-abstraction: only primitives needed by auth and immediate pages are created.

---

## 6. Centralized Routing Architecture

### Context & Requirements
- Constitution Principle III mandates client-side navigation strictly centralized in a single routes file (`src/routes/AppRoutes.tsx`).
- Routes required:
  - `/` -> `HomePage` (Index placeholder with link to `/register`)
  - `/register` -> `RegisterPage` (Registration form view)
  - `/login` -> `LoginPage` (Placeholder for future login screen)
- Layout wrapper: `MainLayout` wrapping all application routes via `<Outlet />`.

### Decision
- Create `src/routes/AppRoutes.tsx` exporting `AppRoutes` component configuring React Router `<Routes>`:
  ```tsx
  <Routes>
    <Route path="/" element={<MainLayout />}>
      <Route index element={<HomePage />} />
      <Route path="register" element={<RegisterPage />} />
      <Route path="login" element={<LoginPage />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Route>
  </Routes>
  ```
- Wrap `<AppRoutes />` with `<BrowserRouter>` and `<AuthProvider>` in `src/main.tsx`.

### Rationale
- Completely adheres to Principle III of the project constitution.
- Provides seamless navigation between Home, Register, and Login without full page reloads.

---

## 7. Form Validation & Error Handling Strategy

### Context & Requirements
- FR-008: Validate email format, username (>= 3 chars alphanumeric/underscore), password (>= 6 chars).
- FR-014 & Principle I: Inline error messages beneath individual fields for validation errors.
- Top-of-card alert banner for network and server errors (e.g. 409 conflict, offline).
- Strictly omit raw technical errors, stack traces, and database internals from user view.

### Decision
1. **Client-Side Validation**:
   - Pre-submission client validation checks:
     - `email`: Required, valid email regex (`/^[^\s@]+@[^\s@]+\.[^\s@]+$/`).
     - `username`: Required, trimmed length >= 3, regex `^[a-zA-Z0-9_]+$`.
     - `password`: Required, trimmed length >= 6.
   - Field errors are tracked in state (`Record<string, string>`) and passed directly into the `error` prop of `Input.tsx`.
2. **Server & Network Error Sanitization**:
   - `authService.ts` catches network errors or HTTP error responses (e.g. 409 Conflict, 500 Internal Error).
   - Maps status codes to sanitized Spanish messages:
     - 409 Conflict: `"El correo electrónico o nombre de usuario ya se encuentra registrado."`
     - Network Error / Failed to fetch: `"No se pudo conectar con el servidor. Por favor verifica tu conexión a internet."`
     - 500 / Other: `"Ocurrió un error inesperado al procesar el registro. Intenta nuevamente más tarde."`
   - Rendered at top of `RegisterCard` via `AlertBanner`.

### Rationale
- Protects security and ensures friendly UX without exposing stack traces.
- Meets all acceptance criteria of User Story 2.
