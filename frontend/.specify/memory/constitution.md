<!--
Sync Impact Report:
- Version change: Unratified Template → 1.0.0
- List of modified principles:
  - [PRINCIPLE_1_NAME] → I. Frontend-Only Architecture & Scope Isolation
  - [PRINCIPLE_2_NAME] → II. Strict TypeScript & Type Safety Gate
  - [PRINCIPLE_3_NAME] → III. Centralized Routing Architecture
  - [PRINCIPLE_4_NAME] → IV. Component Modularity & Single Responsibility
  - [PRINCIPLE_5_NAME] → V. Responsive & Adaptive Fluid Layouts
- Added sections:
  - Technology Stack & Quality Standards
  - Development Workflow & Verification Gates
  - Governance
- Removed sections:
  - None
- Follow-up TODOs:
  - None (all placeholders resolved; no deferred items).
-->

# Football Player Market Valuation Frontend Constitution

## Core Principles

### I. Frontend-Only Architecture & Scope Isolation
The project is strictly a client-side web frontend; no backend services, server-side business logic, or database layers are to be developed or maintained in this repository. All data fetching must interface with external APIs through clean abstractions or client-side mocks. Crucially, the UI MUST NOT directly display raw backend errors, stack traces, database messages, or internal implementation details to end users. All network or operational failures must be intercepted and translated into clear, actionable, user-friendly notices.
*Rationale: Preserves strict boundaries between presentation and server concerns, prevents security leaks via technical diagnostic disclosure, and guarantees a graceful end-user experience.*

### II. Strict TypeScript & Type Safety Gate
All application code MUST be implemented using React, Tailwind CSS, and TypeScript with rigorous type safety. The use of `any` is strictly prohibited across the codebase. If an exceptional scenario emerges where `any` seems unavoidable, the developer MUST consult the project owner beforehand and secure explicit written approval accompanied by a formal justification. Every line of code MUST comply with the project's configured type-checking (`tsc -b`) and linting/formatting rules (`oxlint`).
*Rationale: Compile-time type verification eliminates entire classes of runtime errors, ensures self-documenting data contracts, and prevents unchecked dynamic types from polluting the codebase.*

### III. Centralized Routing Architecture
Client-side navigation MUST be powered by React Router. All route declarations, layout wrappers, and navigation paths MUST be strictly centralized in a single dedicated `routes` file (e.g.`src/routes/AppRoutes.tsx`). Defining distributed, nested, or ad-hoc `<Route>` configurations across arbitrary component files is forbidden.
*Rationale: Centralization establishes a single source of truth for the application's page hierarchy, making navigation flow, route auditing, and deep linking transparent and maintainable.*

### IV. Component Modularity & Single Responsibility
UI components MUST be designed as reusable units governed strictly by the Single Responsibility Principle (SRP). Components MUST be divided into smaller subcomponents whenever they grow difficult to read, test, or maintain. Code SHOULD consistently prioritize readability and long-term maintainability over clever, dense, or prematurely optimized solutions. Components, functions, variables, and source files MUST use clear, descriptive, and intention-revealing names.
*Rationale: Granular, single-purpose components promote code reuse, simplify unit testing, and reduce cognitive load during collaborative maintenance.*

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
2. **Quality Gates & Static Analysis**:
   - Zero linter errors or warnings on pull requests (`pnpm lint` / `oxlint`).
   - Zero TypeScript compilation errors (`tsc -b`).
   - Clean, descriptive naming conventions without cryptic acronyms or single-letter identifiers (except common loop counters).
3. **Error Handling & User Communication**:
   - Intercept all runtime API failures and present sanitized feedback (e.g., via non-blocking notifications or user-facing alert components).
   - Technical error diagnostics and stack traces must be restricted to developer console logging.

## Development Workflow & Verification Gates

1. **Pre-Commit / Pre-PR Verification**:
   - Verify static typing with `tsc -b`.
   - Run linter verification with `pnpm lint`.
   - Verify that all newly created routes are registered in the centralized route file.
2. **Component Architecture Review**:
   - Verify that any component exceeding a single clear responsibility has been decomposed.
   - Verify that layouts utilize fluid Tailwind CSS classes rather than hardcoded pixel dimensions.
   - Verify viewport responsiveness across mobile (320px+), tablet (768px+), and desktop (1024px+) viewports.
3. **Type Safety Exception Review**:
   - If an `any` type is detected during review, verify that explicit owner authorization and documented justification are present in the PR description and code comment.

## Governance

This Constitution represents the authoritative governance specification for the frontend codebase and supersedes informal conventions or conflicting legacy patterns.

- **Amendment Policy**: Any amendment, addition, or removal of principles requires a documented rationale, owner review, and consensus.
- **Versioning Policy**: Follows Semantic Versioning:
  - **MAJOR**: Incompatible governance shifts, removal or fundamental weakening of core principles.
  - **MINOR**: Addition of new principles, structural expansion, or substantial quality gate additions.
  - **PATCH**: Non-substantive wording fixes, formatting revisions, or typo corrections.
- **Compliance Review**: All contributors and automated agents must adhere to these principles when planning, specifying, or implementing features in this repository.

**Version**: 1.0.0 | **Ratified**: 2026-09-16 | **Last Amended**: 2026-09-16
