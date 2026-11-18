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

10. **Authentication:** Is login local, company SSO, or another identity provider? How are accounts provisioned and disabled? Is JWT mandated by deployment/security policy, and what token lifecycle/transport is approved?
11. **Data requirements:** Required fields, unique identifiers, duplicate rules, retention/deletion, audit trail, privacy/consent, and import/export needs?
12. **Integrations:** Must email, calendar, calling/telephony, lead sources, or implementation systems be integrated in the MVP?
13. **Deployment constraints:** Approved hosting, environments, database hosting/version, domain/origin, secret management, backups, and release process?
14. **Supported versions/tools:** Required Java/Spring Boot/PostgreSQL versions and build/CI standards from Prosperr? Node.js 24 LTS was selected for this local frontend block because it is an LTS line compatible with Vite; confirm whether Prosperr requires a different Node version or package manager.
15. **Nonfunctional requirements:** Expected users/data volume, response times, availability, accessibility standard, supported browsers/devices, and recovery objectives?

## Product and UX questions

16. What are the accepted business terms and expansions for BDR, AE, SQL, L1/L2/L3?
17. What brand assets, colors, accessibility expectations, responsive breakpoints, and design references should be used?
18. What timezone/currency/locale rules apply? Can records contain amounts in multiple currencies?
19. What notifications are expected for follow-ups, review decisions, assignment, overdue work, and handover?
20. What dashboard metrics and visualizations are decision-useful for each role, and how should each metric be calculated?

## Answers / decision log

No answers have been confirmed yet. Add date, decision owner, decision, and impacted documents below as answers are received.
