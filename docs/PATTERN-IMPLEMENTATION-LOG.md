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

## Not implemented in Blocks 0–2

Factory Method, custom Adapter, custom Decorator, Facade, Strategy, domain Observer/event bus, State objects, Template Method, and Domain Events are not implemented. Their current fit and triggers are in [`DESIGN-PATTERNS.md`](DESIGN-PATTERNS.md). Do not list them as implemented merely because the frameworks expose related features.

## Future entry template

For every pattern newly selected, add a section with:

- Pattern name and category.
- Concrete problem and why selected.
- Alternatives considered, including the simpler approach.
- Exact classes/files and a short real implementation example.
- Benefits and trade-offs.
- Tests demonstrating the problem/behavior is addressed.

For a block with no new pattern, add a dated note naming the direct approach and why a pattern would add complexity without value.
