# ADR-0009: Give Sales Managers Organization-Wide BDR and AE Access

- **Status:** Accepted (2026-11-19)
- **Supersedes:** The team visibility boundary in [ADR-0008](ADR-0008-sales-manager-team-and-assignment.md)

## Context

The user clarified the CRM workflow: a BDR submits an SQL, a Sales Manager approves it and assigns the opportunity to an AE. Every Sales Manager must have access to all BDR and AE accounts created by Admin. A manager-to-user link must not hide approved SQLs or prevent assignment.

## Decision

- Sales Manager review queues and approved SQL assignment lists include qualifications from all BDR accounts, independent of optional `manager_id` links.
- All Sales Managers can assign or reassign opportunities to any active AE account.
- Dashboard counts, opportunity listings, assignment history, and BDR/AE performance cover all CRM BDR/AE records.
- Keep existing manager links as optional admin metadata for compatibility; do not use them as an authorization boundary or require them to create BDR/AE accounts.
- Keep role checks in Spring Security: these organization-wide capabilities remain limited to SALES_MANAGER routes.

## Consequences

- An approved SQL is available to managers after approval even if its BDR has no manager link.
- Newly created or legacy AEs without manager links are assignable when active.
- Multiple Sales Managers can review, assign, and reassign the same CRM opportunities. Assignment history records the acting manager.
- Any future need for manager ownership partitions requires a separately agreed policy and explicit enforcement changes.

## Implementation

`SqlQualificationRepository` and `SqlQualificationService` query by SQL status without manager linkage. `OpportunityService` uses organization-wide BDR/AE and opportunity queries, while validating that SQL creators are BDRs and assignment targets are active AEs. The Admin user form treats the manager link as optional. `Block5SalesManagerIntegrationTest` verifies approved SQL visibility across managers and unlinked BDR/AE access.
