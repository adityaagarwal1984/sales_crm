# API reference

Base path: `/api/v1`. Requests and responses use JSON. List endpoints use zero-based `page` and bounded `size` (default 20); search/filter parameters are shown below. Inputs use validated DTOs; errors are returned by centralized exception handling. Swagger/OpenAPI is available locally.

## Access model

| Route group | Access |
|---|---|
| `/auth/login`, `/actuator/health`, Swagger | Public |
| `/admin/**` | ADMIN |
| `/organisations/**`, `/contacts/**` reads | Authenticated roles |
| Organisation/contact create and update | ADMIN, BDR |
| `/bdr/**` | BDR; records scoped to the current BDR |
| `/sales-manager/**` | SALES_MANAGER; team scope covers all BDRs and AEs |
| `/ae/**` | AE; opportunity access scoped to current assignment |
| `/leadership/**` | LEADERSHIP, read-only analytics |
| `/notifications/**` | Authenticated; recipient scoped |

Backend authorization is authoritative. UI route guards do not replace it.

## Endpoints

| Method and path | Purpose |
|---|---|
| `POST /auth/login` | Email/password login; returns access JWT and user/role |
| `GET /auth/me`, `POST /auth/change-password` | Current identity; change forced/regular password |
| `GET /admin/dashboard`, `/admin/users` | Admin overview and user list |
| `GET /admin/users/{id}`, `POST /admin/users`, `PUT /admin/users/{id}` | Read/create/edit user |
| `PATCH /admin/users/{id}/status`, `POST /admin/users/{id}/password-reset` | Activate/deactivate; set temporary password |
| `GET /organisations?search=&industry=&companySize=&leadSource=&page=&size=` | Search shared organisation directory |
| `POST /organisations`, `GET /organisations/{id}`, `PUT /organisations/{id}` | Create, view, edit organisation |
| `POST /organisations/{id}/contacts` | Add contact to organisation |
| `GET /contacts?search=&organisationId=&page=&size=`, `GET /contacts/{id}`, `PUT /contacts/{id}` | Search, view, edit contacts |
| `GET /bdr/dashboard`, `GET|POST /bdr/leads` | BDR metrics; list/create owned leads |
| `GET|PUT /bdr/leads/{id}` | View/edit owned lead |
| `GET /bdr/calls`, `GET|POST /bdr/leads/{id}/calls` | Call list/history; record a new immutable call |
| `GET /bdr/follow-ups?filter=&page=&size=`, `PATCH /bdr/follow-ups/{id}`, `POST /bdr/follow-ups/{id}/complete` | List, reschedule, complete with a new call |
| `GET|POST /bdr/sql-qualifications`, `GET /bdr/sql-qualifications/{id}`, `GET /bdr/sql-qualifications/by-lead/{leadId}`, `PUT /bdr/sql-qualifications/{id}`, `POST /bdr/sql-qualifications/{id}/submit` | Draft, view, edit, submit BDR qualification |
| `GET /sales-manager/sql-qualifications?status=&page=&size=`, `GET /sales-manager/sql-qualifications/{id}` | Review queue and detail |
| `POST /sales-manager/sql-qualifications/{id}/approve`, `POST .../{id}/reject` | Decide with audit history; rejection requires reason |
| `GET /sales-manager/sql-qualifications/approved-for-assignment`, `POST /sales-manager/sql-qualifications/{id}/assign` | Find eligible SQL and assign to AE |
| `GET /sales-manager/dashboard`, `/team-performance`, `/account-executives`, `/opportunities` | Manager overview and team/opportunity lists |
| `PUT /sales-manager/opportunities/{id}/assignment`, `GET .../{id}/assignment-history` | Reassign and inspect history |
| `GET /sales-manager/opportunity-reopen-requests`, `POST .../{id}/decision` | Review AE reopen request |
| `GET /ae/opportunities/dashboard`, `GET /ae/opportunities`, `GET /ae/opportunities/{id}` | AE dashboard, assigned list, detail |
| `POST /ae/opportunities/{id}/meetings`, `PATCH .../{id}/stage`, `PATCH .../{id}/close-won`, `PATCH .../{id}/close-lost` | Log meeting, advance, or close opportunity |
| `POST|DELETE /ae/opportunities/{id}/stakeholders[/{contactId}]` | Add/remove opportunity contact |
| `POST /ae/opportunities/{id}/meetings/{meetingId}/follow-up/complete`, `POST .../{id}/reopen-request` | Complete meeting follow-up; request manager-approved reopen |
| `GET /leadership/analytics/filters|overview|bdrs|sales-managers|aes|funnel|revenue` | Filter options and aggregated read-only analytics |
| `GET /notifications`, `POST /notifications/{id}/read` | List recipient notifications and mark read |

All paths above are relative to `/api/v1`. For request DTO fields and response schemas, use the OpenAPI UI or the DTO records in the matching backend module; this page is a route/access map, not a generated schema.

## API conventions and boundaries

- UUIDs identify records; list results are paginated rather than unbounded.
- DTOs define the HTTP contract; persistence entities are not returned directly.
- Service methods enforce ownership and workflow rules; Spring Security enforces roles.
- Database constraints protect concurrent uniqueness; services translate expected conflicts into client-safe errors.
- Calls, reviews, assignments, stage transitions, and closures append history. Completing a follow-up creates a new call record.
- A contact is a person; a lead is a sales pursuit; an SQL is its qualification/review; an opportunity is the approved pursuit assigned to an AE.
