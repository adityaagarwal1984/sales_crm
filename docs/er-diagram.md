# ER Diagram

Identity, organisation, contact, lead, call activity, follow-up, SQL qualification, opportunity lifecycle, meetings, stakeholders, handovers, and notifications are implemented through Liquibase. `sql_submission` is retained for records from the initial Block 4 flow. Revival work and targets remain conceptual.

```mermaid
erDiagram
  ROLE ||--o{ USER : assigned_to
  USER o|--o{ USER : sales_manager_team
  USER ||--o{ ORGANISATION : created
  USER ||--o{ TARGET : receives
  USER ||--o{ LEAD : owns
  ORGANISATION ||--o{ CONTACT : has
  ORGANISATION ||--o{ LEAD : has_pursuits
  CONTACT ||--o{ LEAD : primary_contact
  LEAD ||--o{ CALL_ACTIVITY : records
  CONTACT ||--o{ CALL_ACTIVITY : called
  USER ||--o{ CALL_ACTIVITY : creates
  LEAD ||--o{ FOLLOW_UP : schedules
  CALL_ACTIVITY ||--o| FOLLOW_UP : schedules
  USER ||--o{ FOLLOW_UP : owns
  LEAD ||--o{ SQL_SUBMISSION : submitted_as
  USER ||--o{ SQL_SUBMISSION : submits
  LEAD ||--o| SQL_QUALIFICATION : qualified_as
  USER ||--o{ SQL_QUALIFICATION : creates
  USER ||--o{ SQL_QUALIFICATION : reviews
  SQL_QUALIFICATION ||--o{ SQL_QUALIFICATION_HISTORY : records
  USER ||--o{ SQL_QUALIFICATION_HISTORY : acts
  SQL_QUALIFICATION ||--o| OPPORTUNITY : assigned_to_ae
  USER ||--o{ OPPORTUNITY : owns_as_ae
  USER ||--o{ OPPORTUNITY : assigns
  OPPORTUNITY ||--o{ ASSIGNMENT_HISTORY : logs
  USER o|--o{ ASSIGNMENT_HISTORY : prior_ae
  USER ||--o{ ASSIGNMENT_HISTORY : next_ae
  USER ||--o{ ASSIGNMENT_HISTORY : manager_actor
  OPPORTUNITY ||--o{ MEETING : includes
  USER ||--o{ MEETING : records_as_ae
  USER ||--o{ NOTIFICATION : receives
  CONTACT }o--o{ OPPORTUNITY : stakeholder_on
  OPPORTUNITY ||--o{ HANDOVER : creates_per_won_close
  OPPORTUNITY ||--o{ OPPORTUNITY_STAGE_HISTORY : records
  USER ||--o{ OPPORTUNITY_STAGE_HISTORY : changes
  OPPORTUNITY ||--o{ OPPORTUNITY_CLOSURE : closes
  USER ||--o{ OPPORTUNITY_CLOSURE : closes_as_ae
  OPPORTUNITY ||--o{ OPPORTUNITY_REOPEN_REQUEST : requests
  USER ||--o{ OPPORTUNITY_REOPEN_REQUEST : requests_or_reviews
  MEETING }o--o{ CONTACT : participants
  ORGANISATION ||--o{ REVIVAL_WORK : may_have
  USER ||--o{ REVIVAL_WORK : assigned_to

  USER {
    uuid id PK
    string display_name
    string email
    boolean active
    uuid manager_id FK
  }
  ROLE {
    string code PK
  }
  TARGET {
    uuid id PK
    string metric
    date period_start
    date period_end
  }
  ORGANISATION {
    uuid id PK
    string name
    string website
    string industry
    string company_size
    string location
    string address
    string lead_source
    string description
    uuid created_by FK
    datetime created_at
    datetime updated_at
  }
  CONTACT {
    uuid id PK
    uuid organisation_id FK
    string full_name
    string email
    string phone
    string designation
    string department
    string linkedin_url
    uuid created_by FK
    datetime created_at
    datetime updated_at
  }
  LEAD {
    uuid id PK
    uuid organisation_id FK
    uuid primary_contact_id FK
    uuid owner_id FK
    uuid created_by FK
    string title
    string description
    string last_call_outcome
    datetime last_call_at
    datetime created_at
    datetime updated_at
  }
  CALL_ACTIVITY {
    uuid id PK
    uuid lead_id FK
    uuid contact_id FK
    uuid created_by FK
    string outcome
    datetime called_at
    string notes
    datetime next_follow_up_at
    datetime created_at
  }
  FOLLOW_UP {
    uuid id PK
    uuid lead_id FK
    uuid contact_id FK
    uuid source_call_id FK
    uuid completed_call_id FK
    uuid owner_id FK
    string status
    datetime due_at
    datetime completed_at
    datetime created_at
    datetime updated_at
  }
  SQL_SUBMISSION {
    uuid id PK
    uuid lead_id FK
    uuid submitted_by FK
    string status
    datetime submitted_at
  }
  OPPORTUNITY {
    uuid id PK
    uuid sql_qualification_id FK UK
    uuid assigned_ae_id FK
    uuid assigned_by FK
    string status
    decimal amount
    string currency_code
    datetime assigned_at
    datetime created_at
    datetime updated_at
  }
  ASSIGNMENT_HISTORY {
    uuid id PK
    uuid opportunity_id FK
    uuid previous_ae_id FK
    uuid assigned_ae_id FK
    uuid assigned_by FK
    string action
    datetime created_at
  }
  MEETING {
    uuid id PK
    uuid opportunity_id FK
    uuid ae_user_id FK
    string title
    string notes
    datetime completed_at
    string meeting_type
    string level
    string outcome
    string next_action
    datetime follow_up_at
  }
  OPPORTUNITY_STAGE_HISTORY {
    uuid id PK
    uuid opportunity_id FK
    uuid actor_id FK
    string from_stage
    string to_stage
    string reason
    datetime created_at
  }
  OPPORTUNITY_CLOSURE {
    uuid id PK
    uuid opportunity_id FK
    uuid actor_id FK
    string outcome
    decimal final_deal_value
    datetime closed_at
    string reason
    string notes
  }
  OPPORTUNITY_REOPEN_REQUEST {
    uuid id PK
    uuid opportunity_id FK
    uuid requested_by FK
    uuid reviewed_by FK
    string status
    string request_reason
    string review_reason
    datetime reviewed_at
  }
  OPPORTUNITY_STAKEHOLDER {
    uuid id PK
    uuid opportunity_id FK
    uuid contact_id FK
    string role
    datetime created_at
  }
  NOTIFICATION {
    uuid id PK
    uuid recipient_id FK
    string title
    string message
    string target_path
    datetime created_at
    datetime read_at
  }
  SQL_QUALIFICATION {
    uuid id PK
    uuid lead_id FK UK
    uuid created_by FK
    uuid reviewed_by FK
    string company_size
    string industry
    string business_requirement
    boolean decision_maker_identified
    string decision_maker_details
    decimal estimated_deal_value
    string expected_timeline
    string qualification_notes
    string supporting_information
    string status
    string rejection_reason
    datetime submitted_at
    datetime reviewed_at
    datetime created_at
    datetime updated_at
  }
  SQL_QUALIFICATION_HISTORY {
    uuid id PK
    uuid qualification_id FK
    uuid actor_id FK
    string action
    string from_status
    string to_status
    string reason
    json snapshot
    datetime created_at
  }
  HANDOVER {
    uuid id PK
    datetime created_at
    string status
  }
  REVIVAL_WORK {
    uuid id PK
    string status
    datetime due_at
  }
```

## Implemented relationship and duplicate rules

- One `app_user` created-by reference can be associated with many organisations; each organisation retains one creator.
- One organisation has zero or more contacts; each contact belongs to exactly one organisation. The database foreign key prevents deleting an organisation while it still has contacts; no delete endpoint is exposed in this block.
- Organisation name uniqueness is case-insensitive after trimming. The database index enforces this against concurrent requests.
- Contact email is optional and unique within one organisation when supplied, case-insensitive after trimming. A person may be linked to another organisation with the same email.
- A contact is a person record. It is not an SQL, lead, or opportunity.
- A lead is a BDR-owned sales pursuit linked to one organisation and one primary contact. Each call is an append-only row; a call may schedule a separate follow-up row.
- Completing a pending follow-up references a newly created call activity; it never mutates call history. A SQL qualification is a separate row linked one-to-one to its sales lead. Its history is append-only and stores the actor, decision, reason, and point-in-time form snapshot.
- Each approved SQL creates at most one opportunity. Opportunities progress through L1/L2/L3 before Won/Lost; stage transitions, closure snapshots, and reopen requests are retained as history. One handover is created per Won closure, so a reopened and re-won opportunity can have multiple historical handovers.
- Stakeholders must be contacts linked to the opportunity's organisation; a contact-role pair cannot be duplicated on one opportunity. Meeting participants are associated separately from stakeholder role assignments.

## Important caveats

- An approved SQL can have one opportunity. Reassignment changes the current AE and appends to assignment history; won/lost opportunities cannot be reassigned.
- A contact belongs to one organisation in Block 3. Revisit only if the business requires a person-to-many-organisations relationship; activity polymorphism remains unresolved.
- Role assignment is one role per user in the implemented identity module.
- The qualification status lifecycle is `DRAFT` → `SUBMITTED` → `APPROVED` or `REJECTED`; a rejected record can return to `DRAFT` for revision and resubmission. Database uniqueness prevents a second qualification for the same lead.
- BDRs and AEs may have an optional nullable `app_user.manager_id` link for admin reference. Every Sales Manager can access all BDR SQLs, opportunities, performance metrics, and active AE assignment targets regardless of this link.
- Opportunity value is copied from estimated deal value and defaults to INR pending the approved company currency convention. Completed meeting rows are append-only; the lightweight AE meeting log supports team counts.
- Use database-native UUIDs and types only after the agreed Java/PostgreSQL versions and conventions are chosen.
