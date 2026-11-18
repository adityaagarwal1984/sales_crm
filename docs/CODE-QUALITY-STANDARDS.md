# Code Quality Standards

These standards apply to upcoming modules. They describe the target for new/changed code; they do not require a broad reformat or rewrite of stable Blocks 0–2.

## Backend (Java / Spring Boot)

- **Package by capability, layer within the capability.** Use `com.prosperr.crm.<capability>.api`, `.service`, `.domain`, and `.repository` where those layers are needed. Keep `common` limited to genuinely cross-capability infrastructure.
- **Thin controllers.** Parse/validate HTTP input, call a use case, and map the response. Do not put business rules, transactions, or report queries in controllers.
- **Service boundaries own use cases.** Use constructor injection, explicit transaction boundaries for writes, and domain operations that preserve invariants. Prefer package-private collaborators when no public contract is required.
- **Persistence stays behind repositories.** Use explicit query methods for a small number of known filters. Add `JpaSpecificationExecutor`/Specifications only for approved dynamic filtering; paginate database-backed collections.
- **Use request/response DTOs.** Never return JPA entities. Do not expose hashes, tokens, internal timestamps, or persistence-only fields unless the API requires them. Keep DTO validation at the HTTP boundary and business invariants in the domain/service.
- **Errors are safe and actionable.** Keep login errors generic; provide an operation-specific safe message for other failures. Avoid a catch-all response swallowing diagnostic context: log unexpected errors with a correlation/request ID, while excluding passwords, tokens, and personal data from logs.
- **Security is server-side.** Protect every endpoint by default; make public routes explicit. Do not trust frontend role checks. Validate JWT signature, expiry, issuer, and any configured audience. Keep secrets outside source control.
- **Schema changes use Liquibase.** Add an incremental, forward-only migration with every schema change. Check constraints, indexes, nullability, unique rules, and fresh-database startup. Never rely on Hibernate schema creation/update outside tests.
- **Code style.** Use one declaration/import per line, descriptive names, conventional Java formatting, small methods, and no compressed/minified Java source. Avoid static service locators and manual Singleton implementations.

## Frontend (React / JavaScript)

- Keep route configuration, auth/session state, API transport, feature pages, shared CRM components, and UI primitives in their existing separate areas.
- Pages coordinate a screen. When a page accumulates reusable API/state behavior, extract a feature hook or API module; do not create a generic hook for one trivial request.
- Use the shared Axios client and keep access tokens in memory. Treat `401`/`403` appropriately; do not use client route guards as backend authorization.
- Use shared `FormField`, `Modal`, `ConfirmationDialog`, `EmptyState`, `LoadingSkeleton`, and data components where their conventions fit. Prefer accessible dialogs over `window.prompt/alert` for multi-field editing.
- Use semantic HTML, associated labels, keyboard-accessible controls, visible focus, explicit loading/error/empty states, and responsive table overflow.
- Keep illustrative `dashboardData` out of authenticated/production API flows. Do not present placeholder metrics as CRM records.
- Format JSX and JavaScript as readable multi-line code. Keep business rules in services rather than duplicated in components.

## API and cross-layer conventions

- Agree on the route, request/response shape, authorization role/scope, validation, and error status before implementation.
- Normalize/canonicalize values once at the backend boundary and enforce canonical uniqueness in PostgreSQL where necessary.
- Never put admin-only or ownership rules only in navigation or disabled buttons.
- Use one clear owner for each rule; duplicate display config across frontend/backend is acceptable only when the server stays authoritative and the contract is tested.

## Testing expectations

For every block that changes behavior:

1. Add focused unit tests for pure/business rules and edge cases.
2. Add API/integration tests for authorization, validation, persistence, and transaction behavior.
3. Add frontend tests for the user-visible happy path, errors, and role/route behavior.
4. Run the existing frontend and backend suites after changes; report failures honestly.
5. Test Liquibase against PostgreSQL for database-specific migration behavior. H2 compatibility tests are useful but do not replace a PostgreSQL check.

Current gap: frontend tests cover route/auth basics, while one backend integration test exercises several identity flows. There is no dedicated static-analysis/format enforcement configured in the frontend or Maven build yet. Add tools after agreeing on team standards rather than silently changing the toolchain.

## Future-block review checklist

- [ ] Restate the confirmed business rule and unresolved questions.
- [ ] Identify the principle(s) that guide the boundary.
- [ ] Decide whether a named pattern solves a real problem; state why if none is needed.
- [ ] Define API, permission, data, validation, and error behavior.
- [ ] Add migration and tests with the feature.
- [ ] Record actual files/classes and pattern trade-offs in `PATTERN-IMPLEMENTATION-LOG.md`.
- [ ] Update ADRs and status docs.
- [ ] Present the reviewable implementation and test results before moving to the next block.

## ADR minimum fields

Each important architecture decision should record an ID/title, status, date, context, decision, alternatives, consequences, and affected modules. Supersede an ADR with a new record rather than silently rewriting history.
