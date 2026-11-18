# Deployment Guide

**Status: Outline only.** Hosting and environment requirements are not in the supplied brief; no deployment procedure or credentials are present.

## Environments to define

- Local development: frontend, backend, and PostgreSQL with non-production data.
- Shared development / staging: access controls, test data policy, and migration approval.
- Production: hosting, TLS, secrets, backups, monitoring, restore objectives, and release approval.

## Proposed release principles

- Build frontend and backend as separately deployable components, with PostgreSQL as durable storage.
- Apply Liquibase migrations through a controlled release step; define forward/rollback policy before production.
- Keep credentials and signing keys in environment-specific secret storage, never source control.
- Restrict CORS and Swagger UI access according to environment.
- Document health checks, logs, alerting, backup/restore, and incident ownership before production.

## Decisions required

Hosting provider, deployment method, domain/origins, TLS, secret manager, database managed/self-hosted, migration ownership, backup retention, recovery objectives, observability, scaling, and who can approve production changes.
