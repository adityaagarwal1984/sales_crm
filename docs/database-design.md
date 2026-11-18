# Database Design

**Status:** The Block 2 identity schema is implemented; business record definitions, ownership, deletion policy, and business relationships remain conceptual. The [ER diagram](er-diagram.md) is provisional.

## Candidate concepts

| Concept | Candidate purpose | Decisions needed |
|---|---|---|
| User | Login identity, active state, assigned role(s) | Identity provider, one/multiple roles, provisioning, deactivation, manager/team relationship |
| Role | Authorization role | Fixed enum vs configurable roles; role hierarchy (if any) |
| Target | Goal assigned to a person/team and period | Metric, unit, dates, hierarchy, edit history |
| Organisation | Prospect/customer account | Required fields, unique identifiers, lifecycle and duplicate policy |
| Contact | Person at an organisation | Multiple organisations, consent/contact restrictions, dedupe policy |
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

Liquibase is the schema-change mechanism. The initial identity migration creates fixed roles and users with normalized unique email, BCrypt hash, active state, forced-password-change flag, credential-change timestamp, and created/updated timestamps. Business tables have not yet been migrated.

## Block 2 identity fields to validate

The account model uses a normalized unique email, BCrypt password hash, active/inactive status, one role foreign key, forced-password-change flag, and timestamps. Password hashes are excluded from DTOs.
