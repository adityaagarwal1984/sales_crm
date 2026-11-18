# ER Diagram

Identity, organisation, contact, lead, call activity, follow-up, and SQL qualification tables are implemented through Liquibase. `sql_submission` is retained for records from the initial Block 4 flow; new SQL workflow records use `sql_qualification`. Later sales modules remain conceptual.

```mermaid
erDiagram
  ROLE ||--o{ USER : assigned_to
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
  SQL_SUBMISSION ||--o| OPPORTUNITY : may_convert_to
  LEAD ||--o| SQL_QUALIFICATION : qualified_as
  USER ||--o{ SQL_QUALIFICATION : creates
  USER ||--o{ SQL_QUALIFICATION : reviews
  SQL_QUALIFICATION ||--o{ SQL_QUALIFICATION_HISTORY : records
  USER ||--o{ SQL_QUALIFICATION_HISTORY : acts
  ORGANISATION ||--o{ OPPORTUNITY : has
  USER ||--o{ OPPORTUNITY : assigned_to
  OPPORTUNITY ||--o{ MEETING : includes
  CONTACT }o--o{ OPPORTUNITY : stakeholder_on
  OPPORTUNITY ||--o| HANDOVER : may_create
  ORGANISATION ||--o{ REVIVAL_WORK : may_have
  USER ||--o{ REVIVAL_WORK : assigned_to

  USER {
    uuid id PK
    string display_name
    string email
    boolean active
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
  OPPORTUNITY {
    uuid id PK
    string status
    decimal amount
    string currency
  }
  MEETING {
    uuid id PK
    string stage
    datetime scheduled_at
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

## Important caveats

- The SQL qualification → opportunity/AE assignment relationship is not yet implemented; approved qualification is marked eligible for assignment.
- A contact belongs to one organisation in Block 3. Revisit only if the business requires a person-to-many-organisations relationship; activity polymorphism remains unresolved.
- Role assignment is one role per user in the implemented identity module.
- The qualification status lifecycle is `DRAFT` → `SUBMITTED` → `APPROVED` or `REJECTED`; a rejected record can return to `DRAFT` for revision and resubmission. Database uniqueness prevents a second qualification for the same lead.
- Use database-native UUIDs and types only after the agreed Java/PostgreSQL versions and conventions are chosen.
