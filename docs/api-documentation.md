# API Documentation

**Status:** Identity and Block 3 organisation/contact routes below are implemented. Other business-module routes remain draft pending tech-lead decisions.

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
| SQL submissions | Submit, list, review, request changes, accept/reject |
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

List/detail DTOs include linked organisation/contact data and the creator's ID/name; they never include user credential data. Missing records return 404, duplicate records return 409, validation errors return 400. All roles can read the shared directory; create and edit operations are restricted to BDR and ADMIN.

The auth and admin user endpoints never include a password or hash. Invalid email/password and deactivated accounts return a generic `401`; expired or inactive-user tokens receive `401`; authenticated non-admins receive `403`. The login response includes an access token valid for 15 minutes and a `user` object. Access tokens stay in frontend memory only; there is no refresh token.

## Contract decisions pending

Authentication endpoints and token transport; ownership scope; status transitions; error codes; filter fields; date/time representation; currency precision; bulk operations; export needs; idempotency; rate limits; and audit data. See [Open Questions](OPEN-QUESTIONS.md).
