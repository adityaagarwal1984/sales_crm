# Architecture decisions

This index points to the detailed, dated decision records in [`adr/`](adr/). A newer ADR supersedes an earlier decision when noted; implementation and latest agreed product rules take precedence over old proposals.

| ADR | Decision | Status |
|---|---|---|
| [0001](adr/ADR-0001-modular-monolith.md) | Separate React app; Spring Boot modular monolith | Accepted constraint |
| [0002](adr/ADR-0002-java-spring-maven-baseline.md) | Java/Spring/Maven local baseline | Local baseline; confirm company standard |
| [0003](adr/ADR-0003-identity-and-session.md) | One role per user, environment bootstrap, short-lived JWT, forced password change | Implemented for current scope |
| [0004](adr/ADR-0004-pattern-adoption.md) | Adopt patterns only for real recurring problems | Engineering guideline |
| [0005](adr/ADR-0005-organisation-contact-boundary.md) | Shared organisation/contact directory; creator traceability | Implemented; product assumptions need confirmation |
| [0006](adr/ADR-0006-bdr-prospecting-workflow.md) | BDR-owned leads, append-only calls, follow-ups | Implemented; initial SQL submission model superseded |
| [0007](adr/ADR-0007-sql-qualification-review.md) | Separate qualification lifecycle and review history | Implemented |
| [0008](adr/ADR-0008-sales-manager-team-and-assignment.md) | Initial team-scoped manager assignment proposal | Superseded by ADR-0009 |
| [0009](adr/ADR-0009-sales-manager-organization-wide-access.md) | All Sales Managers can access BDR/AE work | Implemented |
| [0010](adr/ADR-0010-ae-opportunity-lifecycle.md) | Controlled opportunity stages, closure history, manager-approved reopen | Implemented |
| [0011](adr/ADR-0011-leadership-analytics.md) | Leadership SQL aggregates and KPI definitions | Implemented; currency/targets provisional |

## Current architecture

React SPA → versioned JSON API → Spring Security/controllers → capability services → JPA repositories and explicit analytics SQL → PostgreSQL. Liquibase owns schema changes. Modules are identity, organisation, prospecting, sales, and analytics. The Interview Guide explains why this architecture fits and its limits.

## Known improvement candidates

- Deny unmatched routes by default and review JWT issuer/audience policy before deployment.
- Move admin dashboard aggregation out of the controller and paginate admin user lists as volume grows.
- Confirm local technology versions, official currency, SQL rubric, customer lifecycle, and production operations requirements.

These are review items, not claims that production hardening is complete.
