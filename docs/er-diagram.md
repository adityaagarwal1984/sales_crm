# Provisional ER Diagram

**Conceptual only.** This is a discussion aid, not a database schema. Dashed relationships and entity names are candidates. Cardinalities and lifecycle must be confirmed before migrations are designed.

```mermaid
erDiagram
  USER ||--o{ TARGET : receives
  ROLE }o--o{ USER : assigned_to
  USER ||--o{ ACTIVITY : owns
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
    string lifecycle
  }
  CONTACT {
    uuid id PK
    string name
    string email
  }
  ACTIVITY {
    uuid id PK
    string type
    datetime occurred_at
    datetime next_action_at
  }
  SQL_SUBMISSION {
    uuid id PK
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

## Important caveats

- The SQL submission → opportunity conversion may be one-to-one, one-to-many, or not a separate record at all.
- A contact may belong to multiple organisations; an activity may relate to several record types. These are unresolved.
- Role assignment may be single-role, multi-role, or scoped by team. The drawing does not settle that question.
- `lifecycle`, `status`, `stage`, `metric`, and `type` values are placeholders, not approved enums.
- Use database-native UUIDs and types only after the agreed Java/PostgreSQL versions and conventions are chosen.
