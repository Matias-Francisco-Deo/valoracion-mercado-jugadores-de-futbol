<!--
Sync Impact Report:
- Version change: 1.0.0 → 2.0.0
- List of modified principles:
  - I. Frontend-Only Architecture & Scope Isolation → I. Frontend-Only Architecture, Backend Integration & Error Handling
  - III. Centralized Routing Architecture → III. Centralized Routing & Lazy-Loaded Architecture
  - IV. Component Modularity & Single Responsibility → IV. Component Modularity, Reuse & Pragmatic Abstraction
- Added sections:
  - Technology Stack & Quality Standards § 2 (Styling & CSS Architecture)
  - Technology Stack & Quality Standards § 4 (Backend Integration & Response Handling)
  - Development Workflow & Verification Gates § 3 (Backend Integration & Environment Review)
- Removed sections:
  - None
- Follow-up TODOs:
  - None (all principles and rules fully resolved; no deferred items).
-->

# Football Player Market Valuation Frontend Constitution

## Core Principles

### I. Frontend-Only Architecture, Backend Integration & Error Handling
The project is strictly a client-side web frontend; no backend services, server-side business logic, or database layers are to be developed or maintained in this repository. The agent is strictly prohibited from modifying backend code.
Communication with the backend MUST occur exclusively through the project's existing configuration and abstractions. The current `.env` configuration MUST be respected; backend URLs, hosts, ports, or other configuration values MUST NOT be hardcoded directly into components, pages, or services.
When a feature requires backend communication, the agent MUST first verify whether the required endpoint and payload information is already available in the frontend. If endpoint details are missing and the backend is accessible, the agent MAY inspect backend controller and DTO code to obtain the required contract information. If the backend is not accessible, the agent MUST request the missing information from the user rather than inventing endpoints or request/response structures.
Backend responses MUST NOT be sanitized generically. For responses that do not correspond to an HTTP 500 error, when the backend provides a response message intended for the client, the application MUST display that message to the user. Conversely, HTTP 500 errors MUST be handled generically and MUST NOT expose technical details, stack traces, or internal server information.
*Rationale: Preserves strict architectural separation, guarantees configuration integrity across environments, ensures accurate contract adherence without guesswork, and delivers informative client messages while securing internal server failure details.*

### II. Strict TypeScript & Type Safety Gate
All application code MUST be implemented using React, Tailwind CSS, and TypeScript with rigorous type safety. The use of `any` is strictly prohibited across the codebase. If an exceptional scenario emerges where `any` seems unavoidable, the developer MUST consult the project owner beforehand and secure explicit written approval accompanied by a formal justification. Every line of code MUST comply with the project's configured type-checking (`tsc -b`) and linting/formatting rules (`oxlint`).
*Rationale: Compile-time type verification eliminates entire classes of runtime errors, ensures self-documenting data contracts, and prevents unchecked dynamic types from polluting the codebase.*

### III. Centralized Routing & Lazy-Loaded Architecture
Client-side navigation MUST be powered by React Router. All route declarations, layout wrappers, and navigation paths MUST be strictly centralized in a single dedicated `routes` file (e.g., `src/routes/AppRoutes.tsx`). Defining distributed, nested, or ad-hoc `<Route>` configurations across arbitrary component files is forbidden. Crucially, all application pages MUST be loaded using lazy loading (e.g., `React.lazy`).
*Rationale: Centralization establishes a single source of truth for the application's page hierarchy, while mandatory lazy loading optimizes initial bundle footprint, accelerates page load performance, and ensures efficient code-splitting.*

### IV. Component Modularity, Reuse & Pragmatic Abstraction
UI components MUST be governed strictly by the Single Responsibility Principle (SRP). Existing components MUST be used whenever they are suitable for the current need and allow code duplication to be avoided; duplicate components MUST NOT be created when one already exists that can correctly fulfill the same function. General-purpose components SHOULD be created when they do not already exist, are necessary for the current implementation, and have reasonable utility for future components or pages. However, generic abstractions or components MUST NOT be created solely as a precaution if there is no current need or reasonably clear future utility (avoid speculative over-engineering / YAGNI). Components MUST be divided into smaller subcomponents whenever they grow difficult to read, test, or maintain. Code SHOULD consistently prioritize readability and maintainability over dense or prematurely abstracted solutions. Components, functions, variables, and source files MUST use clear, descriptive, and intention-revealing names.
*Rationale: Maximizes code reuse, avoids divergent duplicate UI elements, prevents speculative abstraction overhead, and maintains codebase clarity through focused, single-purpose components.*

### V. Responsive & Adaptive Fluid Layouts
The frontend MUST be responsive and fully functional across mobile phones, tablets, and desktop computers from initial implementation. Fixed widths and fixed heights MUST be avoided in favor of fluid, adaptive layouts that dynamically adjust to available screen dimensions. Typography and text elements MUST remain readable across all display sizes without introducing unwanted horizontal scrolling. Images and multimedia assets MUST scale proportionally without overflowing or breaking their parent layout containers.
*Rationale: Modern web users access applications across a diverse spectrum of screen sizes; enforcing fluid constraints from the start eliminates responsive debt and layout regressions.*

## Technology Stack & Quality Standards

1. **Framework & Ecosystem**:
   - UI Library: React 19+
   - Styling: Tailwind CSS v4+ with utility-first responsive classes
   - Language: TypeScript with strict compiler options enabled
   - Tooling & Bundler: Vite
   - Routing: React Router (`react-router-dom`)
2. **Styling & CSS Architecture**:
   - General styles that can be reused across multiple pages or components MUST be defined in `index.css`.
   - Duplicating global styles across multiple pages or components SHOULD be strictly avoided.
3. **Quality Gates & Static Analysis**:
   - Zero linter errors or warnings on pull requests (`pnpm lint` / `oxlint`).
   - Zero TypeScript compilation errors (`tsc -b`).
   - Clean, descriptive naming conventions without cryptic acronyms or single-letter identifiers (except common loop counters).
4. **Backend Integration & Response Handling**:
   - Backend communication strictly through centralized API abstractions and `.env` config (no hardcoded URLs, hosts, or ports).
   - Backend responses MUST NOT be sanitized generically.
   - For non-500 HTTP responses, display backend client messages directly to the user.
   - HTTP 500 errors MUST be handled generically without exposing technical details, stack traces, or internal server information.
   - The agent is strictly prohibited from modifying backend code.

## Development Workflow & Verification Gates

1. **Pre-Commit / Pre-PR Verification**:
   - Verify static typing with `tsc -b`.
   - Run linter verification with `pnpm lint`.
   - Verify that all newly created routes are registered in the centralized route file.
   - Verify that all application page components are loaded using lazy loading (`React.lazy`).
2. **Component Architecture & Reuse Review**:
   - Verify that existing components are reused where suitable and no duplicate components have been introduced.
   - Verify that any newly created general-purpose component has demonstrated current need and future utility, and that no speculative/precautionary abstractions were added.
   - Verify that any reusable general styles are consolidated in `index.css` rather than duplicated across pages or components.
   - Verify that any component exceeding a single clear responsibility has been decomposed.
   - Verify viewport responsiveness across mobile (320px+), tablet (768px+), and desktop (1024px+) viewports.
3. **Backend Integration & Environment Review**:
   - Verify that backend communication relies exclusively on project abstractions and `.env` configuration (no hardcoded URLs/ports).
   - Verify that no backend code was modified.
   - Verify that non-500 responses display client-intended messages and HTTP 500 errors are handled generically without leaking internals.
4. **Type Safety Exception Review**:
   - If an `any` type is detected during review, verify that explicit owner authorization and documented justification are present in the PR description and code comment.

## Governance

This Constitution represents the authoritative governance specification for the frontend codebase and supersedes informal conventions or conflicting legacy patterns.

- **Amendment Policy**: Any amendment, addition, or removal of principles requires a documented rationale, owner review, and consensus.
- **Versioning Policy**: Follows Semantic Versioning:
  - **MAJOR**: Incompatible governance shifts, removal or fundamental weakening of core principles, or principle redefinitions.
  - **MINOR**: Addition of new principles, structural expansion, or substantial quality gate additions.
  - **PATCH**: Non-substantive wording fixes, formatting revisions, or typo corrections.
- **Compliance Review**: All contributors and automated agents must adhere to these principles when planning, specifying, or implementing features in this repository.

**Version**: 2.0.0 | **Ratified**: 2026-09-16 | **Last Amended**: 2026-09-21
