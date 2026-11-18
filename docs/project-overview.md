# Project Overview

**Status: Draft — based only on the supplied master instruction.**

## Purpose

Build an in-house Sales CRM for Prosperr to support organisation discovery, contact work, qualification, sales meetings, opportunity outcomes, implementation handover, management review, and leadership reporting.

## Users

The brief names five roles: `ADMIN`, `BDR`, `SALES_MANAGER`, `AE`, and `LEADERSHIP`. The meaning of BDR and AE, the identity lifecycle, and the exact access rules need confirmation.

## Product goals

- Give each role useful, role-specific navigation and dashboard views within one coherent application.
- Track sales work and its handoffs across the roles described in the brief.
- Persist business records in PostgreSQL across application restarts.
- Enforce authenticated, role-based, and record-ownership-aware access on the server.
- Provide searchable/filterable records and organization-wide analytics where authorized.

## Initial scope from the brief

- User and role management, targets, and system configuration.
- Organisation discovery, contact mining, calling, follow-ups, and SQL submissions.
- SQL review, AE assignment, team performance, inactive customers, and revival management.
- Assigned opportunities, L1/L2/L3 meetings, stakeholders, Won/Lost outcomes, and implementation handover.
- Organization-wide performance, revenue, department analytics, and sales funnel.

## Out of scope until confirmed

External integrations, email/calendar sync, telephony, data enrichment providers, billing, implementation-project execution, mobile apps, and multi-tenant/customer-facing access are not established by the brief. Do not build them without confirmation.

## Success measures

The brief does not define measurable success criteria. Candidate measures to confirm include adoption by each role, completeness of handoffs, time to process submissions, data freshness, and accuracy of pipeline/revenue reporting. No numerical targets are assumed.

## Constraints

The user is learning Spring Boot while building this as an internship project. Prefer understandable, documented decisions and a modular monolith. Business rules remain subject to the tech lead / manager's direction.
