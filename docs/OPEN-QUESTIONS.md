# Open questions

These decisions remain unresolved or are provisional. The implemented defaults below came from the supplied project blocks; confirm business policy with the tech lead before production use.

## Product and workflow

1. What detailed SQL acceptance rubric defines a qualified prospect? The current fields/statuses follow the implementation brief.
2. What is the official currency, and should the CRM support multiple currencies?
3. What defines a customer account, expiry, and revival eligibility/owner/outcomes? Block 7 revival is not implemented.
4. What handover checklist and acceptance process should follow a Won opportunity?
5. What sales targets, periods, attribution rules, and approved KPI definitions should be used beyond the current Leadership formulas?
6. What notifications are required for overdue follow-ups, decisions, assignments, and handovers?

## Data, security, and operations

7. What are the retention, deletion, privacy/consent, audit, and import/export requirements?
8. Which external integrations (calling, email, calendar, lead sources, implementation systems) are in MVP scope?
9. What are approved deployment environments, PostgreSQL version/hosting, domains, secret rotation, backups, recovery objectives, and release process?
10. Does Prosperr mandate Java/Spring/Node versions, CI checks, accessibility target, supported browsers, or performance/availability objectives?
11. Is the current short-lived access JWT without refresh acceptable beyond local MVP, or is a cookie/session or federated identity approach required?

## Confirmed implementation defaults

- One role per account; first administrator provisioned from environment configuration; admin reset sets a temporary password that must be changed.
- JWT access token is valid for 15 minutes, held in frontend memory; active-user status is checked on requests; no refresh-token flow.
- BDRs share organisation/contact directory access, own leads and prospecting records; calls are append-only; completing follow-up logs a new call.
- Sales Managers can review all BDR submissions and assign approved SQLs to any active AE; assignment is recorded and the AE is notified.
- AE progression is L1 → L2 → L3 → Won/Lost; skipping requires a reason; reopening needs manager approval and resumes at L3.
- Leadership revenue is final Won value; SQL conversion is approved ÷ submitted; assignment time is approval to first assignment. Currency remains provisional.

The corresponding durable decisions live in [`adr/`](adr/); use a new ADR when a confirmed answer changes architecture or product rules.
