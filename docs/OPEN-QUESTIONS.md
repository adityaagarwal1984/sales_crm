# Open Questions

These questions are intentionally explicit: the supplied brief calls out purposeful ambiguity. Resolve blocking items with the tech lead/manager before finalizing architecture, schema, API contracts, or implementation scope. Record answers and decision dates here.

## Blocking business questions

1. **MVP / first workflow:** Which end-to-end workflow must the internship project deliver first, and what is explicitly out of scope for the MVP?
2. **SQL definition:** What does “SQL” mean at Prosperr, what exact qualification criteria/fields are required, and what are the review outcomes and resubmission path?
3. **Organisation and customer lifecycle:** Are a prospect organisation, customer, account, and opportunity distinct concepts? When does an organisation become a customer, and how is “inactive” defined?
4. **Opportunity lifecycle:** What are the exact stages and allowed transitions? Are L1/L2/L3 meetings stages, meeting types, or required milestones? Can they be skipped or repeated?
5. **Won/Lost and handover:** Required close data/reasons? Can outcomes be reopened? What information is required for handover, and who accepts it?
6. **Roles and access scope:** Can a user have multiple roles? What records can each role read/create/update/delete? Does manager scope follow team, department, or whole organization? Is leadership read-only?
7. **Ownership/handoffs:** Who owns organisations, contacts, activities, submissions, and opportunities? Can records have multiple owners? How are reassignment and user deactivation handled?
8. **Revival management:** What inactivity threshold and eligibility rules trigger revival? Who owns it, and what states/outcomes should be recorded?
9. **Targets and metrics:** What target types, periods, units, attribution rules, and definitions drive team/department/revenue/funnel dashboards?

## Blocking architecture / data questions

10. **JWT session details:** Block 2 confirms local email/password login and JWTs. What transport and lifetime are approved? Should the frontend retain an access token in memory, or should the app use an HttpOnly cookie plus CSRF protection? Is there a refresh-token flow?
11. **Data requirements:** Required fields, unique identifiers, duplicate rules, retention/deletion, audit trail, privacy/consent, and import/export needs?
12. **Integrations:** Must email, calendar, calling/telephony, lead sources, or implementation systems be integrated in the MVP?
13. **Deployment constraints:** Approved hosting, environments, database hosting/version, domain/origin, secret management, backups, and release process?
14. **Supported versions/tools:** Does Prosperr require particular Java/Spring Boot/PostgreSQL versions or build/CI standards? The local baseline is Java 21, Spring Boot 3.5.16, Maven 3.10.1, and Node.js 24 LTS. Confirm any company standard before deployment or team handoff.
15. **Nonfunctional requirements:** Expected users/data volume, response times, availability, accessibility standard, supported browsers/devices, and recovery objectives?

## Product and UX questions

16. What are the accepted business terms and expansions for BDR, AE, SQL, L1/L2/L3?
17. What brand assets, colors, accessibility expectations, responsive breakpoints, and design references should be used?
18. What timezone/currency/locale rules apply? Can records contain amounts in multiple currencies?
19. What notifications are expected for follow-ups, review decisions, assignment, overdue work, and handover?
20. What dashboard metrics and visualizations are decision-useful for each role, and how should each metric be calculated?

## Block 2 decisions

- First admin: one-time environment-backed bootstrap, with no public signup.
- Roles: exactly one role per account.
- Password reset: admin sets temporary password; user changes it at next login.
- Token: 15-minute access JWT returned in response, held in memory, no refresh token. User active state is checked on every request.

## Answers / decision log

The user supplied Block 2 requirements on 2026-11-18: local email/password login, JWT authentication, the five named roles, admin user management, inactive-account login rejection, and password-hash exclusion from API responses. The user confirmed the recommended defaults on 2026-11-18: 15-minute access JWT returned in the response and held in memory, no refresh token; one role per account; first admin provisioned from environment variables; administrator-set temporary password with mandatory change at first login.
