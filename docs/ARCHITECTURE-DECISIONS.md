# Architecture Decisions and Review

**Review date:** 2026-11-19  
**Scope:** Blocks 0–2 code and documentation in this repository. This review does not authorize Block 3 implementation.

This was the point-in-time review produced before Block 3. Block 3 was subsequently requested and implemented; its module decisions are recorded in [ADR-0005](adr/ADR-0005-organisation-contact-boundary.md).

## Existing architecture

The codebase is a separate React/Vite frontend and Spring Boot REST API backed by PostgreSQL and Liquibase. The backend is a single deployable application. Its current source is a small identity capability organized in layers under `com.prosperr.crm.identity`:

```text
HTTP controllers / request DTOs
        ↓
identity services and use cases
        ↓
Spring Data repositories
        ↓
JPA entities + PostgreSQL schema (Liquibase)
```

Spring Security authenticates JWT requests, reloads active user state, and applies ADMIN policy to `/api/v1/admin/**`. React has a shared shell, role-based route guard, authentication context, a shared Axios client, reusable CRM/UI components, and pages. Client-side role routing is a UX boundary; backend authorization is the security boundary.

The persisted model currently contains `UserEntity` → `RoleEntity` with a single role per user. There are no organisation, contact, SQL, opportunity, meeting, revival, or handover entities yet. The `app_role` seed is a fixed set matching `RoleCode`; roles are not user-configurable.

## Current design findings

### Strengths to preserve

- The selected modular monolith and separate React frontend are explicit project constraints.
- API DTOs keep JPA entities out of JSON responses; password hashes are not in response types.
- Identity services use constructor injection and keep most account rules out of controllers.
- Role/state fields are private and password transitions update the related flags together.
- Liquibase owns the schema, and the integration scenario covers authentication and key access-control failures.
- Shared React components and a role navigation configuration give upcoming screens a reusable base.

### Improvements to address before or during the relevant feature work

| Priority | Finding | Recommendation | When |
|---|---|---|---|
| High | `AdminUserController` queries `UserRepository` directly for dashboard counts and repeats one count query per role. | Move summary behavior behind a service/query boundary and use one aggregate query if the dashboard expands. | Before adding more admin reporting. |
| High | `anyRequest().permitAll()` leaves unmatched non-API routes public by default. | Prefer deny-by-default after explicitly allowing required static/docs/health routes; review this with the tech lead before deployment. | Security hardening / before new API areas. |
| High | JWT decoding checks signature and expiry but does not require the expected issuer/audience. | Add issuer (and audience if deployment requires it) validation in the decoder and cover it with a negative test. | Before deployment or token federation. |
| Medium | `UserRepository` exposes `JpaSpecificationExecutor`, but no specification exists. | Drop the unused extension until a real dynamic-search query is approved. | Next repository change. |
| Medium | Admin user listing returns all rows; no API pagination/search exists. | Define filters/sort/page contract and use a database-backed page before account volume makes full-list reads costly. | Before broad admin rollout. |
| Medium | DTO records are nested in controllers/services, and service validation is partly manual despite `@Valid`. | Keep DTOs feature-scoped; add bean validation constraints and a consistent validation error contract as API shapes grow. | In the next API contract change; no mass move required. |
| Medium | A global `BadCredentialsException` response says “Invalid email or password,” even for a wrong current password. | Use operation-appropriate errors for password change while keeping login responses generic. | Next auth UX change. |
| Medium | `UserManagementPage` mixes API calls, form state, and rendering, and uses native prompts/alerts rather than the existing dialog/form components. | Split feature logic only as the screen grows; use accessible shared form/dialog primitives for future changes. | When editing this screen next. |
| Medium | Frontend tests cover three routing cases, not successful login, password change, or admin operations. Backend coverage is one long ordered integration test on H2 compatibility mode. | Add focused tests and include a real PostgreSQL migration/startup job in CI when CI is introduced. | Before Block 3 completion / CI setup. |
| Low | Several newer pages and backend classes are compressed into very long lines with inconsistent formatting. | Format files as they are touched and adopt a formatter; avoid a behavior-only rewrite of stable modules. | Incrementally. |
| Low | `RoleCode` and frontend role configuration mirror the same role set. | Keep the server authoritative and add contract/test checks if role changes become common; do not introduce code generation without evidence it pays off. | If role definitions change. |

## Architecture decisions

| ID | Decision | Status | Record |
|---|---|---|---|
| ADR-0001 | Keep the backend as a modular monolith and the React frontend as a separate application. | Accepted as a project constraint | [ADR-0001](adr/ADR-0001-modular-monolith.md) |
| ADR-0002 | Use Java 21, Spring Boot 3.5.16, and Maven for the local baseline, pending company standard confirmation. | Accepted locally; confirmation still open | [ADR-0002](adr/ADR-0002-java-spring-maven-baseline.md) |
| ADR-0003 | Use local credentials, one role per account, 15-minute access JWTs in frontend memory, and one-time environment-backed admin bootstrap. | Accepted for Block 2 | [ADR-0003](adr/ADR-0003-identity-and-session.md) |
| ADR-0004 | Introduce design patterns only to solve a named recurring problem; record the selection or the simpler alternative. | Proposed engineering rule for review | [ADR-0004](adr/ADR-0004-pattern-adoption.md) |

Business workflow definitions, record ownership, manager scope, SQL qualification, status transitions, audit/retention, reporting, and deployment controls remain open in [Open Questions](OPEN-QUESTIONS.md). Those decisions can change future module boundaries; do not encode guesses as framework abstractions.

## Review gate before Block 3

The review and recommendations are documented. Block 3 remains unstarted. The tech lead should review this document, [Design Principles](DESIGN-PRINCIPLES.md), [Design Patterns](DESIGN-PATTERNS.md), and [Code Quality Standards](CODE-QUALITY-STANDARDS.md), then approve or adjust them before business-module implementation begins.
