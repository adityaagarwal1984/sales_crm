# Design Pattern Strategy

Patterns are tools for recurring design problems, not required ingredients. This matrix distinguishes patterns present in the inspected code from patterns that may help only after a real business or integration need appears.

## Selection matrix

| Pattern | Category | Current status | CRM recommendation and trigger |
|---|---|---|---|
| Factory Method | Creational | Not used | Defer. Add only if the same domain object must be created through multiple validated workflows or providers. Ordinary constructors and Spring configuration are sufficient now. |
| Builder | Creational | Library use only | Nimbus `JwtClaimsSet.builder()` assembles token claims. Do not create project-specific builders for simple request records or entities. Consider one only for a complex immutable query/report or object with many optional parts. |
| Singleton | Creational | Spring-managed scope | `@Service`/`@Repository` beans are singleton-scoped by default. Keep them stateless and use Spring lifecycle; do not write a manual singleton or store request/user state in a bean. |
| Adapter | Structural | Not used | Likely future seam for email, calendar, calling, lead-source, or implementation-system integrations. Add when a real provider/API is selected; keep its external payloads out of domain services. |
| Decorator | Structural | No app-owned decorator | Defer. Add around a stable contract only for a real cross-cutting capability such as metrics/retry/caching that can be independently composed. Axios interceptors are HTTP middleware, not evidence for a custom Decorator hierarchy. |
| Facade | Structural | Not used | Defer. Service methods already give controllers a use-case boundary. Add a facade only if a workflow coordinates multiple services and otherwise exposes too much orchestration detail. |
| Strategy | Behavioural | Not used | Candidate for qualification, assignment, revival eligibility, or commission policy only if the business confirms multiple interchangeable rule sets. Start with a direct service method while there is one rule. |
| Observer | Behavioural | Framework-managed UI state only | React Context updates `useAuth()` consumers when auth state changes. No domain observer/event bus exists. Use domain events only when several independent reactions need to follow a committed business event. |
| State | Behavioural | Not used | Do not introduce yet; no workflow entities exist. For SQL/opportunity lifecycle, begin with an enum plus explicit transition validation. Use State objects only if state-specific behavior becomes materially complex. |
| Template Method | Behavioural | Not used | No family of algorithms shares fixed steps with variable hooks. Reassess only if repeated import/report pipelines emerge with a common invariant sequence. |
| Chain of Responsibility | Behavioural | Framework pipeline used | `SecurityConfiguration` registers `JwtAuthenticationFilter` in Spring Security's filter chain. Keep filters focused on HTTP authentication concerns; do not create a second generic handler chain for business rules. |
| Specification | Behavioural | Capability present, not used | `UserRepository` extends `JpaSpecificationExecutor`, but there are no `Specification<UserEntity>` objects. Remove the unused extension until approved dynamic filters justify it. For a few fixed filters, prefer explicit repository methods. |
| MVC | Architectural | Implemented | Spring Web controllers map HTTP requests to DTOs and services. React supplies browser views. Keep API controllers thin and use the backend—not the React route guard—for access control. |
| Repository | Architectural | Implemented | Spring Data `UserRepository` and `RoleRepository` provide persistence operations. Keep query concerns there; don't add repository interfaces around every utility. |
| Service Layer | Architectural | Implemented | `AuthService` and `AdminUserService` contain use-case orchestration and transactions. Put business rules here/domain operations, not in controllers or React components. |
| DTO | Architectural | Implemented | Request/response records carry API data separately from JPA entities. Keep credentials and hashes out of response DTOs. Some records currently live inside controllers/services; move them to a feature API package as contracts grow. |
| Dependency Injection | Architectural | Implemented | Spring constructor injection supplies repositories, encoders, and JWT services. Prefer constructor injection and avoid service locator/static access. |
| Modular Monolith | Architectural | Implemented and required | One Spring Boot deployment with packages grouped by capability; React remains a separate frontend application. Preserve module seams and do not introduce microservices for upcoming blocks. |
| Domain Events | Architectural technique | Not used | Defer. Consider after a confirmed workflow event has multiple independent consumers or asynchronous side effects. If delivery must survive process failure, use an outbox rather than relying on in-memory publication alone. |

## Pattern selection rules for upcoming blocks

1. State the concrete problem and the simpler direct implementation first.
2. Confirm that the problem is recurring or has genuine variants before selecting a pattern.
3. Keep pattern scope inside the owning module; don't add a global framework prematurely.
4. Record the chosen pattern, alternatives, touched files, example, benefits, and trade-offs in [the implementation log](PATTERN-IMPLEMENTATION-LOG.md).
5. If no pattern is needed, record “no pattern; direct approach selected” and why.

## Review notes

- The current application is intentionally small. Direct constructor injection, feature services, JPA repositories, and explicit role checks are adequate.
- The presence of `JpaSpecificationExecutor` alone does not mean Specification is implemented. It is currently unused capability and should not be expanded without approved search/filter requirements.
- The React role configuration is data-driven but is not a full Strategy or plugin system.
- Do not turn the supplied business statuses into State-pattern classes until actual transition rules and variation justify that extra type structure.
