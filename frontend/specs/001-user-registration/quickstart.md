# Quickstart & Verification Guide: User Registration Page & Layout

**Feature Branch**: `001-user-registration`  
**Feature Spec**: `specs/001-user-registration/spec.md`  
**Status**: Draft  

---

## 1. Prerequisites & Environment Setup

Ensure Node.js (v18+) and `pnpm` are installed.

```bash
# Verify environment
node -v
pnpm -v

# Install dependencies if not already installed
pnpm install
```

---

## 2. Running the Development Server

```bash
# Launch the Vite development server
pnpm dev
```
Open your browser at `http://localhost:5173`.

---

## 3. End-to-End Validation Scenarios

### Scenario 1: Initial Visit & Routing (P1)
1. Open `http://localhost:5173/`.
2. **Expected Outcome**:
   - The top navigation bar renders in orange (`#FF9500`) with the brand `"Overcode"` on the left and an `"Iniciar Sesión"` link on the right.
   - The Home page displays placeholder text with a call-to-action button or link leading to `/register`.
3. Click the link to navigate to `/register`.
4. **Expected Outcome**:
   - The route transitions to `/register` without a full page reload.

---

### Scenario 2: Visual Layout & Zero Vertical Scrollbar (P4)
1. On `http://localhost:5173/register`, inspect the page layout across viewport sizes (Desktop 1920x1080, Laptop 1366x768, Tablet 768x1024, Mobile 375x667).
2. **Expected Outcome**:
   - Background is solid green (`#096638`) overlaid with `cancha-pelota.png` covering the entire viewport beneath the navbar.
   - The registration card is centered with a gray background (`#A8A8A8`) and visible shadow.
   - **Crucial**: No vertical scrollbar is displayed in the window or layout viewport under standard resolutions.
   - Card displays the title `"Registrarse"`, three labeled fields (Email, Username, Password), the register button, and the footer text `"¿Ya tienes cuenta? Inicia sesión aqui"`.

---

### Scenario 3: Form Validation & Inline Errors (P2)
1. On `/register`, leave all fields blank and click the register button.
2. **Expected Outcome**:
   - Submission is halted immediately.
   - Inline error messages appear directly below each field indicating required input.
3. Enter an invalid email format (e.g. `invalid-email`).
4. **Expected Outcome**:
   - Inline error under email displays an invalid email notice.
5. Enter a username with fewer than 3 characters (e.g. `ab`) or special characters (e.g. `user!@#`).
6. **Expected Outcome**:
   - Inline error indicates username must be at least 3 alphanumeric/underscore characters.
7. Enter a password with fewer than 6 characters.
8. **Expected Outcome**:
   - Inline error indicates password must be at least 6 characters long.

---

### Scenario 4: Successful Registration & Session Redirection (P1)
1. Enter valid data:
   - Email: `juan.perez@example.com`
   - Username: `juan_perez`
   - Password: `password123`
2. Click the register button.
3. **Expected Outcome**:
   - Button enters loading state to prevent duplicate submissions.
   - On response (or mock/fallback success when backend is offline), AuthContext receives the JWT token and user profile.
   - Token and user data are persisted in `localStorage` under `auth_token` and `auth_user` solely via AuthContext.
   - The application immediately redirects the user to the Home page (`/`).
   - The Home page recognizes the authenticated state.

---

### Scenario 5: Server Conflict & Network Failure Handling (P2)
1. If the backend returns HTTP 409 Conflict (duplicate email or username):
   - **Expected Outcome**: An alert banner renders at the top of the registration card with a friendly notice: `"El correo electrónico o nombre de usuario ya se encuentra registrado."`
2. If backend connectivity fails:
   - **Expected Outcome**: An alert banner renders at the top of the card informing the user of the network issue, with zero exposure of stack traces or database errors.

---

### Scenario 6: Transition to Login Placeholder (P3)
1. From `/register`, locate the text `"¿Ya tienes cuenta? Inicia sesión aqui"`.
2. Click the link `"aqui"`.
3. **Expected Outcome**:
   - Browser navigates smoothly to `/login`.
   - The login route renders a clean placeholder view stating that login will be available in an upcoming phase.

---

## 4. Static Quality Checks

Verify that all code changes comply with the Constitution quality gates:

```bash
# Verify strict TypeScript compilation with zero errors
pnpm build

# Verify oxlint static analysis
pnpm lint
```
