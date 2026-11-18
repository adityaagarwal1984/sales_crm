# Prosperr Sales CRM

An in-house sales CRM for Prosperr. Blocks 0–4 are implemented: project documentation, React UI foundation, Spring Boot identity/authentication, administrator user management, organisation/contact management, and BDR prospecting with SQL qualification review.

## Repository layout

- [`docs/`](docs/README.md) — business, architecture, quality standards, APIs, and delivery documentation.
- [`frontend/`](frontend/README.md) — React, Vite, and JavaScript application.
- [`backend/`](backend/README.md) — Spring Boot modular monolith and local PostgreSQL setup.

## Current status

See [Development Status](docs/DEVELOPMENT-STATUS.md), [Open Questions](docs/OPEN-QUESTIONS.md), and the [Development Plan](docs/development-plan.md). Block 2.5 documented architecture and engineering-principles guidance applied in Block 3.

## Stack

React, Vite, JavaScript, Tailwind CSS, shadcn/ui, React Router, Axios, Recharts, Lucide React; Java 21, Spring Boot 3.5.16, Spring Security, Spring Data JPA, Hibernate, JWT, BCrypt, Jakarta Validation; PostgreSQL, Liquibase, OpenAPI; JUnit, Mockito, and Spring Boot Test.

Local versions are recorded in [System Architecture](docs/system-architecture.md); confirm company standards before deployment.

## Local frontend development

Use Node.js 24 LTS. From `frontend/`, run `npm run dev`; run `npm run build` and `npm test` before reviewing changes. Run the backend and PostgreSQL using [`backend/README.md`](backend/README.md). Later sales workflows remain illustrative placeholders. Do not add credentials to the repository.

## Documentation

Start at [`docs/README.md`](docs/README.md). The Block 2.5 deliverables are:

- [Design Principles](docs/DESIGN-PRINCIPLES.md)
- [Design Patterns](docs/DESIGN-PATTERNS.md)
- [Architecture Decisions and Review](docs/ARCHITECTURE-DECISIONS.md)
- [Code Quality Standards](docs/CODE-QUALITY-STANDARDS.md)
- [Pattern Implementation Log](docs/PATTERN-IMPLEMENTATION-LOG.md)

Business rules that remain unresolved are tracked in [`docs/OPEN-QUESTIONS.md`](docs/OPEN-QUESTIONS.md).
