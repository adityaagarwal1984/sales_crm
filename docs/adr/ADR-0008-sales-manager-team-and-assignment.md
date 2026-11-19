# ADR-0008: Scope Sales Manager Work to Assigned Teams and Preserve AE Assignment History

- **Status:** Implemented with metric assumptions pending confirmation
- **Date:** 2026-11-19

## Context

Block 5 requires a Sales Manager dashboard, team performance, approved SQL assignment to AEs, reassignment, assignment history, and in-app AE notification. The identity module previously had no team relation, and there were no opportunity, meeting, or notification records. The business has not yet supplied customer expiry fields, a conversion denominator, or a currency standard.

## Decision

- Add nullable `manager_id` to `app_user`. Admins link BDR and AE accounts to a Sales Manager from Users & roles. A nullable link permits existing accounts to migrate; unassigned legacy users do not appear in manager team views until linked.
- Scope Sales Manager SQL queues/details, assignments, opportunities, AE choices, and performance metrics to users whose manager link matches the authenticated manager. A manager cannot access another manager's opportunity or SQL by identifier.
- Create one `sales_opportunity` per approved SQL, enforced by a unique database constraint. Copy the SQL estimated value to the opportunity and default currency to INR pending company direction.
- Only active AEs assigned to the same manager can receive assignment. Reassignments are allowed only while opportunity status is `ACTIVE`, and assigning to the existing AE is rejected as a duplicate. `WON`/`LOST` outcomes are terminal in this module.
- Append an immutable assignment-history row for initial assignment and each reassignment. Save an in-app notification for the newly assigned AE; notifications are private to their recipient and can be marked read.
- Add append-only completed meeting activity and AE-owned outcome APIs so the requested performance metrics have persisted sources. This is the minimal activity foundation, not the complete AE pipeline.
- Calculate team conversion provisionally as `won opportunities / all assigned opportunities`. Count revenue as the total copied opportunity value for WON opportunities, provisionally in INR.
- Return no numeric expired-customer count (`null`) and show “Not configured” in the dashboard until customer status/expiry semantics and source data are defined.

## Alternatives considered

- Organization-wide manager access: rejected because the user requires team-level data access.
- Add a separate Team entity: deferred because each employee currently needs one manager and no team metadata or hierarchy was requested. A direct manager link is sufficient for the present boundary.
- Allow assignment to any active AE: rejected because it could cross manager team boundaries.
- Create a new opportunity for each reassignment: rejected because this duplicates the same sales pursuit. The stable opportunity is reassigned and history retains each owner period.
- Use an event bus or external notification service: rejected because there is one synchronous in-app recipient and no external integration requirement.
- Implement customer expiry from an assumed organisation field: rejected because the app has no customer/account lifecycle, contract term, or expiry source.

## Implementation

- **Backend identity:** `UserEntity`, `AdminUserService`, `UserRepository`; Liquibase changeset `005-sales-manager-team-opportunities` adds the manager self-reference. Admin API accepts `managerId`.
- **Backend sales:** `OpportunityEntity`, `OpportunityStatus`, `OpportunityAssignmentHistoryEntity`, `OpportunityMeetingEntity`, `AppNotificationEntity`, their repositories, `OpportunityService`, `AppNotificationService`, `SalesManagerDashboardController`, `AeOpportunityController`, and `NotificationController`.
- **Security:** `SecurityConfiguration` restricts manager endpoints to SALES_MANAGER and AE endpoints to AE; services enforce team and assignment ownership for record-level access.
- **Frontend:** `SalesManagerDashboardPage.jsx`, `SalesManagerAssignmentsPage.jsx`, `TeamPerformancePage.jsx`, `AeOpportunitiesPage.jsx`, manager/AE role navigation and routes, functional notifications in `TopBar.jsx`, and administrator team assignment controls in `UserManagementPage.jsx`.
- **Tests:** `Block5SalesManagerIntegrationTest` covers approval requirement, inactive-AE rejection, duplicate assignment prevention, reassignment history, AE visibility, notifications/read receipts, meeting logging, Won revenue, manager scope, and role denial.
- **Actual example:** `OpportunityService.assignApprovedSql()` checks qualification status, verifies both AE activity and shared manager, creates or reassigns the unique opportunity, appends history, and notifies the receiving AE within the same transaction.

## Benefits and trade-offs

- Team scope is enforced in the API/service layer, not only in frontend navigation. Manager reports and actions share the same boundary.
- Assignment history is durable while an opportunity retains one current owner; inactive AEs cannot receive new assignments.
- Notifications persist across sessions and carry an in-app destination.
- Nullable manager links make migration safe but require administrators to assign legacy users before they appear in manager reports.
- INR, conversion formula, terminal outcome states, and the minimal meeting log are explicit defaults pending tech-lead confirmation. The expired-account metric is intentionally not fabricated.
- Current team performance uses per-member aggregate queries; replace with grouped reporting if team size or traffic makes that costly.

## Consequences

- Liquibase changeset 005 runs on backend startup. Existing BDR and AE users must be linked to a Sales Manager in Users & roles for team visibility.
- Full AE stage, stakeholder, L1/L2/L3, and handover workflows remain a separate block.
- Confirm currency, conversion denominator, Won revenue definition, account expiry data, and whether outcome transitions may be reopened before production use.
