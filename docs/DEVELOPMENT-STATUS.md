# Development status

**Updated:** 2026-11-19

## Scope

- Implemented: Blocks 0–6 and 8, including React foundation, identity/admin, shared organisation/contact directory, BDR prospecting and SQL qualification, Sales Manager review/assignment, AE opportunity lifecycle, and Leadership analytics.
- Not implemented: Block 7 customer revival and target management.
- Architecture: separate React SPA and Spring Boot modular monolith; PostgreSQL schema managed by Liquibase.

## Important current rules

- Organisations/contacts are shared directory records; BDR leads are owned pursuits.
- SQLs are separately qualified/reviewed; only approved SQLs can become AE opportunities.
- Sales Managers can access all BDR and AE accounts; AEs can manage only assigned opportunities.
- Opportunity progression is L1 → L2 → L3 → Won/Lost; skip requires a reason, reopening requires manager approval.
- Leadership dashboards use database aggregates. Revenue is final Won value; SQL conversion is approved ÷ submitted; assignment time is approval to first assignment.

## Local verification recorded in project history

The latest documented run reports backend Maven tests and frontend Vitest/build passing. Backend integration tests use H2 in PostgreSQL compatibility mode; this does not replace migration/query verification on local PostgreSQL. The frontend build reports a bundle-size advisory. See the [Interview Guide](INTERVIEW-GUIDE.md) for testing boundaries and implementation explanation.

These are recorded project results, not a verification performed during this documentation update.

## Remaining decisions

Block 7 customer lifecycle/expiry, official currency, SQL acceptance criteria, KPI targets, deployment/security standards, and supported company versions remain open. See [Open Questions](OPEN-QUESTIONS.md).
