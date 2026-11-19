# Development Plan

**Status: Proposed sequence.** Do not begin a block until its business and technical prerequisites are sufficiently clear and the prior block has no unresolved critical issues.

| Block | Focus | Exit gate |
|---|---|---|
| 0. Discovery and approval | Resolve the blocking questions; confirm definitions, roles, MVP, identity, deployment, and first workflow | Tech lead / manager approves scope and initial decisions |
| 1. Foundation | Frontend UI foundation completed in this block; remaining backend foundation includes Spring Boot structure, PostgreSQL connection, Liquibase baseline, OpenAPI, and error/validation conventions | UI build and dashboard routes verified; backend foundation remains a separate approved sub-block |
| 2. Identity and authorization | Implement JWT authentication, first-admin bootstrap, one-role accounts, and admin user management | Auth/permission edge-case tests pass; migrations and frontend flow verified against PostgreSQL before production handoff |
| 3. Organisation/contact work | Searchable organisation and contact records, duplicate rules, creator traceability | Tech-lead review of provisional access/duplicate defaults and persistence tests |
| 4. BDR prospecting and SQL qualification | Calls/follow-ups, qualification fields, manager review states | BDR-to-manager qualification flow verified |
| 5. Sales Manager | Organization-wide SQL review, AE assignment/reassignment, assignment history, notifications, and team performance | Assignment, security, metrics, and workflow verified |
| 6. AE opportunity execution | Meetings, stakeholders, L1/L2/L3 stages, Won/Lost, handover, manager-approved reopen | AE workflow and state transitions verified |
| 7. Revival | Inactive definition, revival assignments and outcomes | Revival workflow and reporting definition accepted |
| 8. Leadership dashboards | Leadership analytics and filters (target management remains separate) | Aggregates, filters, security, and KPI formulas verified |
| 9. Hardening and release | Accessibility, security, performance, operational deployment, backups, documentation | Release review and recovery readiness |

The order of Blocks 3–6 may change if the tech lead identifies a different MVP. Keep blocks small enough to review and demo.

**Current implementation status:** Blocks 0–6 and 8 are implemented. Block 7 revival and target management are pending.
