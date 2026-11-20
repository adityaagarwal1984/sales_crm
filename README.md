# Prosperr Sales CRM

A role-based sales CRM that connects organisation discovery, BDR outreach, SQL review, AE opportunity management, and leadership analytics.

## At a glance

- **Frontend:** React 19, Vite, React Router, Tailwind CSS, Axios, Recharts, Lucide.
- **Backend:** Java 21, Spring Boot 3.5, Spring Security, JPA, Jakarta Validation, OpenAPI.
- **Database:** PostgreSQL with Liquibase-managed schema changes.
- **Architecture:** Separate React SPA + one Spring Boot modular monolith. The backend is authoritative for roles, ownership, workflow rules, and persistence.
- **Roles:** ADMIN, BDR, SALES_MANAGER, AE, LEADERSHIP.
- **Status:** Blocks 0–6 and 8 implemented; Block 7 revival and target management remain unimplemented.

## Start here

- [Interview and technical guide](docs/INTERVIEW-GUIDE.md) — architecture, API, schema, PostgreSQL rationale, patterns, algorithms, trade-offs, and interview answers.
- [End-to-end user flow](docs/USER-FLOW.md) — visual role-by-role handoffs and workflow.
- [API reference](docs/api-documentation.md) · [Database notes](docs/database-design.md) · [ER diagram](docs/er-diagram.md)
- [Design principles](docs/DESIGN-PRINCIPLES.md) · [Design patterns](docs/DESIGN-PATTERNS.md) · [ADR index](docs/ARCHITECTURE-DECISIONS.md)
- [Current status](docs/DEVELOPMENT-STATUS.md) · [Open questions](docs/OPEN-QUESTIONS.md) · [All docs](docs/README.md)

## Run locally

The public overview is `/`; employee sign-in is `/login` and uses the existing backend login API.

1. Follow [backend setup](backend/README.md) to configure PostgreSQL and start Spring Boot.
2. In `frontend/`, run `npm install` once, then `npm run dev`.
3. Verify changes with `npm run build` and `npm test` in `frontend/`; run `mvn -Dmaven.repo.local=/Users/adityaagarwal/.local/m2 test` in `backend/`.

Never commit `.env` files, credentials, or signing secrets. Confirm Prosperr’s production versions, currency, identity, and deployment standards before a production rollout.
