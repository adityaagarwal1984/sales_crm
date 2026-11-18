# Role and Permission Matrix

**Status: Mixed confirmed and draft capability map.** Shared organisation/contact read access and the BDR → Sales Manager SQL handoff are confirmed by the user. Other checkmarks describe responsibility and do not grant unrestricted access to every record.

| Capability | ADMIN | BDR | SALES_MANAGER | AE | LEADERSHIP |
|---|:---:|:---:|:---:|:---:|:---:|
| User creation / role management | ✓ | — | — | — | — |
| Targets and system configuration | ✓ | — | — | — | — |
| Read shared organisations and contacts | ✓ | ✓ | ✓ | ✓ | ✓ |
| Create/edit organisations and contacts | ✓ | ✓ | — | — | — |
| Calling and follow-ups | — | ✓ | — | — | — |
| Create lead and qualify/submit SQL | — | ✓ | — | — | — |
| View submitted SQL qualifications | — | Own records/status/history | ✓ | — | — |
| Save SQL drafts | — | ✓ | — | — | — |
| Approve/reject submitted SQL with reason | — | — | ✓ | — | — |
| AE assignment | — | — | ✓ | — | — |
| Team performance | — | — | ✓ | — | — |
| Inactive customer / revival management | — | — | ✓ | — | — |
| Assigned opportunities | — | — | — | ✓ | — |
| L1/L2/L3 meetings and stakeholders | — | — | — | ✓ | — |
| Won/Lost and implementation handover | — | — | — | ✓ | — |
| Organization-wide performance / revenue | — | — | — | — | ✓ |
| Department analytics / sales funnel | — | — | — | — | ✓ |

## Authorization rules to clarify

- Does ADMIN see and change all business records, or only configuration and identities?
- Does SALES_MANAGER see all SQLs or only their team? The current queue is organization-wide pending team scope rules.
- Can BDRs see their submissions after AE assignment? Can they edit/resubmit rejected submissions?
- Can AEs see all shared accounts and contacts (directory reads are enabled), or only accounts linked to assigned work?
- Does LEADERSHIP have read-only access? Are revenue and person-level metrics restricted?
- Can users have more than one role? How are permissions scoped across teams/departments?
- What happens to records when a user is deactivated or changes role/team?

## Enforcement

The backend must enforce authorization on every protected operation, including ownership/team scope. Hiding a button or navigation item in React is not a security control. Deny-by-default is the proposed implementation posture, pending review of explicit exceptions.
