# Testing Strategy

**Status: Planned; test scope follows confirmed workflows and acceptance criteria.** No tests or application code have been created in this documentation bootstrap.

## Backend

- JUnit and Mockito unit tests for business rules in services, including state-transition and authorization decisions.
- Spring Boot tests for controllers, validation, security configuration, exception mapping, and persistence integration.
- PostgreSQL-compatible integration testing for Liquibase migrations, constraints, and repository queries.
- Authorization tests for each role and record ownership/team scope; include denied-access cases.
- API contract checks against generated OpenAPI documentation.

## Frontend

- Component tests for form validation, loading/empty/error states, and role-aware navigation.
- API integration tests for success, validation failure, expired session, and forbidden responses.
- End-to-end tests for the agreed critical cross-role workflows once UI and backend blocks exist.
- Accessibility checks for keyboard flows, focus, labels, and error announcements.

## Quality gates

- Each feature block has acceptance criteria and automated tests before being considered complete.
- Migrations are exercised from an empty database and, where relevant, from the prior schema version.
- Do not proceed to another major block while critical defects remain unresolved.
- CI, coverage thresholds, browser matrix, and performance targets are not selected yet; agree them with the tech lead.
