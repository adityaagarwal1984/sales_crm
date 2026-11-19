# Database Design

**Status:** Identity, organisation/contact, prospecting, SQL qualification, team membership, opportunity lifecycle, meetings, stakeholders, closures, handovers, notifications, and Leadership analytics are implemented with Liquibase/query aggregates. Revival and targets remain conceptual. See the [ER diagram](er-diagram.md).

## Candidate concepts

| Concept | Candidate purpose | Decisions needed |
|---|---|---|
| User | Login identity, active state, role, optional Sales Manager team link | Identity provider, one/multiple roles, provisioning, deactivation, larger department/team model |
| Role | Authorization role | Fixed enum vs configurable roles; role hierarchy (if any) |
| Target | Goal assigned to a person/team and period | Metric, unit, dates, hierarchy, edit history |
| Organisation | Prospect/company profile; table `organisation` | Implemented fields and normalized-name duplicate rule; lifecycle/archive policy remains open |
| Contact | Person at one organisation; table `contact` | Implemented fields and per-organisation email duplicate rule; consent/contact restrictions remain open |
| Activity / Follow-up | Calls and next actions | Activity types, due-date rules, completion, ownership, audit trail |
| SQL Qualification | BDR qualification of a sales pursuit; one record per lead, with append-only review snapshots | Detailed acceptance rubric and deal-value currency |
| Legacy SQL Submission | Earlier Block 4 submission row; retained for compatibility/history | Migration or archival plan if legacy rows exist |
| Opportunity | One row per approved SQL assigned to an AE; lifecycle stage, estimate, and final close values | Official reporting currency and forecast rules |
| Assignment history | Append-only AE assignment/reassignment audit | Retention policy |
| Meeting activity | Append-only completed-meeting activity with type, level, outcome, participants, and follow-up | Scheduling/calendar integration |
| In-app notification | User-targeted assignment notification and read timestamp | Retention, delivery/polling expectations |
| Customer account expiry | Not modeled | Customer lifecycle, contract/account expiry source and date rules |
| Stakeholder link | Contacts involved in an opportunity with role labels | A contact can hold multiple different roles; duplicate same-role links are rejected |
| Outcome / Handover | Append-only Won/Lost close records and one implementation handover per Won closure | Additional handover acceptance/checklist workflow |
| Stage history / reopen request | Append-only stage transitions and manager-reviewed reopening requests | Reopen SLA and notifications |
| Revival work | Re-engagement with inactive customers | Inactivity definition, assignment, relationship to prior opportunities |

## Data rules to decide

- Which fields are required and which identifiers must be unique?
- How are duplicate organisations and contacts found and resolved?
- Are records archived/soft-deleted or permanently deleted? What retention rules apply?
- What currency, timezone, and date conventions apply to amounts, targets, and activities?
- Which changes require an audit history, and who can view it?
- How are ownership changes and reassignment recorded?
- Which calculated values are stored versus derived?

## Migration policy

Liquibase is the schema-change mechanism. Changeset `004-create-sql-qualification-workflow` adds qualification and decision history. Changeset `005-sales-manager-team-opportunities` adds the nullable manager-team link, unique-per-SQL opportunity, assignment history, meeting activity, and in-app notifications. Changeset `006-ae-opportunity-lifecycle` adds lifecycle fields, stage/closure histories, stakeholders, reopen requests, meeting details/participants, and implementation handovers. On the next backend start connected to PostgreSQL, Liquibase applies unapplied changesets automatically.

## Block 2 identity fields to validate

The account model uses a normalized unique email, BCrypt password hash, active/inactive status, one role foreign key, forced-password-change flag, and timestamps. Password hashes are excluded from DTOs.
