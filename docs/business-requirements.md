# Business Requirements

**Status: Draft.** This records the supplied brief without filling its business-rule gaps with assumptions.

## Functional requirements from the brief

| ID | Requirement |
|---|---|
| BR-01 | Support the roles ADMIN, BDR, SALES_MANAGER, AE, and LEADERSHIP. |
| BR-02 | ADMIN can manage users, roles, targets, and system configuration. |
| BR-03 | BDR can perform organisation discovery, contact mining, calling, follow-ups, and submit SQLs. |
| BR-04 | SALES_MANAGER can review SQLs, assign AEs, view team performance, and manage inactive customers and revival work. |
| BR-05 | AE can work assigned opportunities, record L1/L2/L3 meetings and stakeholders, mark Won/Lost, and hand over implementation. |
| BR-06 | LEADERSHIP can view organization-wide performance, revenue, department analytics, and the sales funnel. |
| BR-07 | Role-specific dashboards and navigation share one consistent design system. |
| BR-08 | Business records persist in PostgreSQL; schema changes use Liquibase. |
| BR-09 | APIs use DTOs, validation, centralized errors, transactions, pagination/filtering where needed, and server-side authorization. |
| BR-10 | Provide OpenAPI documentation and automated backend tests. |
| BR-11 | Authenticate users with email and password; issue JWT authentication after successful login. |
| BR-12 | Route a signed-in user to a dashboard matching the role assigned to that account. |
| BR-13 | ADMIN can create, view, edit, assign roles, activate/deactivate, and reset passwords for users. |
| BR-14 | Support the fixed role codes ADMIN, BDR, SALES_MANAGER, AE, and LEADERSHIP. |
| BR-15 | Deactivated accounts cannot log in. |
| BR-16 | Never return password material or password hashes through an API. |
| BR-17 | Protect every role-specific endpoint in Spring Boot; frontend visibility is not authorization. |
| BR-18 | User email is unique; the persistence layer records account status. |
| BR-19 | Public admin signup is disabled; the first admin is provisioned from environment configuration, then admins can create accounts. |

## Quality requirements from the brief

- Professional, responsive interface with reusable components, loading/empty/error states, validated forms, and clear status indicators.
- Layered backend: controller → service → repository → database; business logic does not belong in controllers.
- Modular monolith; do not split into microservices.
- No direct entity exposure through APIs and no static arrays/browser storage as the primary record store.
- Keep secrets out of source control.

## Acceptance criteria to define

The brief does not include user stories, field-level validation, measurable performance/accessibility targets, or acceptance examples. Each workflow should have examples and explicit acceptance criteria before its implementation block.

## Terminology requiring confirmation

- **SQL** is retained as written. It may refer to a qualified lead / sales-qualified lead, but the exact expansion, criteria, and lifecycle must come from Prosperr.
- **Organisation**, **customer**, **opportunity**, **inactive customer**, **revival**, **implementation handover**, and **department** require business definitions.
- **BDR**, **AE**, and **L1/L2/L3** should be expanded/defined for documentation and onboarding.

## Block 2 implementation boundary

Defaults confirmed for Block 2: 15-minute access JWT returned in the response and held in React memory, with no refresh token; one role per account; first admin from environment configuration; administrator-set temporary password with required change on first login. Business workflow decisions remain open.
