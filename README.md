# Prosperr Sales CRM

An in-house sales CRM for Prosperr. This repository is in the **discovery and documentation phase**. Business rules and the initial architecture are recorded as drafts; implementation has not started.

## Repository layout

- [`docs/`](docs/README.md) — business, product, architecture, API, and delivery documentation.
- [`frontend/`](frontend/README.md) — planned React, Vite, and JavaScript application.
- [`backend/`](backend/README.md) — planned Spring Boot modular monolith.

## Current status

See [Development Status](docs/DEVELOPMENT-STATUS.md), [Open Questions](docs/OPEN-QUESTIONS.md), and the [Development Plan](docs/development-plan.md). The next step is to resolve the blocking business questions and get approval of the proposed first implementation block.

## Proposed stack

React, Vite, JavaScript, Tailwind CSS, shadcn/ui, React Router, Axios, Recharts, Lucide React; Java, Spring Boot, Spring Security, Spring Data JPA, Hibernate, JWT, BCrypt, Jakarta Validation; PostgreSQL, Liquibase, OpenAPI; JUnit, Mockito, and Spring Boot Test.

Versions and deployment choices are not fixed until the project constraints are confirmed. See [System Architecture](docs/system-architecture.md).

## Local development

Setup instructions will be added after the repository's Java, Node.js, package manager, and PostgreSQL versions are agreed. Do not add credentials to the repository; use environment variables or a local ignored environment file once configuration is defined.

## Documentation

Start at [`docs/README.md`](docs/README.md). Business rules that remain unresolved are tracked in [`docs/OPEN-QUESTIONS.md`](docs/OPEN-QUESTIONS.md).
