# Open Questions

These questions are intentionally explicit: the supplied brief calls out purposeful ambiguity. Resolve blocking items with the tech lead/manager before finalizing architecture, schema, API contracts, or implementation scope. Record answers and decision dates here.

## Blocking business questions

1. **MVP / first workflow:** Which end-to-end workflow must the internship project deliver first, and what is explicitly out of scope for the MVP?
2. **SQL definition:** What does “SQL” mean at Prosperr, what detailed acceptance criteria apply, and what currency is used for estimated deal value? The qualification fields, DRAFT/SUBMITTED/APPROVED/REJECTED states, manager rejection reason, and resubmission path are implemented from the Block 4 brief.
3. **Organisation and customer lifecycle:** Are a prospect organisation, customer, account, and opportunity distinct concepts? When does an organisation become a customer, and how is “inactive” defined?
4. **Opportunity lifecycle:** Answered for Block 6 implementation: L1 → L2 → L3 → WON/LOST; skip forward only with reason; L1/L2 meeting type is distinct from stage and matching completed meetings advance that stage.
5. **Won/Lost and handover:** Answered for Block 6: WON needs final deal value/commercial details, LOST needs reason/notes, AE reopens only after manager approval, approved reopen resumes at L3. Handover acceptance/checklist remains undefined.
6. **Roles and access scope:** Can a user have multiple roles? What records can each role read/create/update/delete? Is leadership read-only? Sales Manager access to all BDR and AE accounts is confirmed.
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
20. What additional dashboard metrics, targets, and attribution rules are needed beyond the implemented Leadership formulas?

## Block 2 decisions

- First admin: one-time environment-backed bootstrap, with no public signup.
- Roles: exactly one role per account.
- Password reset: admin sets temporary password; user changes it at next login.
- Token: 15-minute access JWT returned in response, held in memory, no refresh token. User active state is checked on every request.

## Answers / decision log

- **Block 3 provisional defaults (2026-11-19; confirm with tech lead):** BDR and ADMIN share access to organisation/contact records; `createdBy` is traceability only; industry/company-size/lead-source remain free text; organisation name is duplicate-checked after trim and case-folding; contact email is optional and duplicate-checked within one organisation only. These assumptions are recorded in [ADR-0005](adr/ADR-0005-organisation-contact-boundary.md).
- **Block 4 implementation choices (2026-11-19):** Completing a follow-up logs a new call so call history remains append-only. BDR-owned leads, calls, follow-ups, and drafts remain private; Sales Managers see submitted qualifications and reviewed records. Every qualification review event stores reviewer/action/time and a snapshot. See [ADR-0006](adr/ADR-0006-bdr-prospecting-workflow.md) and [ADR-0007](adr/ADR-0007-sql-qualification-review.md).
- **Block 5 implementation defaults (2026-11-19; stage definition superseded by Block 6):** Only approved qualifications can create opportunities; every Sales Manager can review BDR submissions and assign to any active AE account, regardless of optional manager links. Assignment/reassignment appends history and notifies the new AE. The original interim status model was `ACTIVE`, `WON`, and `LOST`; Block 6 replaced it with L1/L2/L3/WON/LOST. The manager dashboard's won/assigned conversion and INR assumption remain provisional; expired-customer counts remain unconfigured. See [ADR-0008](adr/ADR-0008-sales-manager-team-and-assignment.md) and superseding [ADR-0009](adr/ADR-0009-sales-manager-organization-wide-access.md).
- **Blocks 6 and 8 confirmed defaults (2026-11-19):** Opportunity stages are L1/L2/L3/WON/LOST; forward skip requires reason and reopening requires Sales Manager approval. A matching completed L1/L2 meeting advances the stage. Leadership formulas: revenue uses final Won deal value; SQL conversion is approved ÷ submitted; assignment time is approval to first assignment. See [ADR-0010](adr/ADR-0010-ae-opportunity-lifecycle.md) and [ADR-0011](adr/ADR-0011-leadership-analytics.md). Official currency and Block 7 revival data remain open.

The user supplied Block 2 requirements on 2026-11-18: local email/password login, JWT authentication, the five named roles, admin user management, inactive-account login rejection, and password-hash exclusion from API responses. The user confirmed the recommended defaults on 2026-11-18: 15-minute access JWT returned in the response and held in memory, no refresh token; one role per account; first admin provisioned from environment variables; administrator-set temporary password with mandatory change at first login.
