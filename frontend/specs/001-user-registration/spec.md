# Feature Specification: User Registration Page & Layout

**Feature Branch**: `feature/front-startup`

**Created**: 2026-09-19

**Status**: Draft

**Input**: User description: "We will create a registration page integrated into the website's overall layout. The page will allow new users to register and authenticate using their email, username, and password. If authentication is successful, they will be redirected to the Home page (temporarily empty), which will function as the main page (index) of the application. The layout will have an orange navbar with the color #FF9500, and the layout will contain the registration page. The background of the registration page will be green (#096638) and will feature the image cancha-pelota.png, located in the assets folder, displayed on top of the background and occupying the same space as the background. The registration form will be centered and will have a background color of #A8A8A8. The form will contain a tittle "Registrarse", an input field for email, an input field for username, an input field for password, a register button and a text message saying "¿Ya tienes cuenta? Inicia sesión aqui", the word "aqui" must function as a link to the login page, which will not be implemented yet. all inputs muts have their respective label"

## Clarifications

### Session 2026-09-19

- Q: How should the frontend handle user registration and session persistence upon submitting the form? (FR-012) → A: Connect to backend `POST /auth/register` and persist the JWT token and user info in `localStorage`.
- Q: What validation constraints should the client enforce on the username and password fields before submission? (FR-008) → A: Username min 3 characters (alphanumeric and underscores); password min 6 characters.
- Q: Where and how should registration errors (such as validation errors and backend registration failures) be displayed to the user? (FR-014) → A: Inline messages beneath individual input fields for validation errors; top-of-card alert banner inside the registration card for server/network errors.
- Q: What URL route should host the registration page, and how should unauthenticated visitors reaching the root URL ('/') be handled? (FR-002, FR-013) → A: Registration is hosted at `/register`; root index `/` displays the Home page placeholder with a navigation link to `/register`.
- Q: What brand elements and navigation items should be displayed inside the orange top navigation bar? (FR-001) → A: Platform title ("Valoración de Jugadores") on the left, with an "Iniciar Sesión" link on the right pointing to `/login`.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - New User Registration and Home Redirection (Priority: P1)

As a new visitor to the football player market valuation platform, I want to register a new account using my email, username, and password so that I become authenticated and can immediately access the application's main Home page.

**Why this priority**: Account registration is the primary gateway for users to join and access the application. Without registration and initial redirection to the Home index, users cannot enter the platform.

**Independent Test**: Navigate to the registration page (`/register`), enter valid registration details into the email, username, and password inputs, submit the form, and verify that the user is authenticated and redirected to the Home page (`/`).

**Acceptance Scenarios**:

1. **Given** an unauthenticated visitor on the registration page at `/register`, **When** they fill in valid values for email, username, and password and click the registration button, **Then** the account is registered, the user is authenticated, and they are redirected to the Home page (`/`).
2. **Given** an unauthenticated visitor navigating to the root index (`/`), **When** the Home page renders, **Then** it displays placeholder content along with a navigation action leading to `/register`.
3. **Given** a user successfully registers, **When** they arrive at the Home page (`/`), **Then** the page loads as the index view of the application displaying its main placeholder content.

---

### User Story 2 - Form Input Validation & User Feedback (Priority: P2)

As a prospective user filling out the registration form, I want clear and prompt feedback when I enter missing or invalid information so that I know exactly how to correct my entries before submitting.

**Why this priority**: Input validation prevents corrupt or incomplete user data from reaching the registration process and ensures visitors receive helpful, user-friendly guidance.

**Independent Test**: Attempt to submit the form with empty fields, invalid email format, or incomplete credentials, and verify that appropriate validation messages are displayed next to the respective inputs without submitting.

**Acceptance Scenarios**:

1. **Given** an unauthenticated user on the registration form, **When** they leave one or more required fields blank and click the register button, **Then** the system highlights the missing fields with inline validation labels directly beneath each field and halts submission.
2. **Given** an unauthenticated user entering an improperly formatted email address, **When** they attempt to submit, **Then** an informative inline error message indicates the required email format beneath the email field.
3. **Given** an unauthenticated user entering a username with fewer than 3 characters or invalid symbols, or a password with fewer than 6 characters, **When** they attempt to submit, **Then** the system presents specific inline validation feedback directly beneath the respective input.
4. **Given** an unexpected operational or network failure during submission, or a conflict such as an already-registered account, **When** the error occurs, **Then** a sanitized, friendly notification banner is displayed at the top of the registration card without technical stack traces.

---

### User Story 3 - Navigation to Login for Existing Users (Priority: P3)

As an existing user who has navigated to the registration page, I want an easily accessible link to the login page so that I can sign in to my existing account instead of creating a new one.

**Why this priority**: Avoids dead-ends for existing members who mistakenly reach the registration screen and establishes seamless transition between authentication views.

**Independent Test**: Locate the message "¿Ya tienes cuenta? Inicia sesión aqui" on the registration form, click the "aqui" link, and verify that the browser transitions to the designated login page route.

**Acceptance Scenarios**:

1. **Given** a user viewing the registration form, **When** they click the link "aqui" within the prompt "¿Ya tienes cuenta? Inicia sesión aqui", **Then** the browser navigates to the login route.

---

### User Story 4 - Visual Layout and Responsive Presentation (Priority: P4)

As a user on any device (mobile phone, tablet, or desktop), I want to view a cohesive, visually appealing registration interface with an orange top navigation bar, soccer field background image overlay, and a centered gray registration card that adapts smoothly to my screen dimensions.

**Why this priority**: Consistent visual branding and responsive design ensure accessibility, readability, and a professional user experience across all supported devices.

**Independent Test**: Open the application across multiple screen resolutions (320px mobile up to desktop) and verify that the navbar remains orange (`#FF9500`), the background remains green (`#096638`) with the field ball image (`cancha-pelota.png`), and the form card remains centered with its gray background (`#A8A8A8`) without layout breakage or horizontal scrollbars.

**Acceptance Scenarios**:

1. **Given** any screen size from mobile (320px+) to desktop (1024px+), **When** the registration page is displayed, **Then** the top navigation bar displays the orange color `#FF9500`, featuring the brand title "Valoración de Jugadores" on the left and an "Iniciar Sesión" navigation link pointing to `/login` on the right.
2. **Given** the registration view, **When** the page renders, **Then** the background displays the green tone `#096638` covered by the `cancha-pelota.png` asset occupying the full background space.
3. **Given** any supported viewport, **When** observing the registration form, **Then** it is centered with background `#A8A8A8` and contains the title "Registrarse", distinct labeled inputs for email, username, and password, and the submit button.

---

### Edge Cases

- **Form Submission with Whitespace**: How does the system handle inputs containing only space characters? The system trims whitespace and treats such fields as empty, preventing submission and displaying inline required-field messages.
- **Rapid Multiple Clicks**: What happens if a user repeatedly clicks the registration button during an active submission? The registration button disables or prevents duplicate submissions while the request is in progress.
- **Account Already Exists (Conflict)**: How does the system handle duplicate email or username registration attempts (409 Conflict)? The system displays a clear, sanitized alert banner at the top of the form card informing the user that the account already exists and advising them to sign in or use different credentials.
- **Network or Service Interruption**: How does the system handle lost connectivity during registration? The user receives a friendly, non-technical notice in an alert banner at the top of the registration card stating that the service is temporarily unreachable, while preserving their entered input data.
- **Extreme Small Viewports**: How does the centered registration form handle screens under 360px width? The form card fluidly scales with adequate inner padding so that input labels, fields, and buttons remain fully readable and operable without horizontal clipping.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: System MUST render an overall application layout featuring an orange top navigation bar with the color hex `#FF9500`, displaying the brand title "Valoración de Jugadores" on the left and an "Iniciar Sesión" navigation link pointing to `/login` on the right.
- **FR-002**: System MUST host the registration view at the route `/register` within the primary content region of the application layout.
- **FR-003**: System MUST style the registration page background using the solid green color `#096638`.
- **FR-004**: System MUST display the image `cancha-pelota.png` (sourced from the application assets) layered on top of the green background, occupying the same space as the background.
- **FR-005**: System MUST render a centered registration card featuring a solid background color of `#A8A8A8`.
- **FR-006**: System MUST display the title "Registrarse" prominently inside the registration form card.
- **FR-007**: System MUST provide three distinct input fields: Email, Username, and Password, each paired with its own accessible, clearly visible text label.
- **FR-008**: System MUST validate that all three input fields are populated, that the email conforms to standard email address syntax, that the username contains at least 3 characters (alphanumeric and underscores), and that the password contains at least 6 characters before completing registration.
- **FR-009**: System MUST provide a visible registration button to submit the form.
- **FR-010**: System MUST render an informational text below the registration controls reading "¿Ya tienes cuenta? Inicia sesión aqui".
- **FR-011**: System MUST configure the word "aqui" within the informational text as a clickable link that navigates to the login page route (`/login`).
- **FR-012**: System MUST send registration credentials to the backend endpoint `POST /auth/register`, persist the returned JWT token and user profile in `localStorage`, and immediately redirect the authenticated user to the Home page.
- **FR-013**: System MUST designate the Home page as the primary index route (`/`) of the application, rendering a clean placeholder view with a navigation action to `/register` while pending future feature implementation.
- **FR-014**: System MUST present clear, user-friendly error feedback when validation or registration fails: form input validation messages MUST be rendered inline directly beneath the corresponding fields, and backend/network failures (including duplicate user conflicts) MUST be rendered in an alert banner at the top of the registration card, strictly omitting raw server messages, database details, or stack traces.

### Key Entities *(include if feature involves data)*

- **Registration Credentials**: Represents the user-submitted registration payload, comprising:
  - `email`: Standard electronic mail address string used as primary contact and identity reference.
  - `username`: Unique display and account identifier (minimum 3 characters, alphanumeric and underscores).
  - `password`: Secret authentication credential (minimum 6 characters).
- **Authentication Session**: Represents the client-side authentication status, holding the JWT token and user profile in `localStorage` upon successful registration via `POST /auth/register` to authorize access to the Home index page.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Users can complete and submit the registration form in under 60 seconds.
- **SC-002**: 100% of input fields have associated explicit labels and accessible form controls.
- **SC-003**: 100% of successful registration submissions redirect the user to the Home index page in under 1 second under normal client conditions.
- **SC-004**: 100% of user-facing error scenarios display actionable, friendly error messages with zero exposure of stack traces or technical internals.
- **SC-005**: The registration card and layout adapt without horizontal scrollbars or element overlap across screen resolutions ranging from 320px to 2560px.
- **SC-006**: Navigation link "aqui" transitions the browser route to the login path with a single click.

## Assumptions

- The login page target (`/login`) will display a temporary placeholder or future-ready route since its full implementation is scheduled for a later phase.
- The Home page is the index route (`/`) accessible publicly with placeholder content and a link to `/register`, until future dashboard or player market features are developed.
- User authentication upon registration communicates with the backend `POST /auth/register` endpoint and persists the resulting JWT token and user profile in `localStorage`.
- Specific color hex values (`#FF9500` for navbar, `#096638` for registration page background, `#A8A8A8` for registration form card) are mandatory design tokens for this feature.
- The image asset `cancha-pelota.png` exists in the local assets repository and will be referenced directly for the page background overlay.
