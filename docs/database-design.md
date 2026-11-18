# Database Design

**Status:** Identity and Block 3 organisation/contact tables are implemented with Liquibase. Later sales workflow entities, record lifecycle, and ownership policy remain conceptual. See the [ER diagram](er-diagram.md).

## Candidate concepts

| Concept | Candidate purpose | Decisions needed |
|---|---|---|
| User | Login identity, active state, assigned role(s) | Identity provider, one/multiple roles, provisioning, deactivation, manager/team relationship |
| Role | Authorization role | Fixed enum vs configurable roles; role hierarchy (if any) |
| Target | Goal assigned to a person/team and period | Metric, unit, dates, hierarchy, edit history |
| Organisation | Prospect/company profile; table `organisation` | Implemented fields and normalized-name duplicate rule; lifecycle/archive policy remains open |
| Contact | Person at one organisation; table `contact` | Implemented fields and per-organisation email duplicate rule; consent/contact restrictions remain open |
| Activity / Follow-up | Calls and next actions | Activity types, due-date rules, completion, ownership, audit trail |
| SQL Submission | BDR submission for review | SQL meaning, qualification criteria, revisions, rejection/rework states |
| Opportunity | Sales pursuit assigned to AE | Relationship to organisation, amount/currency, stages, forecast rules |
| Meeting | L1/L2/L3 event and outcome | Whether stages are fixed, optional/repeatable, attendees, scheduling source |
| Stakeholder link | Contacts involved in an opportunity | Role labels and whether one person can hold multiple roles |
| Outcome / Handover | Won/Lost result and implementation transfer | Required fields, approval, handover checklist, reopen policy |
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

Liquibase is the schema-change mechanism. The initial migration creates fixed roles and users. Changeset `002-create-organisation-contact-tables` adds `organisation` and `contact`, foreign keys to the creator and parent organisation, normalized duplicate indexes, timestamps, and optimistic-lock versions. On the next backend start connected to PostgreSQL, Liquibase will apply the new changeset automatically.

## Block 2 identity fields to validate

The account model uses a normalized unique email, BCrypt password hash, active/inactive status, one role foreign key, forced-password-change flag, and timestamps. Password hashes are excluded from DTOs.
