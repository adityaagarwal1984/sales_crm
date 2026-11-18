# ADR-0001: Use a Modular Monolith

- **Status:** Accepted as a project constraint (2026-11-19 review).
- **Date:** 2026-11-18

## Context

The project brief explicitly requests a modular monolith, layered backend, and separate React frontend, Spring Boot backend, and PostgreSQL persistence.

## Decision

Use one Spring Boot backend deployment organized into business capabilities, with a separate React/Vite frontend and PostgreSQL database. Keep business logic in services, persistence behind repositories, and transport DTOs separate from entities. Do not split into microservices without a new approved ADR and operational need.

## Consequences

- One backend deployment reduces operational complexity for an internship-sized initial product.
- Modules still need clear ownership and contracts to avoid a monolith becoming tangled.
- Frontend and backend can be developed and deployed separately while communicating through JSON APIs.
- Business capability boundaries still need to follow confirmed workflows. Blocks 0–2 implement identity as the current capability; no business workflow module is implemented yet.
