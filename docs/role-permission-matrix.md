# Role and Permission Matrix

**Status: Implemented capability map with Block 7 revival still pending.** Shared directory access, organization-wide Sales Manager scope, AE assignment scope, and Leadership read-only analytics are implemented.

| Capability | ADMIN | BDR | SALES_MANAGER | AE | LEADERSHIP |
|---|:---:|:---:|:---:|:---:|:---:|
| User creation / role management | ✓ | — | — | — | — |
| Targets and system configuration | ✓ | — | — | — | — |
| Read shared organisations and contacts | ✓ | ✓ | ✓ | ✓ | ✓ |
| Create/edit organisations and contacts | ✓ | ✓ | — | — | — |
| Calling and follow-ups | — | ✓ | — | — | — |
| Create lead and qualify/submit SQL | — | ✓ | — | — | — |
| View submitted SQL qualifications | — | Own records/status/history | All BDR submissions | — | — |
| Save SQL drafts | — | ✓ | — | — | — |
| Approve/reject submitted SQL with reason | — | — | All BDR submissions | — | — |
| Assign/reassign approved opportunities | — | — | Any active AE | — | — |
| View assignment history and team performance | — | — | All CRM BDR/AE records | — | — |
| Inactive customer / revival management | — | — | ✓ | — | — |
| Assigned opportunities | — | — | — | Own assignments | — |
| Log completed opportunity meetings | — | — | — | Own assignments | — |
| Record Won/Lost outcome | — | — | — | Own active assignments | — |
| Organization-wide performance / revenue | — | — | — | — | Read-only |
| Department analytics / sales funnel | — | — | — | — | Read-only |

## Authorization rules to clarify

- Does ADMIN see and change all business records, or only configuration and identities?
- Every Sales Manager can review all submitted BDR qualifications, assignments, opportunities, and performance for all BDR/AE accounts created by Admin. The optional manager link does not limit access.
- Can BDRs see their submissions after AE assignment? Can they edit/resubmit rejected submissions?
- Can AEs see all shared accounts and contacts (directory reads are enabled), or only accounts linked to assigned work?
- Leadership has read-only access to analytics endpoints; person-level filters and results are available to the role.
- Can users have more than one role? Department and cross-team permission scope remains undefined.
- What happens to records when a user is deactivated or changes role/team?

## Enforcement

The backend must enforce authorization on every protected operation, including ownership/team scope. Hiding a button or navigation item in React is not a security control. Deny-by-default is the proposed implementation posture, pending review of explicit exceptions.
