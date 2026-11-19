# Pattern Implementation Log

This log distinguishes code the project actually uses from patterns that are recommendations only. Update it in each future block after implementation, not merely when a pattern is proposed.

## Implemented patterns and architectural techniques

### Modular Monolith

- **Category:** Architectural pattern
- **Problem solved:** Deliver one backend without operational overhead while retaining internal capability boundaries.
- **Why selected:** The project brief explicitly requires it; Blocks 0–2 are a small team/product baseline.
- **Alternatives considered:** Microservices (unnecessary deployment/communication complexity); one flat package (too little internal boundary).
- **Classes/files:** `backend/src/main/java/com/prosperr/crm/CrmApplication.java`; packages `identity/` and `security/`; [`ADR-0001`](adr/ADR-0001-modular-monolith.md).
- **Implementation example:** `identity/api` → `identity/service` → `identity/repository` → `identity/domain`; one Spring Boot application scans and runs them together.
- **Benefits:** One deployable backend and transaction boundary; capability seams remain visible.
- **Trade-offs:** A monolith can become coupled if modules call each other's internals. Future business capabilities should own their policies and expose small internal service interfaces only where needed.

### MVC (REST presentation)

- **Category:** Architectural pattern
- **Problem solved:** Separate HTTP routing/representation from business behavior.
- **Why selected:** Spring Web provides a familiar request/response boundary for the React SPA.
- **Alternatives considered:** Server-rendered templates (not aligned with the separate React frontend); controllers calling repositories directly (too much persistence coupling).
- **Classes/files:** `identity/api/AuthController.java`, `identity/api/AdminUserController.java`, `security/SecurityConfiguration.java`.
- **Implementation example:** `AuthController.login()` accepts a validated request record and delegates to `AuthService.login()`.
- **Benefits:** Explicit route contract; easier API-level testing.
- **Trade-offs:** DTOs and route definitions require maintenance; controller remains framework-coupled and should stay thin.

### Repository

- **Category:** Architectural pattern
- **Problem solved:** Isolate persistence queries from use-case code.
- **Why selected:** Spring Data JPA supports the current PostgreSQL relational model without custom DAO boilerplate.
- **Alternatives considered:** Direct `EntityManager` use in services (more persistence coupling); in-memory collections (not a durable CRM source).
- **Classes/files:** `identity/repository/UserRepository.java`, `RoleRepository.java`.
- **Implementation example:** `UserRepository.findByEmailIgnoreCase()` and `existsByRole_Code()` are called by identity services.
- **Benefits:** Query contract is explicit and unit-of-work behavior comes from Spring Data.
- **Trade-offs:** Derived query names can become unwieldy. `JpaSpecificationExecutor` is currently unused and should be removed until real dynamic filters are introduced.

### Service Layer

- **Category:** Architectural pattern
- **Problem solved:** Coordinate authentication/user operations, domain rules, and transaction boundaries away from HTTP code.
- **Why selected:** Admin and auth flows have rules beyond persistence and are invoked from REST controllers.
- **Alternatives considered:** Business logic in controllers (transport coupling); a separate generic use-case class for every one-line method (unnecessary indirection now).
- **Classes/files:** `identity/service/AuthService.java`, `AdminUserService.java`, `AdminBootstrap.java`.
- **Implementation example:** `AdminUserService.deactivate/status` protects the final active administrator; `AuthService.changePassword()` validates the current credential and changes the forced-change flag in a transaction.
- **Benefits:** Business rules can be tested without a browser; transaction ownership is visible.
- **Trade-offs:** Keep services cohesive and avoid a single service that spans unrelated capabilities.

### DTO (Data Transfer Object)

- **Category:** Structural/architectural technique
- **Problem solved:** Prevent persistence entities and sensitive fields from becoming the public API contract.
- **Why selected:** User/password data needs a strict allowlist for responses and a stable transport shape.
- **Alternatives considered:** Serialize JPA entities (lazy-loading, accidental-field exposure, persistence/API coupling).
- **Classes/files:** Request/response records in `identity/api/AuthController.java`, `identity/service/AuthService.java`, and `identity/service/AdminUserService.java`.
- **Implementation example:** `AuthService.UserView` includes identity, role, active state, and password-change status but excludes `passwordHash`.
- **Benefits:** Sensitive persistence fields remain out of responses; API can evolve separately from entity mappings.
- **Trade-offs:** Mapping code is required. Current records are nested in controller/service files; group them under a feature API package if the contract grows.

### Dependency Injection

- **Category:** Architectural technique
- **Problem solved:** Supply collaborators without hard-coding concrete construction in use-case code.
- **Why selected:** Spring Boot already manages repositories, services, encoders, and JWT components.
- **Alternatives considered:** Static/global service locators (hidden dependencies); manual construction throughout the application.
- **Classes/files:** Constructor-injected `AuthService`, `AdminUserService`, controllers, and configuration beans.
- **Implementation example:** `AuthService(UserRepository, PasswordEncoder, JwtService)` receives the dependencies it uses.
- **Benefits:** Dependencies are visible and replaceable in tests/configuration.
- **Trade-offs:** More wiring is possible; constructor injection is enough and does not justify an interface for each implementation.

### Singleton scope (Spring-managed)

- **Category:** Creational pattern
- **Problem solved:** Reuse stateless service/repository beans within the application context.
- **Why selected:** Spring's default bean scope is sufficient for stateless application services.
- **Alternatives considered:** Manual singleton `getInstance()` (hidden lifecycle); prototype/request scope (not needed for stateless services).
- **Classes/files:** `@Service` beans such as `AuthService`, `AdminUserService`, `JwtService`; Spring Data repositories.
- **Implementation example:** Spring creates one `AuthService` bean and injects it into the controller.
- **Benefits:** Central lifecycle and low-cost shared collaborators.
- **Trade-offs:** Never store request-specific mutable state in these beans; concurrency bugs become possible otherwise. This is framework-managed scope, not a manually implemented singleton class.

### Chain of Responsibility (Spring Security filter chain)

- **Category:** Behavioural pattern, framework pipeline
- **Problem solved:** Run authentication and security checks in a defined HTTP filter order.
- **Why selected:** Spring Security supplies the established filter pipeline; the CRM needs one custom JWT authentication step.
- **Alternatives considered:** Hand-written authorization middleware or a second business-rule chain (more fragile and duplicated).
- **Classes/files:** `security/SecurityConfiguration.java`, `identity/security/JwtAuthenticationFilter.java`.
- **Implementation example:** `JwtAuthenticationFilter` is registered before `UsernamePasswordAuthenticationFilter`, decodes a Bearer token, loads the active user, and passes control onward.
- **Benefits:** Fits Spring Security's lifecycle and separates transport authentication from controller logic.
- **Trade-offs:** Filter order and exception entry points can be subtle; protect it with API integration tests and avoid adding general-purpose filters without a concrete cross-cutting requirement.

### Builder (third-party library usage only)

- **Category:** Creational pattern
- **Problem solved:** Assemble the immutable JWT claim set from named values.
- **Why selected:** Nimbus/Spring Security exposes a builder API for its claim type.
- **Alternatives considered:** Hand-building serialized JWT claims (unsafe and unnecessary).
- **Classes/files:** `identity/security/JwtService.java` uses `JwtClaimsSet.builder()`.
- **Implementation example:** `JwtClaimsSet.builder().issuer(...).issuedAt(...).expiresAt(...).subject(...).build()`.
- **Benefits:** Named claim construction and library-managed serialization.
- **Trade-offs:** This is library-owned; it is not a project-specific Builder abstraction. Do not add builders for simple DTOs without a construction problem.

## Not implemented as custom abstractions

Factory Method, custom Adapter, custom Decorator, Facade, Strategy, domain Observer/event bus, State objects, Template Method, and Domain Events are not implemented. Their current fit and triggers are in [`DESIGN-PATTERNS.md`](DESIGN-PATTERNS.md). Do not list them as implemented merely because the frameworks expose related features.

## Block 3 — Organisation and Contact Management (2026-11-19)

**Pattern decision:** No new named design pattern was needed. The simple, existing modular-monolith layers were reused.

- **Problem:** Persist and manage organisations and people, support search/pagination, and avoid treating a person as a sales pursuit.
- **Selected approach:** Feature-scoped MVC/REST controllers, a cohesive service for validation/use-case orchestration, Spring Data repositories for persistence queries, DTOs for API boundaries, constructor injection, Liquibase-owned PostgreSQL schema, and explicit role authorization.
- **Alternatives considered:** Generic repository/service abstractions (no demonstrated second implementation); Specification objects for every field (fixed filters fit one JPQL query per repository); Strategy/Factory (one creation flow); domain events (no independent consumers); event bus/outbox (no asynchronous side effects); ownership framework (ownership behavior not specified).
- **Classes/files:** `organisation/api/OrganisationController.java`, `ContactController.java`, `OrganisationDtos.java`; `organisation/service/OrganisationService.java`; `organisation/repository/OrganisationRepository.java`, `ContactRepository.java`; `organisation/domain/OrganisationEntity.java`, `ContactEntity.java`; `db/changelog/db.changelog-master.yaml`; React pages under `frontend/src/pages/organisation/` and reusable forms under `frontend/src/components/organisation/`.
- **Implementation example:** `OrganisationController.search()` passes filters and page parameters to `OrganisationService.search()`, which invokes the repository query and maps entities to `OrganisationSummary`; `OrganisationService.create()` applies the duplicate rule and derives `createdBy` from the authenticated principal.
- **Query behavior:** Contact counts for a page are loaded with one grouped repository query and mapped in memory, avoiding one count query per listed record.
- **Benefits:** Responsibilities remain in the established layers; entities and user security data do not leak through API responses; the service can enforce duplicate rules and transaction boundaries; page size and role access are bounded.
- **Trade-offs:** The module uses explicit query predicates and role rules. A future more complex filter grammar, ownership model, lifecycle, or integrations may justify other patterns after requirements emerge.
- **Tests:** `Block3OrganisationIntegrationTest` covers BDR authorization, non-BDR rejection, create/detail/update, search/pagination, normalized duplicates, creator traceability, and validation.
- **Assumptions for review:** Shared BDR/admin access; free-text industry/company-size/lead-source; trimmed case-insensitive organisation-name deduplication; contact-email deduplication per organisation.

## Future entry template

For every pattern newly selected, add a section with:

- Pattern name and category.
- Concrete problem and why selected.
- Alternatives considered, including the simpler approach.
- Exact classes/files and a short real implementation example.
- Benefits and trade-offs.
- Tests demonstrating the problem/behavior is addressed.

For a block with no new pattern, add a dated note naming the direct approach and why a pattern would add complexity without value.

## Block 4 pattern decision

### Service Layer, Repository, DTO, Dependency Injection, and MVC

- **Category:** Architectural patterns and techniques
- **Problem solved:** Keep prospecting HTTP contracts, persistence, business rules, and database models separate.
- **Why selected:** Calls, ownership checks, follow-up lifecycle, and cross-entity validation require cohesive use-case logic and transactions. The existing modular-monolith layers already provide a consistent solution.
- **Alternatives considered:** Business rules in controllers, direct repository use from React-facing controllers, or introducing a strategy/event framework. These either couple concerns or add abstractions without a current variability/consumer requirement.
- **Classes/files:** `prospecting/api/*Controller.java`, `prospecting/service/ProspectingService.java`, `prospecting/repository/*Repository.java`, `prospecting/domain/*Entity.java`, and `prospecting/api/ProspectingDtos.java`.
- **Actual example:** `ProspectingService.completeFollowUp()` appends a new `CallActivityEntity`, updates the lead's latest-call summary, marks the follow-up completed with the new call reference, and optionally creates another follow-up in one transaction.
- **Benefits:** Call history is append-only, follow-up state is queryable, and ownership/organisation rules are enforced server-side.
- **Trade-offs:** `ProspectingService` currently spans the four closely related prospecting entities. Split it only if upcoming review/assignment workflows create genuinely separate responsibilities.

No Strategy, State, Factory, Observer, Specification, or event-bus pattern was added. The call outcome set and follow-up transition rules are currently fixed and small; simple enums and explicit service methods are clearer until requirements show real policy variation or event consumers.

## Block 4 SQL qualification review (2026-11-19)

**Pattern decision:** Continue the existing MVC, Service Layer, Repository, DTO, and Dependency Injection structure. No additional design pattern was needed.

- **Problem:** Capture qualification details and manage draft, submission, manager approval/rejection, and resubmission while preserving every decision.
- **Selected approach:** A dedicated qualification aggregate, explicit status enum, transactional service methods, one-to-one lead constraint, and append-only snapshot history.
- **Alternatives considered:** Fields on the lead, reusing the initial minimal SQL submission table, creating rows per retry, or introducing a generic State/Strategy/event framework. Those options either mix lifecycles, fail to hold history, allow duplicate tracks, or add complexity without multiple state policies.
- **Classes/files:** `SqlQualificationEntity`, `SqlQualificationHistoryEntity`, `SqlQualificationStatus`, both qualification repositories, `SqlQualificationService`, both qualification controllers, Liquibase changeset 004, and React qualification/review pages.
- **Actual example:** `SqlQualificationService.reject()` checks the `SUBMITTED` state, requires a reason, records the reviewer and instant, and appends a snapshot event in one transaction. Saving a rejected qualification returns it to draft but does not remove the rejection event.
- **Benefits:** BDR drafts are private, manager decisions are traceable, resubmissions do not overwrite history, and approved SQLs can be assigned downstream without assigning them here.
- **Trade-offs:** One qualification per lead prevents parallel qualification tracks; history snapshots consume storage. No detailed scoring policy or currency is assumed.
- **Tests:** `Block4ProspectingIntegrationTest` exercises duplicate prevention, form/link validation, rejection feedback, resubmission, approval actor/time, eligibility, and authorization.

See [ADR-0007](adr/ADR-0007-sql-qualification-review.md).

## Block 5 Sales Manager module (2026-11-19)

**Pattern decision:** Reuse MVC, Service Layer, Repository, DTO, and constructor injection. No additional named design pattern was needed.

- **Problem:** Restrict manager work to their team, assign approved SQLs to active AEs, support reassignment, retain history, and provide a real in-app notification and performance view.
- **Selected approach:** A direct optional `manager_id` user relationship, one opportunity per approved SQL, an append-only assignment audit table, a recipient-owned notification table, and explicit service authorization/state checks.
- **Alternatives considered:** Organization-wide manager queries, unrestricted AE assignment, a new opportunity row on every reassignment, a standalone Team aggregate, or a general event-bus/notification framework. The first three violate access/history/duplicate requirements; no multi-level hierarchy or independent event consumer exists to justify the latter abstractions.
- **Classes/files:** `UserEntity`, `AdminUserService`, `OpportunityEntity`, `OpportunityAssignmentHistoryEntity`, `OpportunityMeetingEntity`, `AppNotificationEntity`, their repositories/services/controllers, changeset 005, and React manager/AE pages and TopBar notifications.
- **Actual example:** `OpportunityService.reassign()` verifies SALES_MANAGER access, active-AE status, and ACTIVE opportunity state, changes the current assignee, appends the prior/new owners and actor, and writes a recipient notification transactionally.
- **Benefits:** Cross-team reads are denied server-side, a pursuit cannot have duplicate active opportunity rows, and assignment changes remain reviewable. Managers see BDR discovery metrics and AE meeting/win/revenue metrics from stored records.
- **Trade-offs:** All Sales Managers can access every BDR and AE record. INR and won/assigned conversion are provisional, and expired customer accounts are not numeric until the customer lifecycle is specified.
- **Tests:** `Block5SalesManagerIntegrationTest` covers assignment constraints, inactive AE denial, history, notifications, cross-manager visibility, activity metrics, and revenue.

See [ADR-0008](adr/ADR-0008-sales-manager-team-and-assignment.md).

## Block 6 AE opportunity lifecycle (2026-11-19)

**Pattern decision:** Reuse MVC, Service Layer, Repository, DTO, and constructor injection. Use enums and explicit transactional lifecycle operations rather than a class hierarchy per state.

- **Problem:** Enforce AE ownership, stage progression, mandatory close data, immutable event history, and manager-reviewed reopening.
- **Selected approach:** `OpportunityLifecycleService` validates transitions; separate append-only entities record stage changes, closure snapshots, and reopen decisions. Meeting completion and stage changes are coordinated in the same application service. Database uniqueness prevents duplicate stakeholder role links; handovers are per Won closure.
- **Alternatives considered:** State pattern classes per stage (only five fixed states and a linear flow, so more indirection than value); mutable status-only updates (lose auditability); event bus/domain events (no independent consumers); unrestricted reopening (contradicts approved business rule).
- **Classes/files:** `sales/service/OpportunityLifecycleService.java`, `OpportunityEntity`, `OpportunityStageHistoryEntity`, `OpportunityClosureEntity`, `OpportunityReopenRequestEntity`, `OpportunityStakeholderEntity`, `ImplementationHandoverEntity`, meeting entities/repositories, `AeOpportunityController`, manager reopen routes, changeset 006, and `frontend/src/pages/ae/`.
- **Actual example:** On approval, manager review records reviewer/time and appends a transition from terminal status to L3. Won/Lost closure snapshots are preserved even if the opportunity later reopens; a subsequent Won creates a separate handover.
- **Benefits:** Stage and closure history remain reviewable, invalid transitions are blocked in the service, and access is checked against current AE assignment.
- **Trade-offs:** Explicit transition logic grows as rules evolve; manager review has no SLA or notification yet. Meeting records represent completed meetings; scheduling is outside this block.
- **Tests:** `Block6AeLifecycleIntegrationTest` covers stage progression, skip reasons, stakeholders, meetings/follow-ups, Won/Lost, re-open approval, repeat close history, handovers, and access control.

## Block 8 Leadership analytics (2026-11-19)

**Pattern decision:** Use a read-only analytics service with purpose-built SQL aggregates; no generic reporting framework or event-driven materialized projections.

- **Problem:** Provide organization-wide dashboard metrics and filters without counting joins (such as multiple contacts) more than once.
- **Selected approach:** `LeadershipAnalyticsService` runs separate named-parameter aggregate queries and assembles API DTOs. KPI formulas are fixed and documented: final Won deal value for revenue; approved SQLs ÷ submitted SQLs for SQL conversion; average approval-to-first-assignment duration for assignment time.
- **Alternatives considered:** JPA entity graph followed by in-memory grouping (unbounded and duplicate-prone); one broad multi-join query (fan-out miscounts); materialized views/event projections (no volume or latency requirement yet).
- **Classes/files:** `analytics/service/LeadershipAnalyticsService.java`, `analytics/api/LeadershipAnalyticsController.java`, `SalesDtos`/analytics DTOs, security configuration, `frontend/src/pages/leadership/LeadershipDashboardPage.jsx`.
- **Actual example:** Overview counts organisations and contacts independently; funnel and revenue aggregations query their respective event/source tables and are combined only in the response, preventing contact joins from multiplying company counts.
- **Benefits:** Dashboard values come from PostgreSQL-backed data; filters and aggregation boundaries are explicit; endpoints are read-only and role-protected.
- **Trade-offs:** Queries are hand-authored and need review as schema/volume changes. Revival performance remains unconfigured until Block 7 defines revival records.
- **Tests:** `Block8LeadershipAnalyticsIntegrationTest` covers seeded end-to-end metrics, filters, revenue/funnel, and non-Leadership denial.
