# API Documentation

**Status:** Block 2 identity routes below match the implemented API. Business-module routes remain draft pending tech-lead decisions.

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

The auth and admin user endpoints never include a password or hash. Invalid email/password and deactivated accounts return a generic `401`; expired or inactive-user tokens receive `401`; authenticated non-admins receive `403`. The login response includes an access token valid for 15 minutes and a `user` object. Access tokens stay in frontend memory only; there is no refresh token.

## Contract decisions pending

Authentication endpoints and token transport; ownership scope; status transitions; error codes; filter fields; date/time representation; currency precision; bulk operations; export needs; idempotency; rate limits; and audit data. See [Open Questions](OPEN-QUESTIONS.md).
