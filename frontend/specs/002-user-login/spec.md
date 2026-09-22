# Feature Specification: User Login and Session Menu

**Feature Branch**: `002-user-login`

**Created**: 2026-09-22

**Status**: Draft

**Input**: User description: "Modify the LoginPage to implement the login flow. Use the cancha-pelota.png file located in the assets folder as the background. The color styles must be the same as those used in the RegisterPage. In the center of the screen, there should be a LoginForm containing a title, \"Iniciar sesión\". Below the title, there should be two fields (one for email and one for password). Below the fields, there should be a login button, and below the button, there should be a text saying \"¿No tienes cuenta? Regístrate aquí\", where \"aquí\" should be a link to the RegisterPage. Both fields are required and must have the same validation rules as the corresponding fields in the registration form. Validation errors must be displayed following the same behavior and styling used in the registration form. After logging in successfully, redirect the user to the home page. If a user is logged in, the navbar should replace the \"Iniciar sesión\" text with a button containing a user icon and the logged-in user's username. When the username button is clicked, display a dropdown menu below the button and the navbar. The dropdown menu must contain the username, the user's email, and a \"Cerrar sesión\" option consisting of a logout icon and the text \"Cerrar sesión\". Logging out should end the session and redirect the user to the home page. The dropdown menu should only be displayed when a user is logged in. The navbar must show the \"Iniciar sesión\" option again after the user logs out."

## Clarifications

### Session 2026-09-22

- Q: What contract should the login flow use to authenticate the user? → A: `POST /auth/login` with `{ email, password }` and response `{ token, expiresAt, user }`.
- Q: Should `expiresAt` automatically invalidate the session when it expires? → A: Persist `expiresAt`, but end the session only when the backend rejects a request because the session is no longer valid.

## User Scenarios & Testing _(mandatory)_

### User Story 1 - Existing User Login (Priority: P1)

As a registered user, I want to sign in with my email and password so that I can access the application's Home page as an authenticated user.

**Why this priority**: Signing in is the primary path for returning users and is required to make the existing registration flow useful after the initial session ends.

**Independent Test**: Navigate to `/login`, submit valid credentials for an existing account, and verify that the session is established and the user is redirected to `/`.

**Acceptance Scenarios**:

1. **Given** an unauthenticated visitor on `/login`, **When** they enter valid credentials and submit the form, **Then** the user is authenticated and redirected to the Home page (`/`).
2. **Given** an unauthenticated visitor on `/login`, **When** authentication fails, **Then** the login form remains visible and presents a clear error without redirecting the visitor.
3. **Given** a user completes login successfully, **When** the Home page loads, **Then** the navbar identifies the authenticated user by username instead of showing the sign-in option.

---

### User Story 2 - Login Validation and Navigation (Priority: P2)

As a visitor using the login form, I want the same field validation and feedback conventions as registration so that I can correct missing or invalid credentials quickly and move between authentication pages without dead ends.

**Why this priority**: Consistent validation reduces confusion and prevents incomplete credentials from being submitted.

**Independent Test**: Submit the login form with empty fields and invalid email or password values, then verify inline feedback and the registration link.

**Acceptance Scenarios**:

1. **Given** an unauthenticated visitor on `/login`, **When** they submit either required field empty, **Then** the corresponding inline validation message appears using the registration form's behavior and styling, and authentication is not attempted.
2. **Given** an unauthenticated visitor enters an email that does not meet the registration form's email rule, **When** they submit, **Then** an inline email validation message appears beneath the email field.
3. **Given** an unauthenticated visitor enters a password that does not meet the registration form's password rule, **When** they submit, **Then** an inline password validation message appears beneath the password field.
4. **Given** a visitor viewing `/login`, **When** they select the `aquí` link in "¿No tienes cuenta? Regístrate aquí", **Then** they navigate to the registration page at `/register`.

---

### User Story 3 - Authenticated Navbar and Logout (Priority: P1)

As an authenticated user, I want to see my identity in the navbar and have a clear logout action so that I can inspect my account details and end my session safely.

**Why this priority**: Session visibility and logout are essential account controls and affect every authenticated page.

**Independent Test**: Establish an authenticated session, open the username control in the navbar, verify its account details and logout action, then log out and verify the public navbar state.

**Acceptance Scenarios**:

1. **Given** a logged-in user, **When** the navbar renders, **Then** it replaces the "Iniciar sesión" text with a control containing a user icon and the user's username.
2. **Given** a logged-in user, **When** they select the username control, **Then** a dropdown appears below the control and navbar containing the username, the user's email, and a "Cerrar sesión" action with a logout icon and the text "Cerrar sesión".
3. **Given** a logged-in user with the dropdown open, **When** they select "Cerrar sesión", **Then** the session ends, the dropdown closes, the user is redirected to `/`, and the navbar shows "Iniciar sesión" again.
4. **Given** an unauthenticated visitor, **When** the navbar renders, **Then** no account dropdown is displayed and the navbar shows the "Iniciar sesión" option.

---

### User Story 4 - Consistent Authentication Presentation (Priority: P3)

As a visitor, I want the login screen to match the registration screen so that the authentication experience feels like one coherent part of the application across devices.

**Why this priority**: Consistent visual treatment makes the login flow recognizable and preserves the established product identity.

**Independent Test**: Open `/login` at mobile and desktop viewport sizes and verify the shared navbar, background asset, colors, centered form, and responsive behavior.

**Acceptance Scenarios**:

1. **Given** a visitor opens `/login`, **When** the page renders, **Then** it uses the `cancha-pelota.png` background and the same color treatment, navbar, and form presentation established by the registration page.
2. **Given** a visitor opens `/login` on a viewport from 320px wide through desktop sizes, **When** the form renders, **Then** the title, fields, button, validation messages, and registration link remain readable, centered, and usable without horizontal overflow or overlap.
3. **Given** a visitor opens `/login`, **When** the form renders, **Then** it contains the title "Iniciar sesión", labeled email and password fields, a login button, and the text "¿No tienes cuenta? Regístrate aquí" with only `aquí` acting as the registration link.

---

### Edge Cases

- **Whitespace-only credentials**: Values containing only spaces are treated as empty, and the corresponding required-field message is displayed without attempting authentication.
- **Invalid email syntax**: An email that fails the registration form's established email rule is rejected locally with inline feedback.
- **Authentication failure**: Invalid credentials or a non-server authentication failure leaves the user on the login page and displays the backend-provided client message or an appropriate user-facing error in the established form error area.
- **Server failure**: A server error displays a generic, non-technical message and does not expose stack traces or internal details.
- **Repeated submission**: The login action prevents duplicate submissions while an authentication request is active.
- **Session unavailable or rejected**: If no authenticated session exists, or the backend rejects a request because the session is no longer valid, the navbar shows "Iniciar sesión" and does not render the account dropdown.
- **Dropdown state after logout**: Logging out always closes the dropdown and removes access to the prior user's account details.
- **Small viewports**: The navbar control and dropdown remain usable on narrow screens without clipping or horizontal scrolling.

## Requirements _(mandatory)_

### Functional Requirements

- **FR-001**: System MUST provide the login page at the `/login` route within the existing application layout.
- **FR-002**: System MUST render a centered login form with the title "Iniciar sesión", a labeled email field, a labeled password field, a login button, and the text "¿No tienes cuenta? Regístrate aquí".
- **FR-003**: System MUST make only the word `aquí` in the registration prompt a link to `/register`.
- **FR-004**: System MUST require both email and password fields before attempting authentication.
- **FR-005**: System MUST require an email with standard email address syntax, applying the same required-field behavior, inline message placement, and visual styling used by the registration form.
- **FR-006**: System MUST prevent authentication attempts when local validation fails and MUST display the relevant validation message directly beneath the affected field.
- **FR-007**: System MUST authenticate valid credentials by sending `{ email, password }` to `POST /auth/login` and MUST process a successful response containing `{ token, expiresAt, user }`.
- **FR-008**: System MUST redirect a user to the Home page (`/`) after successful authentication.
- **FR-009**: System MUST display a user icon and the authenticated user's username in the navbar in place of the "Iniciar sesión" option whenever a valid session exists.
- **FR-010**: System MUST open a dropdown below the username control when an authenticated user selects it.
- **FR-011**: The authenticated-user dropdown MUST display the logged-in user's username, email, and a "Cerrar sesión" action containing a logout icon and the text "Cerrar sesión".
- **FR-012**: System MUST render the authenticated-user dropdown only when a valid user session exists.
- **FR-013**: System MUST end the authenticated session when the user selects "Cerrar sesión", close the dropdown, and redirect the user to `/`.
- **FR-014**: System MUST restore the public navbar state with the "Iniciar sesión" option after logout, when no session exists, or when the backend rejects a request because the session is no longer valid.
- **FR-015**: System MUST display authentication failures using the existing form feedback conventions, showing client-intended non-server error messages to the user and using a generic message for server failures without exposing technical details.
- **FR-016**: System MUST prevent duplicate login submissions while an authentication request is in progress.
- **FR-017**: System MUST use the `cancha-pelota.png` asset and the same background, navbar, form, and responsive color treatment as the registration page.
- **FR-018**: System MUST keep the login form, navigation controls, feedback, and account dropdown usable from 320px-wide mobile viewports through desktop viewports without horizontal scrolling or overlapping content.

### Key Entities _(include if feature involves data)_

- **Login Credentials**: The user's email and password submitted to authenticate an existing account.
- **Authentication Session**: The active authenticated state, token expiration timestamp, and user profile, including username and email, returned by `POST /auth/login`; `expiresAt` is persisted for session data but backend rejection determines when the session is invalidated.
- **Account Menu**: The authenticated-only navigation menu containing the current user's identity details and logout action.

## Success Criteria _(mandatory)_

### Measurable Outcomes

- **SC-001**: At least 95% of users with valid credentials reach the Home page after one login submission under normal service conditions.
- **SC-002**: 100% of empty or invalid email and password submissions receive an inline, field-specific message without an authentication attempt.
- **SC-003**: 100% of authenticated navbar renders show the current username and provide access to the current email and logout action through the account menu.
- **SC-004**: 100% of logout actions end the session, close the account menu, redirect to `/`, and restore the "Iniciar sesión" navbar option.
- **SC-005**: Users can reach the registration page from the login prompt in one click, with the link target `/register`.
- **SC-006**: Login and account-menu content remains readable and operable without horizontal scrolling or overlapping elements at widths from 320px to 2560px.
- **SC-007**: 100% of server failures shown during login use a generic user-facing message and expose no stack traces or internal service details.

## Assumptions

- The existing registration form is the source of truth for email syntax, password constraints, required-field behavior, validation message placement, and validation styling.
- The existing authentication service and session mechanism provide the login operation and store the returned token, `expiresAt`, and user profile; the client does not proactively invalidate the session solely because the timestamp has passed.
- The Home page remains the post-login and post-logout destination at `/`.
- The existing navbar is shared by the login, registration, and Home pages; account-state changes are reflected wherever that navbar is rendered.
- The `cancha-pelota.png` asset is available in the application's assets and is the same background asset used by registration.
- The application uses the existing icon set for user and logout icons, preserving the established visual language.
- Password recovery, account editing, persistent login preferences, and authorization for additional protected routes are outside this feature's scope.
