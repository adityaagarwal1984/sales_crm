# Development Plan

**Status: Proposed sequence.** Do not begin a block until its business and technical prerequisites are sufficiently clear and the prior block has no unresolved critical issues.

| Block | Focus | Exit gate |
|---|---|---|
| 0. Discovery and approval | Resolve the blocking questions; confirm definitions, roles, MVP, identity, deployment, and first workflow | Tech lead / manager approves scope and initial decisions |
| 1. Foundation | Monorepo build setup, shared frontend shell, Spring Boot structure, PostgreSQL connection, Liquibase baseline, OpenAPI, error/validation conventions | Clean local setup and migration; documented smoke path |
| 2. Identity and authorization | Provisioning/authentication choice, role mapping, password/token lifecycle if applicable, server-side access rules | Positive and negative auth tests for all roles/scopes |
| 3. Organisation/contact work | Searchable organisation and contact records, duplication rules, ownership/audit | Agreed BDR workflow and persistence tests |
| 4. Activities and SQL submission/review | Calls/follow-ups, qualification fields, review states, assignment | BDR-to-manager workflow accepted end-to-end |
| 5. Opportunity execution | AE assignment, meetings, stakeholders, outcomes, Won handover | AE workflow and state transitions accepted |
| 6. Revival | Inactive definition, revival assignments and outcomes | Revival workflow and reporting definition accepted |
| 7. Targets and dashboards | Admin target management, manager/leadership analytics | Metrics reconcile to agreed business definitions |
| 8. Hardening and release | Accessibility, security, performance, operational deployment, backups, documentation | Release review and recovery readiness |

The order of Blocks 3–6 may change if the tech lead identifies a different MVP. Keep blocks small enough to review and demo.
