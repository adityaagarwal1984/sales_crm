# ADR-0010: Controlled AE Opportunity Lifecycle

- **Status:** Accepted and implemented
- **Date:** 2026-11-19
- **Category:** Domain workflow / modular monolith

## Context

Block 6 requires account executives to manage assigned opportunities through meetings, stakeholders, pipeline stages, close outcomes, and implementation handover. Reopen decisions must be auditable and approved by a Sales Manager.

## Decision

Use `L1 → L2 → L3 → WON/LOST`. AE stage changes are forward-only; skipping an open stage requires a reason, and terminal close is allowed only from L3. Meeting type/level is stored independently from opportunity stage; a completed matching L1 or L2 meeting advances the current stage. WON requires final deal value and commercial details and creates a handover; LOST requires a reason and closure notes. An AE requests reopening and a Sales Manager approves or rejects; approved reopening restores L3. Stage history, closure snapshots, reopen decisions, and handovers are separate append-only records.

## Rationale and alternatives

- **Selected:** Explicit lifecycle service and status enum keep the fixed transition policy visible; history records preserve audit context across reopening.
- **Alternatives:** Mutable status fields only would lose prior closes; a class per State would add indirection for this short fixed pipeline; unrestricted reopen would bypass manager governance.

## Implementation

`OpportunityLifecycleService`, `OpportunityStatus`, `OpportunityStageHistoryEntity`, `OpportunityClosureEntity`, `OpportunityReopenRequestEntity`, `OpportunityStakeholderEntity`, `ImplementationHandoverEntity`, `OpportunityMeetingEntity`, `AeOpportunityController`, and manager review routes; Liquibase changeset 006; React AE opportunity dashboard/list/detail and manager reopen queue.

## Consequences

Stage and close behavior is centralized and testable; prior closures remain visible and repeated Won outcomes produce distinct handovers. Meeting scheduling/calendar integration and reopen SLA/notification are outside this block. INR remains a display assumption pending official currency confirmation.

## Verification

`Block6AeLifecycleIntegrationTest` covers forward progression, skip reason, meeting/follow-up, stakeholder validation, close requirements, manager-approved reopen, repeated closure/history, multiple handovers, and role/ownership denial.
