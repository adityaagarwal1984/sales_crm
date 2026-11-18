# ADR-0001: Use a Modular Monolith

- **Status:** Proposed from the supplied instruction; awaiting tech-lead confirmation.
- **Date:** 2026-11-18

## Context

The project brief explicitly requests a modular monolith, layered backend, and separate React frontend, Spring Boot backend, and PostgreSQL persistence.

## Decision

Propose one Spring Boot backend deployment organized into business modules, with a separate React/Vite frontend and PostgreSQL database. Keep business logic in services, persistence behind repositories, and transport DTOs separate from entities.

## Consequences

- One backend deployment reduces operational complexity for an internship-sized initial product.
- Modules still need clear ownership and contracts to avoid a monolith becoming tangled.
- Frontend and backend can be developed and deployed separately while communicating through JSON APIs.
- Final module boundaries, deployment, and auth details remain open until business workflows are confirmed.
