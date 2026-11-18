# ADR-0007: Use a Separate SQL Qualification and Manager Review Workflow

- **Status:** Implemented
- **Date:** 2026-11-19

## Context

The minimal Block 4 SQL submission contained only a lead and submitter. The user specified a qualification form, BDR drafts/submissions, Sales Manager approval or rejection, rejection feedback, resubmission, audit history, and eligibility for later AE assignment. BDRs own qualification entry; managers receive submitted qualifications. AE assignment is outside this module.

## Decision

- Store one `sql_qualification` per sales lead, enforced by a unique database constraint. Drafts and rejected records are visible to their BDR; only submitted and reviewed records appear in the manager queue.
- Store company size and industry as qualification snapshots, alongside business requirement, decision-maker flag/details, estimated deal value, expected timeline, notes, and supporting information.
- Use the explicit lifecycle `DRAFT` → `SUBMITTED` → `APPROVED` or `REJECTED`. A rejected record may be edited; saving it returns it to `DRAFT`, while its previous rejection remains in history. A new submission is reviewed as a new decision event.
- Require business requirement, a yes/no decision-maker answer, estimated deal value, and expected timeline to submit. Require decision-maker details when the answer is yes. Leave detailed acceptance scoring and currency convention for tech-lead confirmation.
- Append an immutable history record for create, save, submit, approve, and reject. Each event captures actor, timestamp, status change, reason when present, and a JSON snapshot of the qualification.
- Record reviewer and review time on approval/rejection. Approval marks the record as eligible for AE assignment; it does not create or assign an AE opportunity.
- Retain the original `sql_submission` table and read path for legacy records. All new qualification writes use the new API and schema.

## Alternatives considered

- Reuse the initial minimal submission row: rejected because it has nowhere to store required qualification fields, review state, rejection reason, or resubmission history.
- Put qualification fields directly on the lead: rejected because the lead is a sales pursuit record and needs independent qualification lifecycle and audit history.
- Create a new qualification row per resubmission: rejected because one stable qualification per pursuit with append-only snapshots preserves the full rework history and prevents duplicate active qualifications.
- Add generic State/Strategy/Specification abstractions: rejected because there is one fixed lifecycle and no confirmed policy variants. Explicit enum transitions and service methods are simpler.
- Assign an AE during approval: rejected because assignment is a separate downstream workflow.

## Implementation

- **Backend:** `SqlQualificationEntity`, `SqlQualificationHistoryEntity`, `SqlQualificationStatus`, `SqlQualificationRepository`, `SqlQualificationHistoryRepository`, `SqlQualificationService`, `BdrSqlQualificationController`, `SalesManagerSqlQualificationController`, and Liquibase changeset `004-create-sql-qualification-workflow`.
- **Frontend:** `SqlQualificationFormPage.jsx`, `SalesManagerSqlQueuePage.jsx`, `SalesManagerSqlReviewPage`, BDR/manager routes in `App.jsx`, and links in `roleNavigation.js`.
- **Tests:** `Block4ProspectingIntegrationTest` covers draft creation, duplicate prevention, invalid lead links, submit, review, rejection reason, resubmission, approval audit, eligibility, and role checks.
- **Actual example:** `SqlQualificationService.submit()` validates required information, transitions only a draft to submitted, and records the actor and snapshot inside the same transaction. `approve()` and `reject()` accept only submitted qualifications.

## Benefits and trade-offs

- The lead stays a distinct sales pursuit; qualification status and form data have a dedicated lifecycle.
- Duplicate pursuit qualifications are prevented at the database layer, and manager decisions are auditable even after a BDR revises and resubmits.
- The manager can review records without seeing in-progress drafts. Approval exposes downstream eligibility without coupling this module to AE assignment.
- The one-record-per-lead rule disallows multiple independent qualification tracks on one pursuit. If the business needs multiple decision cycles, the model will need an explicit new-pursuit or versioning rule.
- History snapshots increase storage use and the `snapshot_json` field is intentionally large; retention and privacy rules should be confirmed before production.

## Consequences

- Block 4 qualification and manager decision workflow is implemented. Assignment rules, SQL acceptance rubric, and estimated-value currency remain open decisions.
- Liquibase changeset 004 runs on backend startup against a configured database.
