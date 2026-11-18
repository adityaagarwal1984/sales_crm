# API Documentation

**Status: Draft contract outline.** Endpoint names, payloads, and state transitions are not approved. OpenAPI should describe the agreed contract as implementation proceeds.

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

## Contract decisions pending

Authentication endpoints and token transport; ownership scope; status transitions; error codes; filter fields; date/time representation; currency precision; bulk operations; export needs; idempotency; rate limits; and audit data. See [Open Questions](OPEN-QUESTIONS.md).
