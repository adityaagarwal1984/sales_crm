# Database Notes

PostgreSQL is the durable source of truth; Liquibase owns the schema. Table groups and rationale are summarized in the [Interview Guide](INTERVIEW-GUIDE.md), and the relationships are in the [ER Diagram](er-diagram.md).

## Core tables

| Domain | Tables |
|---|---|
| Identity | `app_user`, `app_role` |
| Shared directory | `organisation`, `contact` |
| BDR prospecting | `sales_lead`, `call_activity`, `follow_up` |
| Qualification | `sql_qualification`, `sql_qualification_history`; legacy `sql_submission` retained |
| Assignment | `sales_opportunity`, `opportunity_assignment_history`, `app_notification` |
| AE lifecycle | `opportunity_stage_history`, `opportunity_meeting`, `opportunity_meeting_participant`, `opportunity_stakeholder`, `opportunity_closure`, `implementation_handover`, `opportunity_reopen_request` |

## Important constraints

- `app_user.email`: unique after lowercasing.
- `organisation.name`: unique after trim + case-fold.
- `contact`: nonblank email unique per organisation after trim + case-fold; email may be omitted.
- `sql_qualification.lead_id`: unique; one qualification per sales pursuit.
- `sales_opportunity.sql_qualification_id`: unique; one opportunity per qualification.
- `opportunity_stakeholder`: one stakeholder row per contact per opportunity (a contact cannot be listed twice under two roles).
- Foreign keys connect owners, creators, org/contact/lead/qualification/opportunity, and history actors.
- `@Version` protects edits to shared organisations, contacts, leads, qualifications, and opportunities from silent lost updates.

The normalized unique indexes are PostgreSQL expression/partial indexes. Keep application duplicate checks for friendly errors, but rely on the database constraint for concurrent correctness.

## Migrations

| Changeset | Purpose |
|---|---|
| 001 | Roles and users |
| 002 | Organisations and contacts |
| 003 | Leads, calls, and follow-ups |
| 004 | SQL qualification and review history |
| 005 | Manager links, opportunities, assignment history, meetings, notifications |
| 006 | L1/L2/L3 lifecycle, stakeholders, close/reopen history, and handovers |

Do not edit an already-applied changeset to change production schema. Add a new forward changeset and test it against an empty database and the previous schema.

## Deliberate boundaries

- A `contact` is a person; a `sales_lead` is a BDR-owned pursuit; `sql_qualification` is review data; `sales_opportunity` is downstream AE work.
- Calls, qualification reviews, assignment changes, stage changes, and closures are separate history/activity rows.
- Customer expiry/revival and target tables are not implemented. Currency policy, retention, and lifecycle/archive rules remain open.
