# Prosperr Sales CRM

An in-house sales CRM for Prosperr. The documentation-first discovery phase is complete enough to begin UI work. The **Block 1 frontend foundation** is implemented with illustrative dashboards; business workflows and backend behavior are still unimplemented.

## Repository layout

- [`docs/`](docs/README.md) — business, product, architecture, API, and delivery documentation.
- [`frontend/`](frontend/README.md) — planned React, Vite, and JavaScript application.
- [`backend/`](backend/README.md) — planned Spring Boot modular monolith.

## Current status

See [Development Status](docs/DEVELOPMENT-STATUS.md), [Open Questions](docs/OPEN-QUESTIONS.md), and the [Development Plan](docs/development-plan.md). The next step is to resolve the blocking business questions and get approval of the proposed first implementation block.

## Proposed stack

React, Vite, JavaScript, Tailwind CSS, shadcn/ui, React Router, Axios, Recharts, Lucide React; Java, Spring Boot, Spring Security, Spring Data JPA, Hibernate, JWT, BCrypt, Jakarta Validation; PostgreSQL, Liquibase, OpenAPI; JUnit, Mockito, and Spring Boot Test.

Versions and deployment choices are not fixed until the project constraints are confirmed. See [System Architecture](docs/system-architecture.md).

## Local frontend development

Use Node.js 24 LTS. From [`frontend/`](frontend/README.md), run `npm install` and `npm run dev`. Run `npm run build` and `npm test` before reviewing changes. Dashboard values are static illustrative placeholders. Do not add credentials to the repository.

## Documentation

Start at [`docs/README.md`](docs/README.md). Business rules that remain unresolved are tracked in [`docs/OPEN-QUESTIONS.md`](docs/OPEN-QUESTIONS.md).
