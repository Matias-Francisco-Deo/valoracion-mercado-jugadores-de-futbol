# Quickstart: User Login and Session Menu

**Feature**: `002-user-login`

## Prerequisites

- Node.js and pnpm installed.
- Frontend dependencies installed with `pnpm install`.
- `VITE_API_BASE_URL` configured for a backend exposing `POST /auth/login`.
- A registered test account with a known email and password.

## Start the application

```bash
pnpm install
pnpm dev
```

Open the Vite URL and use the `/login` route.

## Validation scenarios

1. **Visual parity**: Open `/login` and confirm the `cancha-pelota.png` background, orange navbar, centered gray card, title `Iniciar sesión`, and responsive layout match `/register`.
2. **Required fields**: Submit with both fields empty. Confirm inline required messages appear beneath email and password and no request is sent.
3. **Schema rules**: Submit an invalid email. Confirm field-level Yup messages match the registration conventions.
4. **Successful login**: Submit valid credentials. Confirm `POST /auth/login` receives `{ email, password }`, the session stores token, `expiresAt`, and user, and navigation reaches `/`.
5. **Authenticated navbar**: On `/`, confirm the navbar shows the user icon and username instead of `Iniciar sesión`. Open the control and confirm username, email, and `Cerrar sesión` with logout icon are visible.
6. **Logout**: Select `Cerrar sesión`. Confirm the session is cleared, the menu closes, navigation reaches `/`, and `Iniciar sesión` returns.
7. **Failure handling**: Use invalid credentials and a simulated server/network failure. Confirm client messages are shown for non-500 failures and generic messaging is used for server/network failures.
8. **Responsive behavior**: Repeat the visual and account-menu checks at 320px, 768px, 1024px, and wide desktop widths. Confirm no horizontal scroll or overlap.

## Verification commands

```bash
pnpm build
pnpm lint
```

Expected result: both commands complete without TypeScript or lint errors. Manual/API verification should follow the endpoint details in [contracts/auth-login.md](contracts/auth-login.md) and entity definitions in [data-model.md](data-model.md).
