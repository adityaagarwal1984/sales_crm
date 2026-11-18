# Development Status

**Last updated:** 2026-11-19

## Current phase

Blocks 0–3 are implemented. Block 2.5 architecture review and engineering guidelines are documented. Block 3 adds organisation and contact management.

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
- Implemented Block 3 organisation/contact entities, repositories, DTOs, services, REST APIs, Liquibase migration, duplicate constraints, paginated search/filtering, and BDR/ADMIN server authorization.
- Made the shared organisation/contact directory readable across all CRM roles while keeping create/edit access with BDR and ADMIN.
- Added organisation and contact listing/detail pages, create/edit forms, organisation profile contact section, search, and pagination; records load from the backend API.

## Open / blocked on decisions

- Confirm organisation/contact duplicate rules and controlled vocabularies with the tech lead.
- Ownership and manager scope for future sales workflow records.
- MVP boundary and acceptance criteria for business features.
- Production database/service configuration and access-control review.
- Data model, status transitions, reporting definitions, and deployment constraints.
- Prosperr brand guidance and approved dashboard metrics/currency.

See [Open Questions](OPEN-QUESTIONS.md).

## Current runtime verification

Block 2 implementation is running locally against the installed PostgreSQL 18.7 server. The first-run script created the `prosperr_crm` database schema and initial administrator; a live login and Admin dashboard API call both returned successfully. The automated integration suite also runs against H2 in PostgreSQL compatibility mode.

## Next action

Review Block 3 duplicate, taxonomy, and shared-directory defaults with the tech lead before extending the data model.

## Verification

- `npm run build` — passed.
- `npm test` — passed (4 route tests covering auth behavior and BDR organisation navigation/data state).
- `bash -n backend/scripts/first-run.sh` — passed.
- `npm audit --omit=dev` — passed with no production dependency vulnerabilities.
- Full `npm audit` reports five high-severity findings in the dev-toolchain dependency chain through `picomatch`. npm offered only a force fix that downgrades Vite across a major version; that change was not applied. Recheck once upstream packages provide a compatible fix.
- `mvn -Dmaven.repo.local=/Users/adityaagarwal/.local/m2 test` — passed; one Spring integration test covers valid/invalid login, role permissions, account deactivation, duplicate normalized email, password-hash exclusion, and JWT expiration using H2 in PostgreSQL compatibility mode.
- First run against local PostgreSQL 18.7 — Liquibase migration succeeded; initial admin login returned HTTP 200 with `mustChangePassword=true`; Admin dashboard summary returned one active account.
- Block 2.5 — inspected frontend/backend architecture, entities, auth/security, DTOs, reusable UI, tests, and existing ADRs; documented principles, pattern decisions, review findings, quality standards, and implementation history. Its principles guided Block 3.
- Block 3 — frontend `npm test` passed (4 route tests); `npm run build` passed. Backend `mvn -Dmaven.repo.local=/Users/adityaagarwal/.local/m2 test -q` passed: Block 2 integration (1) and Block 3 integration (1), H2 in PostgreSQL compatibility mode. The UI production build reports a main-bundle chunk-size advisory. Local PostgreSQL now records Liquibase changesets `001` and `002`; the organisation/contact tables and normalized indexes are present. A runtime PostgreSQL `lower(bytea)` error was traced to optional string query bindings; search filters now normalize inputs in Java before binding, with the backend suite passing and equivalent filter SQL verified against PostgreSQL.
