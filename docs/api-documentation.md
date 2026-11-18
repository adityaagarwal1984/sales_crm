# API Documentation

**Status:** Identity, organisation/contact, BDR prospecting, and SQL qualification/review routes are implemented. Later business-module routes remain draft pending tech-lead decisions.

## Proposed conventions

- Base path candidate: `/api/v1` (confirm compatibility/versioning expectations).
- JSON request and response DTOs; never expose persistence entities.
- Consistent validation errors and centralized error responses; exact schema to define.
- Pagination/filtering for large lists; define cursor/page conventions, sort allowlist, and filter grammar before implementation.
- Authentication and authorization on the server; Swagger UI must not imply that an endpoint is public.

## Candidate API groups

| Group | Candidate operations |
|---|---|
| Identity | Sign-in / identity lifecycle (depends on auth decision) |
| Users and roles | User administration, role assignment, activation |
| Targets | Create/update/view target periods and progress |
| Organisations and contacts | Search, create, update, view, deduplicate |
| Activities | Record calls, notes, and follow-ups |
| SQL qualifications | BDR draft and submission; manager review, approve/reject, and history |
| Opportunities | List assigned records, update pipeline and stakeholders |
| Meetings | Record L1/L2/L3 meetings and outcomes |
| Outcomes / handover | Mark Won/Lost and initiate implementation transfer |
| Revival | Identify, assign, and update revival work |
| Analytics | Role-authorized dashboards and reporting |

## Block 2 candidate endpoints

These paths are a working draft for the authentication/user-management block and remain subject to API review:

| Method | Path | Access | Purpose |
|---|---|---|---|
| POST | `/api/v1/auth/login` | Public | Validate email/password; return 15-minute access token and safe user profile |
| GET | `/api/v1/auth/me` | Authenticated | Return current safe user profile |
| POST | `/api/v1/auth/change-password` | Authenticated | Change temporary/current password; return a fresh access token |
| GET | `/api/v1/admin/dashboard` | ADMIN | Return active/total user counts and available roles |
| GET | `/api/v1/admin/users` | ADMIN | List users (pagination/search are not included in this first MVP) |
| POST | `/api/v1/admin/users` | ADMIN | Create a user with one role and temporary password |
| GET | `/api/v1/admin/users/{id}` | ADMIN | View a safe user profile |
| PUT | `/api/v1/admin/users/{id}` | ADMIN | Edit name, email, or role |
| PATCH | `/api/v1/admin/users/{id}/status` | ADMIN | Activate or deactivate an account |
| POST | `/api/v1/admin/users/{id}/password-reset` | ADMIN | Set temporary password; next login requires change |

## Block 3 organisation and contact endpoints

All roles can read the shared organisation/contact directory. BDR and ADMIN can create and edit these records. The backend enforces method-specific access; frontend route guards only control navigation. `createdBy` is retained for traceability.

| Method | Path | Access | Purpose |
|---|---|---|---|
| GET | `/api/v1/organisations` | All roles | Search/filter shared organisations with pagination |
| POST | `/api/v1/organisations` | BDR, ADMIN | Create organisation; creator is read from the authenticated principal |
| GET | `/api/v1/organisations/{id}` | All roles | View profile, creator, and its contacts |
| PUT | `/api/v1/organisations/{id}` | BDR, ADMIN | Edit organisation fields |
| POST | `/api/v1/organisations/{id}/contacts` | BDR, ADMIN | Add a contact to an organisation |
| GET | `/api/v1/contacts` | All roles | Search shared contacts, optionally filter by `organisationId`, with pagination |
| GET | `/api/v1/contacts/{id}` | All roles | View contact and linked organisation |
| PUT | `/api/v1/contacts/{id}` | BDR, ADMIN | Edit contact; `organisationId` can change its linked organisation |

Organisation list query parameters: `search` (name, website, industry, location), `industry`, `companySize`, `leadSource`, `page` (zero-based, default 0), and `size` (default 20, maximum 100). Contact list accepts `search` (name, email, phone, designation, department, organisation name), `organisationId`, `page`, and `size`. Both use `createdAt DESC` ordering and return Spring Data's page JSON (`content`, `number`, `size`, `totalElements`, `totalPages`).

Organisation input accepts `name` (required, max 180), `website`, `industry`, `companySize`, `location`, `address`, `leadSource`, and `description`. Contact input accepts `name` (required, max 180), optional `email`, `phone`, `designation`, `department`, `linkedinUrl`, and optional `organisationId` on contact updates. Values in the company size, industry, and lead source fields are free text because no controlled vocabulary was supplied. Blank optional values are stored as null. No endpoint exposes JPA entities.

Duplicate policy: organisation names are unique ignoring case and surrounding whitespace. A non-empty contact email is unique within its organisation, but may appear under another organisation. Database indexes enforce both duplicate rules against concurrent requests. Contact email is optional. Duplicate rules and role/ownership scope remain assumptions to confirm with the tech lead.

List/detail DTOs include linked organisation/contact data and the creator's ID/name; they never include user credential data. Missing records return 404, duplicate records return 409, validation errors return 400, and non-BDR/non-admin roles receive 403.

The auth and admin user endpoints never include a password or hash. Invalid email/password and deactivated accounts return a generic `401`; expired or inactive-user tokens receive `401`; authenticated non-admins receive `403`. The login response includes an access token valid for 15 minutes and a `user` object. Access tokens stay in frontend memory only; there is no refresh token.

## Contract decisions pending

Authentication endpoints and token transport; ownership scope; status transitions; error codes; filter fields; date/time representation; currency precision; bulk operations; export needs; idempotency; rate limits; and audit data. See [Open Questions](OPEN-QUESTIONS.md).

## Block 4 BDR prospecting and SQL qualification endpoints

All endpoints below require an authenticated token. Lead, call, follow-up, and qualification operations require BDR and are scoped to that BDR. Sales Manager review endpoints are restricted to the `SALES_MANAGER` role; manager queues include submitted, approved, and rejected qualifications, never drafts. Page responses use `content`, `number`, `size`, `totalElements`, and `totalPages`; `size` is limited to 100.

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/v1/bdr/dashboard` | Return all-time owned lead, created organisation/contact, call, interested lead, non-draft qualification, pending follow-up, and overdue follow-up counts |
| GET/POST | `/api/v1/bdr/leads` | Search and page owned leads / create a lead from an organisation and primary contact |
| GET/PUT | `/api/v1/bdr/leads/{id}` | View or edit an owned lead |
| GET/POST | `/api/v1/bdr/leads/{leadId}/calls` | Read call history or append a call activity; optional `nextFollowUpAt` creates a separate pending follow-up |
| GET | `/api/v1/bdr/calls` | Page all owned call activities |
| GET | `/api/v1/bdr/follow-ups?filter=UPCOMING\|OVERDUE\|COMPLETED\|ALL` | Page owned follow-ups; default filter is `UPCOMING` |
| PATCH | `/api/v1/bdr/follow-ups/{id}` | Reschedule a pending follow-up with `{ "dueAt": "ISO-8601 instant" }` |
| POST | `/api/v1/bdr/follow-ups/{id}/complete` | Complete a pending follow-up by appending a new call with outcome, calledAt, notes, and optional nextFollowUpAt |
| GET/POST | `/api/v1/bdr/sql-qualifications` | List own qualifications / create a draft for an owned lead whose latest call outcome is `INTERESTED` |
| GET | `/api/v1/bdr/sql-qualifications/{id}` | View own qualification, including its status, rejection reason, and history |
| GET | `/api/v1/bdr/sql-qualifications/by-lead/{leadId}` | Find own qualification for a lead, if one exists |
| PUT | `/api/v1/bdr/sql-qualifications/{id}` | Save a draft or edit a rejected qualification (saving a rejection returns it to `DRAFT`) |
| POST | `/api/v1/bdr/sql-qualifications/{id}/submit` | Validate and submit a draft to the Sales Manager queue |
| GET | `/api/v1/sales-manager/sql-qualifications?status=SUBMITTED` | Page the manager queue; supported statuses: `SUBMITTED`, `APPROVED`, `REJECTED` |
| GET | `/api/v1/sales-manager/sql-qualifications/{id}` | Review qualification details and immutable history snapshots |
| POST | `/api/v1/sales-manager/sql-qualifications/{id}/approve` | Approve a submitted qualification; records reviewer and timestamp |
| POST | `/api/v1/sales-manager/sql-qualifications/{id}/reject` | Reject a submitted qualification with `{ "reason": "..." }` |

Call outcomes: `CONNECTED`, `NOT_CONNECTED`, `BUSY`, `CALL_BACK_LATER`, `INTERESTED`, `NOT_INTERESTED`, `INVALID_NUMBER`. Qualification submission requires a business requirement, a yes/no decision-maker value, decision-maker details when yes, estimated deal value, and expected timeline. Only one qualification record can exist per lead; rejected records can be edited and resubmitted, preserving each decision and snapshot in history. Approval marks a qualification eligible for AE assignment, but this API does not assign an AE. The older `sql_submission` table and read endpoint are retained for records from the initial Block 4 flow; new qualification work uses `sql_qualification`.
