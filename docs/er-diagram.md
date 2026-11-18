# ER Diagram

Identity, organisation, and contact tables are implemented through Liquibase. Later CRM workflow entities remain conceptual.

```mermaid
erDiagram
  ROLE ||--o{ USER : assigned_to
  USER ||--o{ ORGANISATION : created
  USER ||--o{ TARGET : receives
  ORGANISATION ||--o{ CONTACT : has
  ORGANISATION ||--o{ SQL_SUBMISSION : referenced_by
  USER ||--o{ SQL_SUBMISSION : submits
  SQL_SUBMISSION ||--o| OPPORTUNITY : may_convert_to
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

## Important caveats

- The SQL submission → opportunity conversion may be one-to-one, one-to-many, or not a separate record at all.
- A contact belongs to one organisation in Block 3. Revisit only if the business requires a person-to-many-organisations relationship; activity relationships remain unresolved.
- Role assignment is one role per user in the implemented identity module.
- `lifecycle`, `status`, `stage`, `metric`, and `type` values are placeholders, not approved enums.
- Use database-native UUIDs and types only after the agreed Java/PostgreSQL versions and conventions are chosen.
