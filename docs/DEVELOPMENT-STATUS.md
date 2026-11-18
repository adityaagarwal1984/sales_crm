# Development Status

**Last updated:** 2026-11-19

## Current phase

Blocks 0–4 are implemented. Block 2.5 architecture review and engineering guidelines are documented. Block 4 now includes BDR-owned leads, append-only calls and follow-ups, plus a draft → manager review SQL qualification workflow.

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
- Implemented Block 4 lead and calling workflows with BDR ownership, dashboard metrics, immutable call history, and follow-up scheduling/rescheduling/completion. The initial minimal SQL submission endpoint was superseded by the qualification flow below.
- Connected BDR dashboard, leads, lead call history, calls, follow-ups, and SQL qualification screens to the backend. BDR navigation now opens these live screens.
- Made organisations and contacts readable across all CRM roles while keeping create/edit access with BDR and ADMIN. Sales Managers receive submitted qualifications with linked lead and primary-contact context; assignment to an AE remains the next workflow block.
- Implemented SQL qualification drafts, submission, manager approval/rejection, rejection feedback, resubmission, and append-only qualification history. Approved records are marked eligible for AE assignment; this module does not assign an AE.
- Added Liquibase changeset 003 for BDR prospecting tables and contact creator attribution; updated API and ER documentation plus ADR-0006.
- Added organisation and contact listing/detail pages, create/edit forms, organisation profile contact section, search, and pagination; records load from the backend API.
- Updated the ER diagram, API documentation, implementation log, architecture decision record, and current development status. Recorded shared-access and duplicate rules as provisional assumptions for tech-lead confirmation.

## Open / blocked on decisions

- Detailed SQL acceptance criteria and any currency convention for estimated deal value.
- Role/ownership/manager scope and record visibility.
- MVP boundary and acceptance criteria for business features.
- Production database/service configuration and access-control review.
- Data model, status transitions, reporting definitions, and deployment constraints.
- Prosperr brand guidance and approved dashboard metrics/currency.

See [Open Questions](OPEN-QUESTIONS.md).

## Current runtime verification

Block 2 implementation is running locally against the installed PostgreSQL 18.7 server. The first-run script created the `prosperr_crm` database schema and initial administrator; a live login and Admin dashboard API call both returned successfully. The automated integration suite also runs against H2 in PostgreSQL compatibility mode.

## Next action

Implement the next workflow block for Sales Manager → AE assignment after clarifying assignment scope and rules. Confirm the Interested Prospects metric rule (latest call outcome) and SQL acceptance criteria with the tech lead.

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
- Block 4 — `npm run build` passed (Vite reports a bundle-size advisory); backend `mvn test` passed, 3 tests total including a prospecting integration flow for lead creation, interested call, follow-up completion with a new call, SQL submission, dashboard metrics, and role rejection. The tests use H2 in PostgreSQL compatibility mode; the local PostgreSQL migration has not been verified in this turn.
- Block 4 SQL qualification — backend `mvn test` passed (3 integration tests total, including draft/duplicate prevention, submit, manager review and rejection, BDR resubmission, approval audit, eligibility flag, and role checks); frontend `npm test` passed (5 tests); `npm run build` passed with the existing bundle-size advisory. Liquibase changeset 004 was exercised in H2 PostgreSQL compatibility mode; apply it to local PostgreSQL by restarting the backend.
