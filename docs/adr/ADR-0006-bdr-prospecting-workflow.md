# ADR-0006: Model BDR Prospecting as Owned Leads, Immutable Calls, Follow-ups, and SQL Submissions

- **Status:** Implemented; SQL submission portion superseded by [ADR-0007](ADR-0007-sql-qualification-review.md)
- **Date:** 2026-11-19

## Context

Block 4 requires a BDR workflow connecting existing organisations and contacts to sales pursuits, call history, follow-ups, and SQL counts. The brief explicitly says a contact is a person and a lead is a sales pursuit; every call must be retained as a new activity. SQL qualification and review details are not defined.

## Decision

- Add a `prospecting` capability inside the existing modular monolith with direct controller → service → repository → entity layering.
- A lead references one organisation and one primary contact and is owned by its creating BDR. BDRs see their own leads, calls, follow-ups, and submissions. Sales Managers can read the shared SQL intake queue, including linked lead, organisation, and primary contact details; other roles are denied from BDR prospecting routes.
- Store each call as its own immutable `call_activity` record. `nextFollowUpAt` creates a separate `follow_up` record. Completing a pending follow-up appends a new call and marks that follow-up completed; rescheduling only changes the follow-up due time.
- The initial implementation stored a minimal SQL submission separately from leads. The richer qualification/review lifecycle is defined by ADR-0007.
- Dashboard totals are all-time. Pending and overdue follow-ups are calculated against the server's current instant. Interested Prospects counts leads whose latest call by call timestamp has outcome `INTERESTED`.
- Add contact creator attribution for the Contacts Added metric, backfilling existing contacts from the creator of their organisation.

## Alternatives considered

- Treat a lead as an organisation/contact status: rejected because it conflates a sales pursuit with the person/company records and cannot represent multiple pursuits.
- Update one call row on every new call: rejected because it destroys history and violates the brief.
- Store follow-up time only on calls: rejected because follow-ups need their own pending/completed state, reschedule history anchor, and queryable queues.
- Store SQL as a lead flag: rejected based on the user's selected separate-submission-record approach; it also cannot represent repeat submissions.
- Add patterns such as Strategy, State, event bus, or generic specifications: rejected for now because one fixed call outcome set and one follow-up lifecycle do not justify the added indirection.

## Consequences

- Call history remains append-only; follow-up lifecycle can be queried independently. The minimal SQL submission endpoint was replaced by the qualification flow in ADR-0007.
- Leads, calls, and follow-ups are private to their owning BDR. Sales Managers see SQL submissions and their linked summary for intake. Assignment to an AE is a later workflow step.
- The legacy `sql_submission` table remains for compatibility; new qualification work uses `sql_qualification`.
- The dashboard's Interested Prospects metric reflects the most recent call outcome, which should be confirmed with the tech lead.

## Main files

- Backend: `prospecting/domain`, `prospecting/repository`, `prospecting/service/ProspectingService.java`, `prospecting/api`, and `db/changelog/db.changelog-master.yaml`.
- Frontend: `frontend/src/pages/bdr/BdrPages.jsx`, `frontend/src/config/roleNavigation.js`, and `frontend/src/App.jsx`.
- Verification: `backend/src/test/java/com/prosperr/crm/Block4ProspectingIntegrationTest.java`.
