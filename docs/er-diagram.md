# Entity relationship overview

This diagram covers the implemented persistence model. `sql_submission` is a legacy table from the initial submission flow. Targets and customer revival are not currently modeled.

```mermaid
erDiagram
  APP_ROLE ||--o{ APP_USER : grants
  APP_USER o|--o{ APP_USER : manager_link
  APP_USER ||--o{ ORGANISATION : creates
  ORGANISATION ||--o{ CONTACT : contains
  ORGANISATION ||--o{ SALES_LEAD : pursued_by
  CONTACT ||--o{ SALES_LEAD : primary_contact
  APP_USER ||--o{ SALES_LEAD : owns
  SALES_LEAD ||--o{ CALL_ACTIVITY : has_history
  CONTACT ||--o{ CALL_ACTIVITY : contacted
  APP_USER ||--o{ CALL_ACTIVITY : logs
  SALES_LEAD ||--o{ FOLLOW_UP : schedules
  CALL_ACTIVITY ||--o| FOLLOW_UP : source_call
  SALES_LEAD ||--o| SQL_QUALIFICATION : qualifies
  SQL_QUALIFICATION ||--o{ SQL_QUALIFICATION_HISTORY : records
  SQL_QUALIFICATION ||--o| SALES_OPPORTUNITY : may_create
  SALES_OPPORTUNITY ||--o{ OPPORTUNITY_ASSIGNMENT_HISTORY : assignment_history
  SALES_OPPORTUNITY ||--o{ OPPORTUNITY_STAGE_HISTORY : stage_history
  SALES_OPPORTUNITY ||--o{ OPPORTUNITY_MEETING : meetings
  SALES_OPPORTUNITY ||--o{ OPPORTUNITY_STAKEHOLDER : stakeholders
  SALES_OPPORTUNITY ||--o{ OPPORTUNITY_CLOSURE : closure_history
  SALES_OPPORTUNITY ||--o{ OPPORTUNITY_REOPEN_REQUEST : reopen_requests
  OPPORTUNITY_MEETING ||--o{ OPPORTUNITY_MEETING_PARTICIPANT : participants
  SALES_OPPORTUNITY ||--o{ IMPLEMENTATION_HANDOVER : handover
  APP_USER ||--o{ APP_NOTIFICATION : receives
```

## Key cardinalities and rules

- One account has one role. `app_user.manager_id` is an optional reporting link; it does not limit Sales Manager team-wide access.
- One organisation has many contacts and sales leads. A lead links one organisation, one primary contact, and its BDR owner.
- One lead has at most one SQL qualification; an approved qualification can create at most one opportunity.
- Calls, SQL reviews, assignments, stage changes, closures, and reopen decisions retain separate history records.
- Stakeholders and meeting participants link contacts to opportunities/meetings; the service verifies they belong to the same organisation.
- A Won closure creates an implementation handover record.

For physical columns, indexes, foreign keys, and changeset ownership, see [Database Notes](database-design.md) and `backend/src/main/resources/db/changelog/db.changelog-master.yaml`.
