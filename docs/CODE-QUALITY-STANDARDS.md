# Code quality standards

Use these for new work and files being changed. Avoid broad rewrites of stable code just to conform.

## Backend

- Organize by business capability, then layer (`api`, `service`, `repository`, `domain`). Keep controllers thin.
- Use constructor injection, cohesive use-case services, explicit write transactions, and repositories for persistence.
- Define validated request/response DTOs; never serialize JPA entities or expose password hashes/tokens.
- Enforce roles and record ownership on the server. Make public routes explicit; keep secrets in environment/configuration.
- Use Liquibase for forward-only schema changes. Add appropriate foreign keys, constraints, indexes, and pagination.
- Use DB constraints as the final guard for concurrent duplicate writes; map expected conflicts to safe client errors.
- Keep errors actionable but do not log credentials, tokens, or unnecessary personal data.

## Frontend

- Keep routing, auth state, shared API client, feature pages, and reusable UI components in their existing boundaries.
- Use the shared Axios client; route guards improve navigation but are not authorization.
- Provide semantic labels, keyboard-accessible controls, visible focus, and explicit loading/error/empty states.
- Use real APIs for CRM records; mark any preview/example content as illustrative.
- Keep workflow policy on the server. Share visual components and tokens where they provide consistent behavior.

## API and data

Before implementation, agree route, DTO, role/scope, validation, error semantics, and data history behavior. Normalize input at a clear boundary; encode durable integrity rules in PostgreSQL. Paginate collection endpoints and avoid returning unbounded records.

## Verification expectations

For behavior changes, add focused unit/integration/UI tests, run the relevant existing suites, and exercise database-specific migrations/queries on PostgreSQL. H2 PostgreSQL compatibility is useful but not equivalent to PostgreSQL. Report what was actually run and any limitation.

## Future-block checklist

1. Confirm requirements and ambiguity; identify principles and whether a pattern is justified.
2. Specify API, authorization, persistence, validation, edge cases, and errors.
3. Implement with migration/tests and update the status, ADR, and pattern log when relevant.
4. Review trade-offs and report observed verification.
