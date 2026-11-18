# Role and Permission Matrix

**Status: Draft capability map from the supplied brief.** This is not a final authorization policy. A checkmark means the role is described as responsible for the capability; it does not grant unrestricted access to every record.

| Capability | ADMIN | BDR | SALES_MANAGER | AE | LEADERSHIP |
|---|:---:|:---:|:---:|:---:|:---:|
| User creation / role management | ✓ | — | — | — | — |
| Targets and system configuration | ✓ | — | — | — | — |
| Organisation discovery | — | ✓ | — | — | — |
| Contact mining | — | ✓ | — | — | — |
| Calling and follow-ups | — | ✓ | — | — | — |
| SQL submission | — | ✓ | Review only? | — | — |
| SQL review | — | — | ✓ | — | — |
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
- Does SALES_MANAGER see the whole organization, a team, or assigned records? Can managers edit or only review?
- Can BDRs see their own submissions after assignment? Can they edit/resubmit rejected submissions?
- Can AEs see only assigned opportunities, or also shared accounts and prior history?
- Does LEADERSHIP have read-only access? Are revenue and person-level metrics restricted?
- Can users have more than one role? How are permissions scoped across teams/departments?
- What happens to records when a user is deactivated or changes role/team?

## Enforcement

The backend must enforce authorization on every protected operation, including ownership/team scope. Hiding a button or navigation item in React is not a security control. Deny-by-default is the proposed implementation posture, pending review of explicit exceptions.
