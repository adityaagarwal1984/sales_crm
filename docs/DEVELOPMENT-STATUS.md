# Development Status

**Last updated:** 2026-11-18

## Current phase

Block 1 UI foundation is implemented. Block 2 authentication and administrator access management are implemented as an MVP; business workflow modules remain future work.

## Completed

- Inspected the workspace; it contained no repository or project files.
- Created the requested `docs/`, `frontend/`, and `backend/` structure and root project entry point.
- Recorded supplied role capabilities and technology stack as draft requirements.
- Added provisional architecture, conceptual ER diagram, role-permission matrix, API outline, workflow outlines, edge-case list, test strategy, and deployment outline.
- Captured unresolved business/technical decisions and a proposed sequential roadmap.
- Created the React/Vite frontend with Tailwind CSS 4, shadcn-style source-owned UI components, React Router, Lucide, Recharts, and Axios dependencies.
- Added the shared responsive CRM shell, role-specific navigation, and illustrative placeholder dashboards for all five roles.
- Added shared CRM components, preview-only dashboard search/filter/pagination, design-system documentation, and route navigation tests.
- Installed Node.js 24 LTS for local frontend development.
- Added a Java 21 / Spring Boot 3.5.16 / Maven backend build foundation and environment-based PostgreSQL configuration.
- Implemented PostgreSQL/Liquibase roles and users, BCrypt login, 15-minute JWTs, active-user checks per request, first-admin bootstrap, forced password changes, admin user CRUD/status/reset endpoints, and React auth routes/admin screens.
- Connected frontend authentication to the API; removed the demo role switcher and gated role-specific dashboards by signed-in role.

## Open / blocked on decisions

- Core workflow and SQL qualification definition.
- Role/ownership/manager scope and record visibility.
- MVP boundary and acceptance criteria for business features.
- Production database/service configuration and access-control review.
- Data model, status transitions, reporting definitions, and deployment constraints.
- Prosperr brand guidance and approved dashboard metrics/currency.

See [Open Questions](OPEN-QUESTIONS.md).

## Next block

Block 2 implementation is in progress. The integration suite currently runs against H2 in PostgreSQL compatibility mode. A live PostgreSQL startup check remains before calling the block done.

## Verification

- `npm run build` — passed.
- `npm test` — passed (3 auth-route tests covering unauthenticated access, login form, and forced password change).
- `npm audit --omit=dev` — passed with no production dependency vulnerabilities.
- Full `npm audit` reports five high-severity findings in the dev-toolchain dependency chain through `picomatch`. npm offered only a force fix that downgrades Vite across a major version; that change was not applied. Recheck once upstream packages provide a compatible fix.
- `mvn -Dmaven.repo.local=/Users/adityaagarwal/.local/m2 test` — passed; one Spring integration test covers valid/invalid login, role permissions, account deactivation, duplicate normalized email, password-hash exclusion, and JWT expiration using H2 in PostgreSQL compatibility mode.
