# ADR-0011: Leadership Analytics Read Model

- **Status:** Accepted and implemented
- **Date:** 2026-11-19
- **Category:** Read model / reporting

## Context

Block 8 needs organization-wide executive, BDR, manager, AE, funnel, and revenue reporting with filters. Joining one-to-many data (such as organisations and contacts) in a single broad query risks inflated counts.

## Decision

Keep analytics read-only and compute purpose-specific database aggregates in `LeadershipAnalyticsService`. Use separate aggregate queries for distinct data domains, then assemble response DTOs. Apply the selected date/period, department, employee, and stage filters. KPI definitions:

- Revenue: sum of final deal value for WON closures, using each opportunity's current final Won value.
- SQL conversion: approved qualifications divided by submitted qualifications.
- Assignment time: average duration from qualification approval to first assignment.
- Organisation and contact totals are counted independently, avoiding duplicate organisations from contact joins.

Leadership APIs are restricted to `LEADERSHIP`. Revival performance remains unavailable until Block 7 defines a customer lifecycle and data model.

## Rationale and alternatives

- **Selected:** Named SQL aggregates are direct, bounded, and testable for the current dashboard.
- **Alternatives:** Fetching full JPA graphs then grouping in memory risks unbounded reads; one multi-join query causes fan-out; materialized views or domain-event projections add operational maintenance without a measured latency need.

## Implementation

`analytics/service/LeadershipAnalyticsService.java`, `analytics/api/LeadershipAnalyticsController.java`, analytics DTOs in `sales/api/SalesDtos.java`, role authorization in `SecurityConfiguration`, and `frontend/src/pages/leadership/LeadershipDashboardPage.jsx`.

## Consequences

Metrics are backed by current database records and filters are applied server-side. Query definitions must evolve with schema and volume. Official currency and target management remain outside this decision.

## Verification

`Block8LeadershipAnalyticsIntegrationTest` verifies overview and role analytics, funnel, monthly/source revenue, filters, and denial for other roles using an H2 PostgreSQL-compatibility database.
