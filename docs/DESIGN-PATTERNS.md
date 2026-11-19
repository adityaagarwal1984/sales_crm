# Pattern choices

Patterns are tools for recurring problems, not a checklist. These are the patterns actually used or intentionally rejected in the current CRM.

| Pattern / technique | Category | Current use | Why it fits / trade-off |
|---|---|---|---|
| MVC / REST | Architecture | Spring controllers expose `/api/v1` resources and actions; services own rules. | Clear React/backend contract. DTO mapping and API versioning need maintenance. |
| Service Layer | Architecture | Feature services coordinate validation, authorization, transactions, and persistence. | Keeps rules out of controllers; cohesive services can grow too large if capability boundaries blur. |
| Repository | Architecture | Spring Data JPA repositories; explicit SQL for analytics aggregates. | Isolates persistence; ORM is less suitable for some reporting queries. |
| DTO | Architecture technique | Request/response records separate APIs from entities. | Controls validation and prevents password hash/entity leakage; requires mapping. |
| Dependency Injection | Creational technique | Spring constructor-injects collaborators. | Explicit dependencies and test seams; no need for one interface per class. |
| Modular monolith | Architecture | One deployable app with identity, organisation, prospecting, sales, analytics packages. | Local transactions and simple operations; module boundaries need discipline as team/scale grows. |
| Chain of Responsibility | Behavioral/framework | Spring Security filter chain includes `JwtAuthenticationFilter`. | Uses established authentication pipeline; filter ordering must be understood. |
| Builder | Creational/library usage | Nimbus `JwtClaimsSet.builder()` creates JWT claims. | Clear named claim construction; library usage, not a custom builder framework. |
| Append-only history | Data/audit technique | Qualification, assignment, stage, closure and reopen actions create history records. | Preserves actor/time/reason; increases storage and requires consistent transactional writes. |
| Read-model aggregation | Query technique | Leadership service uses SQL aggregates and separate counts. | Efficient dashboard queries and avoids join fan-out; formulas must remain documented. |

## Pattern candidates evaluated

| Pattern | Decision | Add it when… |
|---|---|---|
| Factory Method | Not used | There are multiple creation flows selected by type/provider. One current workflow does not justify a factory. |
| Project-specific Builder | Not used | Construction has many optional fields/invariants; simple DTO/entity construction is adequate. |
| Manual Singleton | Not used | Spring already manages stateless service bean lifecycle. Never put request state in singleton beans. |
| Adapter | Not used | A real telephony, email, calendar, or external CRM provider is selected and needs a stable boundary. |
| Decorator | Not used | Cross-cutting behavior needs composition around multiple implementations; current filters/interceptors suffice. |
| Facade | Not used | A stable simplified API is needed over a genuinely complex subsystem. |
| Strategy | Not used | Qualification, assignment, or scoring has multiple interchangeable approved policies. |
| Observer / Domain Events | Not used | Multiple independent consumers need a committed domain event; current notifications are transactional direct writes. |
| State | No state classes | Current SQL and opportunity lifecycles have small explicit enums/transitions. Add state objects only if behavior becomes substantially state-specific. |
| Template Method | Not used | Several workflows share a stable algorithm with deliberate variable steps. |
| Specification | Not used for CRM policy | Current list filters are fixed and explicit. Use composable specifications only if filter combinations grow. |
| Domain Events / outbox | Not used | Asynchronous or cross-service delivery requires reliable post-commit publication. Current single-app workflows are synchronous. |

### How to explain the choice

“I selected the smallest pattern that addresses a current requirement. The CRM uses standard MVC, services, repositories, DTOs, dependency injection, and a modular monolith. It uses explicit enum transition checks instead of a State class hierarchy because the current workflow is small. I would introduce Strategy or an Adapter when multiple real policies/providers exist.”

See [Pattern Implementation Log](PATTERN-IMPLEMENTATION-LOG.md) for feature-specific examples and [ADRs](adr/) for decisions/trade-offs.
